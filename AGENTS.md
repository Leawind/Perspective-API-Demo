# Perspective API Demo Agent 指南

## 编码指南

### 代码格式

- Java
  - 遵循 `google-java-format` 规范（非强制要求）
  - 缩进：2 个空格
  - 大括号：K&R 风格（左括号不换行）
  - 导入语句：禁止使用通配符导入（如 `import java.util.*`）
- md、yaml、json 等文件用 deno 进行格式化：`deno fmt`

### 命名规范

- 模组 ID 使用小写蛇形命名：`perspective_api_demo`
- Mixin 类以 `Mixin` 为后缀，例如 `MinecraftMixin`
- 方法名和变量名使用小驼峰命名
- 常量使用全大写蛇形命名
- 表示角度或弧度的参数和变量名要用后缀表示其单位：`Deg` 是角度，`Rad` 是弧度

## 架构与依赖约束

本项目沿用 Perspective API 的事件桥接思路。`internal.bridge` 负责适配原版差异，`internal.logic` 实现 Demo 功能，`platform` 负责加载器差异，`internal.utils` 提供通用辅助功能。

### 核心约束

1. 事件驱动解耦：`bridge` 层的 Mixin 仅负责拦截原版调用并发射 `GameClientEvents` 等通用事件；具体业务行为由 `logic` 层监听事件后实现
2. 版本差异下沉：优先将 Minecraft 版本和加载器差异封装在 `bridge` 或 `platform` 层，避免业务逻辑散落 Stonecutter 宏
3. 加载器隔离：Fabric、Forge、NeoForge 专有代码只放在对应的 `platform` 子包中；共享调用通过 `platform.api` 抽象

## Minecraft 版本兼容性

当一个构建产物兼容连续的多个 Minecraft 版本时，应以其中最低版本作为开发和构建目标。模组元数据中的 Minecraft 版本要求应声明为 `>=` 该最低版本；发布平台上的额外版本标签则在对应变体的 `gradle.properties` 中通过 `publish.additionalMcVersions` 声明。

## Stonecutter 条件编译

当前激活的 Minecraft 版本可以在 `stonecutter.gradle.kts` 文件中找到。

在代码中可以根据 Minecraft 版本、加载器等条件编译代码，示例：

```java
/*? if >=26.1 {*/
@Unique private static final String SETUP_CAMERA_METHOD = "alignWithEntity";
/*? } else {*/
/*@Unique private static final String SETUP_CAMERA_METHOD = "setup";
*//*? } */
```

不符合当前条件的代码使用 `/*  */` 包裹。

尽量避免深层嵌套。如果必须嵌套，符合当前条件的代码中的条件仍然用 `/* */`，被注释的其他版本的代码中的条件用 `/^ ^/`。

例如，如果当前版本是 26.1 或以上：

```java
/*? if >=26.1 {*/
@Inject(method = SETUP_CAMERA_METHOD, at = @At("RETURN"))
private void beforeCameraUpdate(float partialTicks, CallbackInfo ci) {
  cameraSetupContext.setup((Camera) (Object) this, partialTicks);
}
/*? } else {*/
  /*@Inject(method = SETUP_CAMERA_METHOD, at = @At("RETURN"))
  private void beforeMoveCamera(
    /^? if >= 1.21.11 {^/
    net.minecraft.world.level.Level blockGetter,
    /^? } else {^/
    /^net.minecraft.world.level.BlockGetter blockGetter,
    ^//^? }^/
    net.minecraft.world.entity.Entity entity,
    CallbackInfo ci) {
    cameraSetupContext.setup((Camera) (Object) this, partialTicks);
  }
*//*? } */
```

### 风格

- 当需要使用 `else`、`else if` 时，尽量用 `>=` 条件，不要用 `<` 或 `<=`
- 如果一个方法体中使用了 Stonecutter 条件编译，且需要通过注释说明原因，应将该注释写在方法的 Javadoc 中，而不是方法体中

正确示例：

```java
/*? if >=1.21.11 {*/
return currentVersion().dataVersion().version();
/*? } else {*/
/*return currentVersion().getDataVersion().getVersion();
 *//*? }*/
```

错误示例：

```java
/*? if <1.21.11 {*/
/*return currentVersion().getDataVersion().getVersion();
 *//*? } else {*/
return currentVersion().dataVersion().version();
/*? }*/
```

### 格式化提示

合并相邻的条件编译块时（即 `*/` 后紧跟 `/*?`），应确保它们紧邻而非被空白分隔：

```
查找：(`\s|^)\*/(\s|\n)+/\*\?`
替换：`*//*?`
```
