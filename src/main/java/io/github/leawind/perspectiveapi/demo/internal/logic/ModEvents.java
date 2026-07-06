package io.github.leawind.perspectiveapi.demo.internal.logic;

import io.github.leawind.perspectiveapi.api.PerspectiveAPI;
import io.github.leawind.perspectiveapi.api.PerspectiveHelper;
import io.github.leawind.perspectiveapi.demo.internal.bridge.events.GameClientEvents;
import io.github.leawind.perspectiveapi.demo.internal.impl.FreeCameraPerspective;
import io.github.leawind.perspectiveapi.demo.internal.impl.FreeThirdPersonPerspective;
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

    GameClientEvents.MOUSE_TURN_PLAYER.on(
        e -> {
          if (manager.isCurrent(FreeThirdPersonPerspective.ID)) {
            FreeThirdPersonPerspective.INSTANCE.rotate((float) e.dx * 0.15f, (float) e.dy * 0.15f);
            e.cancelDefault = true;
          }
        });

    GameClientEvents.MOUSE_SCROLL.on(
        ctx -> {
          var instance = FreeThirdPersonPerspective.INSTANCE;
          if (Minecraft.getInstance().isPaused()) return;
          if (manager.isCurrent(FreeThirdPersonPerspective.ID)) {
            float factor = (float) Math.pow(1.1487, -ctx.yOffset);
            instance.smoothFovHalfTan.setTarget(instance.smoothFovHalfTan.getTarget() * factor);
            ctx.cancelDefault = true;
          }
        });

    GameClientEvents.TICK_KEYBOARD_INPUT.on(
        impulse -> {
          if (manager.isCurrent(FreeThirdPersonPerspective.ID)) {
            var minecraft = Minecraft.getInstance();
            if (minecraft == null) return;
            var player = minecraft.player;
            if (player == null) return;

            Quaternionfc perspectiveRotation = FreeThirdPersonPerspective.INSTANCE.rotation;
            Quaternionf playerRotation =
                PerspectiveHelper.eulerDegToQuat(player.getRotationVector(), new Quaternionf());

            var moveVector = new Vector3f(-impulse.x, 0, -impulse.y);

            {
              perspectiveRotation.transform(moveVector, moveVector);
              playerRotation.transformInverse(moveVector, moveVector);
            }

            {
              var movement = player.getDeltaMovement();
              if (movement.lengthSqr() > 0.01f) {
                var orientation =
                    PerspectiveHelper.viewVectorToEulerDeg(movement.toVector3f(), new Vector2f());
                player.setYRot(orientation.y);
              }
            }

            impulse.x = -moveVector.x;
            impulse.y = -moveVector.z;
          }
        });

    GameClientEvents.MOUSE_TURN_PLAYER.on(
        e -> {
          if (manager.isCurrent(FreeCameraPerspective.ID)) {
            FreeCameraPerspective.INSTANCE.rotate((float) e.dx * 0.15f, (float) e.dy * 0.15f);
            e.cancelDefault = true;
          }
        });
    GameClientEvents.TICK_KEYBOARD_INPUT.on(
        impulse -> {
          if (manager.isCurrent(FreeCameraPerspective.ID)) {
            impulse.set(0);
          }
        });
    GameClientEvents.HANDLE_KEYBINDS_START.on(
        (minecraft) -> {
          if (!manager.isCurrent(FreeCameraPerspective.ID)) return;

          while (minecraft.options.keyUp.consumeClick()) {}
          while (minecraft.options.keyDown.consumeClick()) {}
          while (minecraft.options.keyLeft.consumeClick()) {}
          while (minecraft.options.keyRight.consumeClick()) {}
          while (minecraft.options.keyJump.consumeClick()) {}
          while (minecraft.options.keyShift.consumeClick()) {}

          while (minecraft.options.keyInventory.consumeClick()) {}
          while (minecraft.options.keyDrop.consumeClick()) {}
        });
  }
}
