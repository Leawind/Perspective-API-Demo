package io.github.leawind.perspectiveapi.demo.internal.logic.perspectives;

import com.google.auto.service.AutoService;
import io.github.leawind.perspectiveapi.api.PerspectiveAPI;
import io.github.leawind.perspectiveapi.api.PerspectiveBehavior;
import io.github.leawind.perspectiveapi.api.PerspectiveContext;
import io.github.leawind.perspectiveapi.api.PerspectiveInfo;
import io.github.leawind.perspectiveapi.api.PerspectiveMath;
import io.github.leawind.perspectiveapi.api.PerspectiveState;
import io.github.leawind.perspectiveapi.demo.internal.bridge.events.GameClientEvents;
import net.minecraft.client.Minecraft;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

/// Free camera perspective:
///
/// - Keyboard controls movement
/// - mouse controls rotation
/// - Q/E roll
/// - scroll wheel controls movement speed
///
/// The player no longer moves or turns.
@SuppressWarnings({"unused", "UnstableApiUsage", "ConstantConditions"})
@AutoService(PerspectiveBehavior.class)
@PerspectiveInfo.Declaration(
    id = FreeCameraPerspective.ID,
    priority = 10,
    baseType = PerspectiveBehavior.BaseType.THIRD_PERSON_BACK)
public final class FreeCameraPerspective implements PerspectiveBehavior {
  public static final String ID = "perspective_api_demo.free_camera";

  private static final float MOUSE_ROTATION_SCALE = 0.15f;
  private static final float ROLL_SPEED = 90.0f;
  private static final float BASE_SPEED = 10.0f;
  private static final float SCROLL_SPEED_STEP = 0.5f;

  private double lastTickSeconds;
  private float speedFactor;

  private boolean needInit = true;
  private final Vector3d position = new Vector3d();
  private final Quaternionf rotation = new Quaternionf();

  private void rotate(float deltaYaw, float deltaPitch) {
    Quaternionf yawRot =
        new Quaternionf().rotationAxis((float) Math.toRadians(deltaYaw), PerspectiveMath.DOWN);
    rotation.mul(yawRot, rotation);

    Quaternionf pitchRot =
        new Quaternionf().rotationAxis((float) Math.toRadians(-deltaPitch), PerspectiveMath.RIGHT);
    rotation.mul(pitchRot, rotation);
  }

  private void roll(float deltaRoll) {
    Quaternionf rollRot =
        new Quaternionf().rotationAxis((float) Math.toRadians(deltaRoll), PerspectiveMath.FORWARD);
    rotation.mul(rollRot, rotation);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public void init() {
    GameClientEvents.MOUSE_TURN_PLAYER.on(
        input1 -> {
          if (!PerspectiveAPI.isCurrent(ID)) return;

          rotate(
              (float) input1.dx * MOUSE_ROTATION_SCALE, (float) input1.dy * MOUSE_ROTATION_SCALE);
          input1.cancelDefault();
        });
    GameClientEvents.MOUSE_SCROLL.on(
        input -> {
          if (Minecraft.getInstance().isPaused() || !PerspectiveAPI.isCurrent(ID)) return;

          speedFactor += (float) input.yOffset * SCROLL_SPEED_STEP;
          input.cancelDefault = true;
        });
    GameClientEvents.TICK_KEYBOARD_INPUT.on(
        impulse -> {
          if (PerspectiveAPI.isCurrent(ID)) impulse.set(0);
        });
    GameClientEvents.HANDLE_KEYBINDS_START.on(
        minecraft -> {
          if (!PerspectiveAPI.isCurrent(ID)) return;

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

  @Override
  public void onActivate() {
    needInit = true;
  }

  @SuppressWarnings({"ConstantConditions", "MathClampMigration"})
  @Override
  public void applyCameraState(
      PerspectiveState.@NonNull Mutable state, @NonNull PerspectiveContext context) {

    {
      double now = GLFW.glfwGetTime();
      float deltaTime = (float) (now - lastTickSeconds);
      lastTickSeconds = now;

      if (context.isTransitioning()) return;

      // cannot use Math.clamp below java 21
      deltaTime = Math.max(Math.min(deltaTime, 0.1f), 0.0001f);

      var minecraft = Minecraft.getInstance();
      if (minecraft == null) return;

      var options = minecraft.options;
      if (options == null) return;

      // moveDirection: local
      Vector3f moveDirection = new Vector3f();
      if (options.keyUp.isDown()) moveDirection.add(PerspectiveMath.FORWARD);
      if (options.keyDown.isDown()) moveDirection.add(PerspectiveMath.BACKWARD);
      if (options.keyLeft.isDown()) moveDirection.add(PerspectiveMath.LEFT);
      if (options.keyRight.isDown()) moveDirection.add(PerspectiveMath.RIGHT);
      if (options.keyShift.isDown()) moveDirection.add(PerspectiveMath.DOWN);
      if (options.keyJump.isDown()) moveDirection.add(PerspectiveMath.UP);

      if (moveDirection.lengthSquared() > 0) {
        float moveDistance = deltaTime * BASE_SPEED * (float) Math.pow(2, speedFactor);

        // offset: local
        Vector3f offset = moveDirection.mul(moveDistance);

        // offset: world
        rotation.transform(offset);
        position.add(offset);
      }

      if (options.keyDrop.isDown()) roll(-ROLL_SPEED * deltaTime);
      if (options.keyInventory.isDown()) roll(ROLL_SPEED * deltaTime);
    }

    if (needInit) {
      var eyePos = context.cameraEntity().getEyePosition(context.partialTicks());
      this.position.set(eyePos.x, eyePos.y, eyePos.z);
      this.rotation.set(state.rotation());
      needInit = false;
    }
    state.position().set(this.position);
    state.rotation().set(this.rotation);
  }
}
