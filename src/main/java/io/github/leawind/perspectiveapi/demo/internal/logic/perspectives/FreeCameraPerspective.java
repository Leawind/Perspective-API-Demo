package io.github.leawind.perspectiveapi.demo.internal.logic.perspectives;

import com.google.auto.service.AutoService;
import io.github.leawind.perspectiveapi.api.PerspectiveAPI;
import io.github.leawind.perspectiveapi.api.PerspectiveBehavior;
import io.github.leawind.perspectiveapi.api.PerspectiveMath;
import io.github.leawind.perspectiveapi.api.context.PerspectiveContext;
import io.github.leawind.perspectiveapi.demo.internal.bridge.events.GameClientEvents;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

/// Free camera perspective:
///
/// - Keyboard controls movement
/// - mouse controls rotation
/// - Q/E roll
/// - scroll wheel controls FOV
///
/// The player no longer moves or turns.
@AutoService(PerspectiveBehavior.class)
@PerspectiveBehavior.Info(
    id = FreeCameraPerspective.ID,
    priority = 10,
    nameKey = "perspective.perspective_api_demo.free_camera.name",
    cameraType = CameraType.THIRD_PERSON_BACK)
@SuppressWarnings("unused")
public class FreeCameraPerspective implements PerspectiveBehavior {
  public static final String ID = "perspective_api_demo.free_camera";

  private static final float ROLL_SPEED = 90.0f;

  private double lastTickSeconds;

  private boolean needInit = true;
  public final Vector3d position = new Vector3d();
  public final Quaternionf rotation = new Quaternionf();

  private void applyMove(Vector3fc delta, float multiplier) {
    applyMove(delta.mul(multiplier, new Vector3f()));
  }

  private void applyMove(Vector3f delta) {
    rotation.transform(delta);
    position.add(delta);
  }

  private void applyMove(Vector3fc delta) {
    applyMove(new Vector3f(delta));
  }

  public void rotate(float deltaYaw, float deltaPitch) {
    Quaternionf yawRot =
        new Quaternionf().rotationAxis((float) Math.toRadians(deltaYaw), PerspectiveMath.DOWN);
    rotation.mul(yawRot, rotation);

    Quaternionf pitchRot =
        new Quaternionf().rotationAxis((float) Math.toRadians(-deltaPitch), PerspectiveMath.RIGHT);
    rotation.mul(pitchRot, rotation);
  }

  public void roll(float deltaRoll) {
    Quaternionf rollRot =
        new Quaternionf().rotationAxis((float) Math.toRadians(deltaRoll), PerspectiveMath.FORWARD);
    rotation.mul(rollRot, rotation);
  }

  @Override
  public void init() {
    GameClientEvents.MOUSE_TURN_PLAYER.on(
        e -> {
          if (PerspectiveAPI.isCurrent(ID)) {
            rotate((float) e.dx * 0.15f, (float) e.dy * 0.15f);
            e.cancelDefault = true;
          }
        });
    GameClientEvents.TICK_KEYBOARD_INPUT.on(
        impulse -> {
          if (PerspectiveAPI.isCurrent(ID)) {
            impulse.set(0);
          }
        });
    GameClientEvents.HANDLE_KEYBINDS_START.on(
        (minecraft) -> {
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

  @Override
  public void applyTransform(
      @NonNull PerspectiveContext context,
      @NonNull Vector3d position,
      @NonNull Quaternionf rotation) {
    if (needInit) {
      this.position.set(position);
      this.rotation.set(rotation);
      needInit = false;
    } else {
      position.set(this.position);
      rotation.set(this.rotation);
    }
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public void renderTickWhenActive(PerspectiveContext context) {

    double now = GLFW.glfwGetTime();
    float deltaTime = (float) (now - lastTickSeconds);
    lastTickSeconds = now;

    if (context.isTransitioning()) return;

    // cannot use Math.clamp below java 21
    deltaTime = Math.max(Math.min(deltaTime, 0.1f), 0.0001f);

    var minecraft = Minecraft.getInstance();
    if (minecraft == null) return;

    var options = minecraft.options;
    if (options != null) {
      float moveMultiplier = deltaTime * 10;

      if (options.keyUp.isDown()) {
        applyMove(PerspectiveMath.FORWARD, moveMultiplier);
      }
      if (options.keyDown.isDown()) {
        applyMove(PerspectiveMath.BACKWARD, moveMultiplier);
      }
      if (options.keyLeft.isDown()) {
        applyMove(PerspectiveMath.LEFT, moveMultiplier);
      }
      if (options.keyRight.isDown()) {
        applyMove(PerspectiveMath.RIGHT, moveMultiplier);
      }
      if (options.keyShift.isDown()) {
        applyMove(PerspectiveMath.DOWN, moveMultiplier);
      }
      if (options.keyJump.isDown()) {
        applyMove(PerspectiveMath.UP, moveMultiplier);
      }

      if (options.keyDrop.isDown()) {
        roll(-ROLL_SPEED * deltaTime);
      }
      if (options.keyInventory.isDown()) {
        roll(ROLL_SPEED * deltaTime);
      }
    }
  }
}
