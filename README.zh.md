<div align="center">

<img src="src/main/resources/logo.128x.png" alt="Perspective API Demo" style="image-rendering:pixelated;height:8em;">

# 视角 API 演示 (Perspective API Demo)

中文 | [English](README.md)

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/cA3rwRVH?style=flat&logo=modrinth&color=17B85A&cacheSeconds=3600&label=Modrinth)](https://modrinth.com/mod/perspective-api-demo)
[![CurseForge Downloads](https://img.shields.io/curseforge/dt/1579305?style=flat&logo=curseforge&color=F1643%5E&cacheSeconds=3600&label=CurseForge)](https://www.curseforge.com/minecraft/mc-mods/perspective-api-demo)

一个 Minecraft 模组，基于[视角 API](https://github.com/Leawind/Perspective-API)实现了一些简单的功能。

</div>

## 视角

本模组添加了以下自定义视角：

### 简单第三人称

与原版第三人称几乎相同，只是修改了相机位置，让玩家位于画面左下部。

### 自由第三人称

与原版第三人称几乎相同，但鼠标移动时仅转动相机，不会带着玩家转动，而玩家会自动转向移动方向。

此外，用鼠标滚轮可以同时调整相机与玩家间的距离与视野大小，以玩家为主体，实现希区柯克式变焦效果。

### 自由相机

相机脱离玩家实体，可独立控制：

- 鼠标移动控制相机转动
- 移动键控制相机移动
- 空格/潜行键可以上升/下降
- 丢弃/物品栏键可以让相机滚转，即以垂直于画面的线为轴旋转

这些移动操作都基于相机的局部参考系，而非世界参考系。

> [!TIP]
> 本模组并非用于实际游玩，所以功能并不完美，例如自由相机下仍可用鼠标破坏/放置方块，这不被视为幺蛾子。
