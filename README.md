<div align="center">

<img src="src/main/resources/logo.128x.png" alt="Perspective API Demo" style="image-rendering:pixelated;height:8em;">

# 视角 API 演示 (Perspective API Demo)

[中文](README.zh.md) | English

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/cA3rwRVH?style=flat&logo=modrinth&color=17B85A&cacheSeconds=3600&label=Modrinth)](https://modrinth.com/mod/perspective-api-demo)
[![CurseForge Downloads](https://img.shields.io/curseforge/dt/1579305?style=flat&logo=curseforge&color=F1643%5E&cacheSeconds=3600&label=CurseForge)](https://www.curseforge.com/minecraft/mc-mods/perspective-api-demo)

A Minecraft mod that implements some simple features based on the [Perspective API](https://github.com/Leawind/Perspective-API).

</div>

## Perspectives

This mod adds the following custom perspectives:

### Simple Third Person

Almost identical to vanilla third-person view, except that the camera position is adjusted so that the player is positioned in the lower-left portion of the screen.

### Free Third Person

Almost identical to vanilla third-person view, but moving the mouse only rotates the camera, not the player. The player automatically turns toward the movement direction instead.

Additionally, the mouse scroll wheel adjusts both the camera-to-player distance and the FOV simultaneously, centered on the player, achieving a Hitchcock zoom (dolly zoom) effect.

### Free Camera

The camera detaches from the player entity and can be controlled independently:

- Mouse movement controls camera rotation
- Movement keys control camera movement
- Space / Sneak move the camera up / down
- Drop / Inventory keys roll the camera (rotate around the view axis)

All movements are based on the camera's local reference frame, not the world reference frame.

> [!TIP]
> This mod is not intended for actual gameplay, so its features are not perfect—for example, in free camera mode, you can still break/place blocks with the mouse. This is not considered a bug.
