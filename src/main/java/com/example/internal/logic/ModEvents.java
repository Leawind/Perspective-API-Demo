package com.example.internal.logic;

import com.example.internal.bridge.events.GameClientEvents;
import com.example.internal.impl.FreeCameraPerspective;
import com.example.internal.impl.FreeThirdPersonPerspective;
import io.github.leawind.perspectiveapi.api.PerspectiveAPI;
import io.github.leawind.perspectiveapi.api.PerspectiveHelper;
import net.minecraft.client.Minecraft;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ModEvents {
  private static final Logger LOGGER = LoggerFactory.getLogger(ModEvents.class);

  public static void register() {
    var manager = PerspectiveAPI.getManager();

    // region FreeThirdPersonPerspective
    GameClientEvents.MOUSE_TURN_PLAYER.on(
        e -> {
          if (manager.isCurrent(FreeThirdPersonPerspective.INSTANCE)) {
            FreeThirdPersonPerspective.INSTANCE.rotate((float) e.dx * 0.15f, (float) e.dy * 0.15f);
            e.cancelDefault = true;
          }
        });

    GameClientEvents.TICK_KEYBOARD_INPUT.on(
        impulse -> {
          if (manager.isCurrent(FreeThirdPersonPerspective.INSTANCE)) {
            var minecraft = Minecraft.getInstance();
            if (minecraft == null) return;
            var player = minecraft.player;
            if (player == null) return;

            Quaternionfc perspectiveRotation = FreeThirdPersonPerspective.INSTANCE.getRotation();
            Quaternionf playerRotation =
                PerspectiveHelper.getQuat(player.getRotationVector(), new Quaternionf());

            // 视角空间下的移动向量
            var moveVector = new Vector3f(-impulse.x, 0, -impulse.y);

            { // 将向量从"视角局部空间"变换到"世界空间"
              perspectiveRotation.transform(moveVector, moveVector);
              // 将"世界空间"下的向量变换到"玩家局部空间"（应用玩家旋转的逆矩阵）
              playerRotation.transformInverse(moveVector, moveVector);
            }

            // 旋转玩家到移动方向
            {
              var movement = player.getDeltaMovement();
              if (movement.lengthSqr() > 0.01f) {
                var orientation =
                    PerspectiveHelper.getEulerDeg(movement.toVector3f(), new Vector2f());
                player.setYRot(orientation.y);
              }
            }

            impulse.x = -moveVector.x;
            impulse.y = -moveVector.z;
          }
        });
    // endregion

    // region FreeCameraPerspective
    GameClientEvents.MOUSE_TURN_PLAYER.on(
        e -> {
          if (manager.isCurrent(FreeCameraPerspective.INSTANCE)) {
            FreeCameraPerspective.INSTANCE.rotate((float) e.dx * 0.15f, (float) e.dy * 0.15f);
            e.cancelDefault = true;
          }
        });
    GameClientEvents.TICK_KEYBOARD_INPUT.on(
        impulse -> {
          if (manager.isCurrent(FreeCameraPerspective.INSTANCE)) {
            impulse.set(0);
          }
        });

    // endregion
  }
}
