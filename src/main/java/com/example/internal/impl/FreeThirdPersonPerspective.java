package com.example.internal.impl;

import io.github.leawind.perspectiveapi.api.PerspectiveHelper;
import io.github.leawind.perspectiveapi.api.context.PerspectiveRenderTickContext;
import io.github.leawind.perspectiveapi.internal.bridge.Bridge;
import net.minecraft.client.CameraType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

/** 自由第三人称视角：相机围绕玩家旋转，始终朝向玩家。鼠标移动只旋转相机，不旋转玩家。 */
@SuppressWarnings("unused")
public class FreeThirdPersonPerspective extends AbstractPerspective {
  public static final FreeThirdPersonPerspective INSTANCE = new FreeThirdPersonPerspective();

  public static final Identifier ID = Bridge.createIdentifier("example", "free_third_person");

  /** x=pitch, y=yaw */
  private final Vector2f orientation = new Vector2f();

  private double distance = 4.0;
  private boolean needInit = true;

  @Override
  public @NonNull Identifier id() {
    return ID;
  }

  @Override
  public @NonNull CameraType cameraType() {
    return CameraType.THIRD_PERSON_BACK;
  }

  public void rotate(float deltaYaw, float deltaPitch) {
    orientation.y += deltaYaw;
    orientation.x = Math.max(-90f, Math.min(90f, orientation.x + deltaPitch));
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  @Override
  public void onActivate() {
    needInit = true;
  }

  @Override
  public void renderTick(PerspectiveRenderTickContext context) {
    Entity entity = context.entity();
    if (entity == null) return;

    var eyePos = entity.getEyePosition(context.partialTicks());

    if (needInit) {
      Vec2 rotVec = entity.getRotationVector();
      orientation.set(rotVec.x, rotVec.y);
      needInit = false;
    }

    // 欧拉角 → 旋转，用于计算相机位置
    PerspectiveHelper.getQuat(orientation, rotation);
    var backward = PerspectiveHelper.getBackwardVector(rotation, new Vector3f());
    position.set(eyePos.x, eyePos.y, eyePos.z).add(backward.mul((float) distance));

    // 相机始终朝向玩家
    Vector3f toEntity =
        new Vector3f(
            (float) (eyePos.x - position.x),
            (float) (eyePos.y - position.y),
            (float) (eyePos.z - position.z));

    PerspectiveHelper.getQuat(toEntity, rotation);
  }
}
