package io.github.leawind.perspectiveapi.demo.internal.logic.perspectives;

import io.github.leawind.perspectiveapi.api.Perspective;
import io.github.leawind.perspectiveapi.api.PerspectiveHelper;
import io.github.leawind.perspectiveapi.api.context.PerspectiveContext;
import io.github.leawind.perspectiveapi.demo.internal.utils.ExpSmoothDouble;
import io.github.leawind.perspectiveapi.internal.bridge.Bridge;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

/// - camera orbits around the player and always faces it.
/// - Mouse movement only rotates the camera, not the player.
@SuppressWarnings("unused")
public class FreeThirdPersonPerspective implements Perspective {
  public static final Identifier ID = Bridge.createIdentifier("example", "free_third_person");
  public static final FreeThirdPersonPerspective INSTANCE = new FreeThirdPersonPerspective();

  public final Vector3d position = new Vector3d();
  public final Quaternionf rotation = new Quaternionf();

  private final Vector2f eulerDeg = new Vector2f();

  private static double getFrustumHalfHeight(double distance, float fovDeg) {
    double fovRad = fovDeg / 180d * Math.PI;
    return distance * Math.tan(fovRad / 2);
  }

  public double frustumHalfHeight = getFrustumHalfHeight(4.0, 70.0f);

  public final ExpSmoothDouble smoothFovHalfTan = new ExpSmoothDouble(100, frustumHalfHeight / 4.0);

  public double getDistance(double now) {
    return frustumHalfHeight / smoothFovHalfTan.get(now);
  }

  private float getFieldOfViewValue() {
    double now = System.currentTimeMillis();
    return (float) (2 * Math.atan(smoothFovHalfTan.get(now)) * 180d / Math.PI);
  }

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
  public void clientTickWhenActive(Minecraft minecraft) {
    Entity entity = minecraft.getCameraEntity();
    if (entity == null) return;
    frustumHalfHeight = getFrustumHalfHeight(4 * entity.getBoundingBox().getSize(), 70.0f);
  }

  @Override
  public void renderTickWhenActive(PerspectiveContext context) {
    Entity entity = context.entity();
    if (entity == null) return;

    var eyePos = entity.getEyePosition(context.partialTicks());

    if (needInit) {
      Vec2 rotVec = entity.getRotationVector();
      eulerDeg.set(rotVec.x, rotVec.y);
      needInit = false;
    }

    PerspectiveHelper.eulerDegToQuat(eulerDeg, rotation);
    var backward = PerspectiveHelper.getBackwardVector(rotation, new Vector3f());
    double now = System.currentTimeMillis();
    position.set(eyePos.x, eyePos.y, eyePos.z).add(backward.mul((float) getDistance(now)));

    Vector3f viewVectorToEntity =
        new Vector3f(
            (float) (eyePos.x - position.x),
            (float) (eyePos.y - position.y),
            (float) (eyePos.z - position.z));

    PerspectiveHelper.viewVectorToQuat(viewVectorToEntity, rotation);
  }

  @Override
  public float applyFov(@NonNull PerspectiveContext ctx, float vanillaFovDeg) {
    return getFieldOfViewValue();
  }

  @Override
  public void applyTransform(
      @NonNull PerspectiveContext ctx, @NonNull Vector3d position, @NonNull Quaternionf rotation) {
    position.set(this.position);
    rotation.set(this.rotation);
  }
}
