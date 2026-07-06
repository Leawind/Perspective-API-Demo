package io.github.leawind.perspectiveapi.demo.internal.impl;

import io.github.leawind.perspectiveapi.api.Perspective;
import io.github.leawind.perspectiveapi.api.PerspectiveHelper;
import io.github.leawind.perspectiveapi.api.context.PerspectiveContext;
import io.github.leawind.perspectiveapi.internal.bridge.Bridge;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
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
@SuppressWarnings("unused")
public class FreeCameraPerspective implements Perspective {
  public static final Identifier ID = Bridge.createIdentifier("example", "free_camera");
  public static final FreeCameraPerspective INSTANCE = new FreeCameraPerspective();

  private static final float ROLL_SPEED = 90.0f;

  private double lastTickSeconds;

  public final Vector3d position = new Vector3d();
  public final Quaternionf rotation = new Quaternionf();

  @Override
  public @NonNull Identifier id() {
    return ID;
  }

  @Override
  public @NonNull CameraType cameraType() {
    return CameraType.THIRD_PERSON_BACK;
  }

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
        new Quaternionf().rotationAxis((float) Math.toRadians(deltaYaw), PerspectiveHelper.DOWN);
    rotation.mul(yawRot, rotation);

    Quaternionf pitchRot =
        new Quaternionf()
            .rotationAxis((float) Math.toRadians(-deltaPitch), PerspectiveHelper.RIGHT);
    rotation.mul(pitchRot, rotation);
  }

  public void roll(float deltaRoll) {
    Quaternionf rollRot =
        new Quaternionf()
            .rotationAxis((float) Math.toRadians(deltaRoll), PerspectiveHelper.FORWARD);
    rotation.mul(rollRot, rotation);
  }

  private boolean needInit = true;

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

  @Override
  public void renderTick(PerspectiveContext context) {

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
        applyMove(PerspectiveHelper.FORWARD, moveMultiplier);
      }
      if (options.keyDown.isDown()) {
        applyMove(PerspectiveHelper.BACKWARD, moveMultiplier);
      }
      if (options.keyLeft.isDown()) {
        applyMove(PerspectiveHelper.LEFT, moveMultiplier);
      }
      if (options.keyRight.isDown()) {
        applyMove(PerspectiveHelper.RIGHT, moveMultiplier);
      }
      if (options.keyShift.isDown()) {
        applyMove(PerspectiveHelper.DOWN, moveMultiplier);
      }
      if (options.keyJump.isDown()) {
        applyMove(PerspectiveHelper.UP, moveMultiplier);
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
