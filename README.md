| [中文](README.zh.md) | English |
| :------------------: | :-----: |

# Perspective API Demo

This mod implements several simple custom perspectives based on the [Perspective API](https://github.com/Leawind/Perspective-API). If you are developing a mod that involves custom perspective functionality, you may refer to the code in this mod. This mod is not intended for actual gameplay, so its features are not perfect—for example, in free camera mode, you can still break/place blocks with the mouse. This is not considered a bug.

## Features

This mod adds the following custom perspectives:

### Simple Third Person

Almost identical to vanilla third-person view, except that the camera position is adjusted so that the player is positioned in the lower-left portion of the screen.

### Free Third Person

Almost identical to vanilla third-person view, but moving the mouse only rotates the camera, not the player. The player automatically turns toward the movement direction instead.

Additionally, the mouse scroll wheel adjusts both the camera-to-player distance and the FOV simultaneously, centered on the player, achieving a Hitchcock zoom (dolly zoom) effect.

### Free Camera

The camera detaches from the player entity and can be controlled independently:

- Movement keys control camera movement
- Space / Sneak move the camera up / down
- Drop / Inventory keys roll the camera (rotate around the view axis)

All movements are based on the camera's local reference frame, not the world reference frame.
