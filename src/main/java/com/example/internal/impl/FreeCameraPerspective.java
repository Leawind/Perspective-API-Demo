package com.example.internal.impl;

import io.github.leawind.perspectiveapi.api.PerspectiveHelper;
import io.github.leawind.perspectiveapi.api.context.PerspectiveRenderTickContext;
import io.github.leawind.perspectiveapi.internal.bridge.Bridge;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

/** 自由移动视角：进入时位于玩家眼睛处，键盘控制移动，鼠标控制旋转，Q/E滚转，滚轮控制FOV。玩家不再移动或转动。 */
@SuppressWarnings("unused")
public class FreeCameraPerspective extends AbstractPerspective {
  public static final FreeCameraPerspective INSTANCE = new FreeCameraPerspective();

  public static final Identifier ID = Bridge.createIdentifier("example", "free_camera");

  private static final float ROLL_SPEED = 90.0f;

  private long lastTickNanos = System.nanoTime();

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

  @Override
  public void onActivate() {
    alignWithPlayer();
  }

  @Override
  public void renderTick(PerspectiveRenderTickContext context) {

    if (context.isInTransition()) {
      alignWithPlayer();
    }

    float deltaTime = (System.nanoTime() - lastTickNanos) / 1_000_000_000f;
    lastTickNanos = System.nanoTime();

    var minecraft = Minecraft.getInstance();
    if (minecraft == null) return;

    var options = minecraft.options;
    if (options != null) {
      float moveMultiplier = deltaTime * 10f;

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

  private void alignWithPlayer() {
    var minecraft = Minecraft.getInstance();
    if (minecraft == null) return;

    var player = minecraft.player;
    if (player == null) return;

    Vec3 pos = player.getEyePosition(1);
    position.set(pos.x, pos.y + 1, pos.z);

    PerspectiveHelper.eulerDegToQuat(player.getRotationVector(), rotation);
  }
}
