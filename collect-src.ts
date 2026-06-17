#!/usr/bin/env -S deno run -A

import { Command } from "npm:commander";
import { minimatch } from "npm:minimatch";
import * as path from "jsr:@std/path";

// 定义程序配置
const program = new Command();

program
  .name("git-content-collector")
  .description("收集指定目录下所有被 Git 追踪的文件内容到单个文本文件中")
  .argument("<source-dir>", "项目根目录或包含 .git 的目录")
  .argument("<output-file>", "输出文本文件路径")
  .option("-b, --include-binary", "是否包含二进制文件内容 (默认跳过)", false)
  .option(
    "-i, --ignore <patterns...>",
    "要忽略的 glob 模式 (例如: 'node_modules/**', '**/*.log')。提供此选项将覆盖默认忽略列表。",
    [],
  )
  .parse([Deno.execPath(), ...Deno.args]);

const sourceDir: string = program.args[0];
const outputFile: string = program.args[1];
const includeBinary: boolean = program.getOptionValue("includeBinary");
const userIgnorePatterns: string[] = program.getOptionValue("ignore");

// 默认忽略的一些常见目录/文件
const DEFAULT_IGNORE_PATTERNS = [
  "**/node_modules/**",
  "**/dist/**",
  "**/build/**",
  "**/.git/**",
  "**/target/**", // Java/Maven
  "**/bin/**",
  "**/*.class",
  "**/*.jar",
  "**/*.war",
  "**/*.ear",
  "**/*.png",
  "**/*.jpg",
  "**/*.jpeg",
  "**/*.gif",
  "**/*.ico",
  "**/*.svg",
  "**/*.woff",
  "**/*.woff2",
  "**/*.ttf",
  "**/*.eot",
  "**/*.zip",
  "**/*.tar",
  "**/*.gz",
  "**/*.exe",
  "**/*.dll",
  "**/*.so",
  "**/*.dylib",
  "**/src/test/**"
];

// 如果用户提供了自定义忽略模式，则使用用户的；否则使用默认的
const finalIgnorePatterns = userIgnorePatterns.length > 0
  ? userIgnorePatterns
  : DEFAULT_IGNORE_PATTERNS;

/**
 * 获取指定目录下所有被 Git 追踪的文件相对路径
 */
async function getGitTrackedFiles(dir: string): Promise<string[]> {
  try {
    const command = new Deno.Command("git", {
      args: ["ls-files", "-z"], // 注意：git 命令是 ls-files
      cwd: dir,
      stdout: "piped",
      stderr: "piped",
    });

    const { code, stdout, stderr } = await command.output();

    if (code !== 0) {
      const errorText = new TextDecoder().decode(stderr);
      throw new Error(`Git command failed: ${errorText}`);
    }

    const output = new TextDecoder().decode(stdout);
    const files = output.split("\0").filter((f) => f.length > 0);

    return files.sort();
  } catch (e) {
    console.error(`Error getting git tracked files in ${dir}:`, e.message);
    throw e;
  }
}

/**
 * 简单检测是否为二进制文件
 * 检查前 8192 字节中是否包含空字节 (\0)
 */
function isBinary(content: Uint8Array): boolean {
  const checkLength = Math.min(content.length, 8192);
  for (let i = 0; i < checkLength; i++) {
    if (content[i] === 0) {
      return true;
    }
  }
  return false;
}

/**
 * 检查文件路径是否匹配任何忽略模式
 */
function isIgnored(relativePath: string, patterns: string[]): boolean {
  for (const pattern of patterns) {
    if (minimatch(relativePath, pattern)) {
      return true;
    }
  }
  return false;
}

async function main() {
  console.log(`Starting collection from: ${sourceDir}`);
  console.log(`Output will be saved to: ${outputFile}`);
  if (finalIgnorePatterns.length > 0) {
    console.log(`Ignoring patterns: ${finalIgnorePatterns.join(", ")}`);
  }

  // 检查源目录是否存在
  try {
    const stat = await Deno.stat(sourceDir);
    if (!stat.isDirectory) {
      console.error(`Error: ${sourceDir} is not a directory.`);
      Deno.exit(1);
    }
  } catch (e) {
    console.error(`Error: Cannot access directory ${sourceDir}. ${e.message}`);
    Deno.exit(1);
  }

  let trackedFiles: string[] = [];
  try {
    trackedFiles = await getGitTrackedFiles(sourceDir);
  } catch (_e) {
    console.error(
      "Failed to list git files. Ensure you are in a git repository.",
    );
    Deno.exit(1);
  }

  if (trackedFiles.length === 0) {
    console.warn("No git tracked files found.");
    await Deno.writeTextFile(outputFile, "");
    return;
  }

  console.log(`Found ${trackedFiles.length} tracked files before filtering.`);

  // 过滤掉忽略的文件
  const filteredFiles = trackedFiles.filter((file) =>
    !isIgnored(file, finalIgnorePatterns)
  );
  console.log(
    `${
      trackedFiles.length - filteredFiles.length
    } files ignored. Processing ${filteredFiles.length} files.`,
  );

  const allResults: string[] = [];
  let skippedCount = 0;

  for (const relativePath of filteredFiles) {
    const fullPath = path.join(sourceDir, relativePath);

    if (fullPath.indexOf("test") != -1) {
      console.log("Contains test: ", fullPath);
      const isi = isIgnored(fullPath, finalIgnorePatterns);
      console.log(`is ignored: ${isi}, patterns: ${finalIgnorePatterns}`);
    }

    try {
      const rawContent = await Deno.readFile(fullPath);

      if (isBinary(rawContent)) {
        if (!includeBinary) {
          skippedCount++;
          continue;
        } else {
          try {
            const text = new TextDecoder("utf-8", { fatal: true }).decode(
              rawContent,
            );
            allResults.push(
              `// Source: ${relativePath}\n${text}\n\n` + "---".repeat(20) +
                "\n\n",
            );
          } catch {
            allResults.push(
              `// [BINARY FILE CONTENT OMITTED]: ${relativePath}\n\n` +
                "---".repeat(20) + "\n\n",
            );
          }
          continue;
        }
      }

      const content = new TextDecoder("utf-8").decode(rawContent);

      allResults.push(`// Source: ${relativePath}\n\n`);
      allResults.push(content);
      allResults.push("\n\n" + "-".repeat(80) + "\n\n");
    } catch (e) {
      console.error(`Failed to process ${relativePath}: ${e.message}`);
      allResults.push(
        `// [ERROR READING FILE]: ${relativePath} - ${e.message}\n\n` +
          "-".repeat(80) + "\n\n",
      );
    }
  }

  const finalOutput = allResults.join("");

  try {
    await Deno.writeTextFile(outputFile, finalOutput);
    console.log(`Successfully wrote content to ${outputFile}`);
    if (skippedCount > 0) {
      console.log(`Skipped ${skippedCount} binary files.`);
    }
  } catch (e) {
    console.error(`Failed to write output file: ${e.message}`);
    Deno.exit(1);
  }
}

main();
