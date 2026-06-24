package com.example.internal.impl;

import com.example.internal.utils.ExpSmoothDouble;
import io.github.leawind.perspectiveapi.api.PerspectiveHelper;
import io.github.leawind.perspectiveapi.api.context.PerspectiveRenderTickContext;
import io.github.leawind.perspectiveapi.internal.bridge.Bridge;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
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
  private final Vector2f eulerDeg = new Vector2f();

  // region dolly zoom
  /// S = d * tan(FOV/2)
  private static double getFrustumHalfHeight(double distance, float fovDeg) {
    double fovRad = fovDeg / 180d * Math.PI;
    return distance * Math.tan(fovRad / 2);
  }

  public double frustumHalfHeight = getFrustumHalfHeight(4.0, 70.0f);

  /// tan(FOV/2)
  public final ExpSmoothDouble smoothFovHalfTan = new ExpSmoothDouble(100, frustumHalfHeight / 4.0);

  public double getDistance(double now) {
    return frustumHalfHeight / smoothFovHalfTan.get(now);
  }

  @Override
  public Float getFieldOfView() {
    // 希区柯克式变焦
    double now = System.currentTimeMillis();
    return (float) (2 * Math.atan(smoothFovHalfTan.get(now)) * 180d / Math.PI);
  }

  // endregion

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
    eulerDeg.y += deltaYaw;
    eulerDeg.x = Math.max(-90f, Math.min(90f, eulerDeg.x + deltaPitch));
  }

  @Override
  public void onActivate() {
    needInit = true;
  }

  @Override
  public void clientTick(Minecraft minecraft) {
    Entity entity = minecraft.getCameraEntity();
    if (entity == null) return;
    frustumHalfHeight = getFrustumHalfHeight(4 * entity.getBoundingBox().getSize(), 70.0f);
  }

  @Override
  public void renderTick(PerspectiveRenderTickContext context) {
    Entity entity = context.entity();
    if (entity == null) return;

    var eyePos = entity.getEyePosition(context.partialTicks());

    if (needInit) {
      Vec2 rotVec = entity.getRotationVector();
      eulerDeg.set(rotVec.x, rotVec.y);
      needInit = false;
    }

    // 欧拉角 → 旋转，用于计算相机位置
    PerspectiveHelper.eulerDegToQuat(eulerDeg, rotation);
    var backward = PerspectiveHelper.getBackwardVector(rotation, new Vector3f());
    double now = System.currentTimeMillis();
    position.set(eyePos.x, eyePos.y, eyePos.z).add(backward.mul((float) getDistance(now)));

    // 相机始终朝向玩家
    Vector3f viewVectorToEntity =
        new Vector3f(
            (float) (eyePos.x - position.x),
            (float) (eyePos.y - position.y),
            (float) (eyePos.z - position.z));
    
    PerspectiveHelper.viewVectorToQuat(viewVectorToEntity, rotation);
  }
}
