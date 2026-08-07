package io.github.leawind.perspectiveapi.demo.internal.logic.perspectives;

import com.google.auto.service.AutoService;
import io.github.leawind.perspectiveapi.api.PerspectiveAPI;
import io.github.leawind.perspectiveapi.api.PerspectiveBehavior;
import io.github.leawind.perspectiveapi.api.PerspectiveContext;
import io.github.leawind.perspectiveapi.api.PerspectiveInfo;
import io.github.leawind.perspectiveapi.api.PerspectiveMath;
import io.github.leawind.perspectiveapi.api.PerspectiveState;
import io.github.leawind.perspectiveapi.demo.internal.bridge.events.GameClientEvents;
import io.github.leawind.perspectiveapi.demo.internal.bridge.events.MouseScrollContext;
import io.github.leawind.perspectiveapi.demo.internal.bridge.events.context.MouseTurnPlayerContext;
import io.github.leawind.perspectiveapi.demo.internal.utils.ExpSmoothDouble;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

/// - camera orbits around the player and always faces it.
/// - Mouse movement only rotates the camera, not the player.
@SuppressWarnings({"unused", "UnstableApiUsage", "ConstantConditions", "MathClampMigration"})
@AutoService(PerspectiveBehavior.class)
@PerspectiveInfo.Declaration(
    id = FreeThirdPersonPerspective.ID,
    priority = 10,
    baseType = PerspectiveBehavior.BaseType.THIRD_PERSON_BACK)
public final class FreeThirdPersonPerspective implements PerspectiveBehavior {
  public static final String ID = "perspective_api_demo.free_third_person";

  private static final float MOUSE_ROTATION_SCALE = 0.15f;
  private static final float DEFAULT_FOV_DEG = 70.0f;
  private static final double DEFAULT_DISTANCE = 4.0;
  private static final double ZOOM_SCROLL_BASE = 1.1487;

  private final Vector3d position = new Vector3d();
  private final Quaternionf rotation = new Quaternionf();

  /// Camera orientation in euler degrees. Pitch is clamped to [-90, 90], but the clamp still
  /// permits gimbal lock: at +-90 pitch the view aligns with the yaw axis, making yaw undefined.
  private final Vector2f eulerDeg = new Vector2f();

  private final ExpSmoothDouble smoothFovHalfTan =
      new ExpSmoothDouble(100, getFrustumHalfHeight(DEFAULT_DISTANCE, DEFAULT_FOV_DEG) / 4.0);

  private double frustumHalfHeight = getFrustumHalfHeight(DEFAULT_DISTANCE, DEFAULT_FOV_DEG);
  private boolean needInit = true;

  private void rotate(float deltaYaw, float deltaPitch) {
    eulerDeg.y += deltaYaw;
    // Pitch is clamped to [-90, 90] to avoid flipping, but this still allows
    // gimbal lock: at +-90 the view aligns with the yaw axis and yaw is ineffective.
    eulerDeg.x = Math.max(-90f, Math.min(90f, eulerDeg.x + deltaPitch));
  }

  @Override
  public void init() {
    GameClientEvents.MOUSE_TURN_PLAYER.on(this::onMouseTurnPlayer);
    GameClientEvents.MOUSE_SCROLL.on(this::onMouseScroll);
    GameClientEvents.TICK_KEYBOARD_INPUT.on(this::onKeyboardInput);
    GameClientEvents.CLIENT_TICK.on(this::onClientTick);
  }

  @Override
  public void onActivate() {
    needInit = true;
  }

  private void onClientTick(Minecraft minecraft) {
    if (!PerspectiveAPI.isCurrent(ID)) return;
    Entity entity = minecraft.getCameraEntity();
    if (entity == null) return;
    frustumHalfHeight =
        getFrustumHalfHeight(4 * entity.getBoundingBox().getSize(), DEFAULT_FOV_DEG);
  }

  @Override
  public void computeCameraState(
      PerspectiveState.@NonNull Mutable state, @NonNull PerspectiveContext context) {
    {
      Entity entity = context.cameraEntity();
      if (entity == null) return;

      var eyePos = entity.getEyePosition(context.partialTicks());

      if (needInit) {
        Vec2 rotVec = entity.getRotationVector();
        eulerDeg.set(rotVec.x, rotVec.y);
        needInit = false;
      }

      PerspectiveMath.eulerDegToQuat(eulerDeg, rotation);
      var backward = PerspectiveMath.getBackward(rotation, new Vector3f());
      double now = System.currentTimeMillis();
      position.set(eyePos.x, eyePos.y, eyePos.z).add(backward.mul((float) getDistance(now)));

      // The final rotation is re-derived from the view vector via directionToQuat
      // (direction -> euler -> quaternion). The conversion does not limit the pitch,
      // so at +-90 the yaw is unrecoverable from the direction: gimbal lock.
      Vector3f viewVectorToEntity =
          new Vector3f(
              (float) (eyePos.x - position.x),
              (float) (eyePos.y - position.y),
              (float) (eyePos.z - position.z));

      PerspectiveMath.directionToQuat(viewVectorToEntity, rotation);
    }

    state.position().set(this.position);
    state.rotation().set(this.rotation);
    state.setFovDeg(getFieldOfViewValue());
  }

  private void onMouseTurnPlayer(MouseTurnPlayerContext input) {
    if (!PerspectiveAPI.isCurrent(ID)) return;

    rotate((float) input.dx * MOUSE_ROTATION_SCALE, (float) input.dy * MOUSE_ROTATION_SCALE);
    input.cancelDefault();
  }

  private void onMouseScroll(MouseScrollContext input) {
    if (Minecraft.getInstance().isPaused() || !PerspectiveAPI.isCurrent(ID)) return;

    float zoomFactor = (float) Math.pow(ZOOM_SCROLL_BASE, -input.yOffset);
    smoothFovHalfTan.setTarget(smoothFovHalfTan.getTarget() * zoomFactor);
    input.cancelDefault = true;
  }

  private void onKeyboardInput(Vector2f impulse) {
    if (!PerspectiveAPI.isCurrent(ID)) return;

    var player = Minecraft.getInstance().player;
    if (player == null) return;

    Vec2 playerRotationDeg = player.getRotationVector();
    Quaternionf playerRotation =
        PerspectiveMath.eulerDegToQuat(
            new Vector2f(playerRotationDeg.x, playerRotationDeg.y), new Quaternionf());
    Vector3f moveVector = new Vector3f(-impulse.x, 0, -impulse.y);
    rotation.transform(moveVector, moveVector);
    playerRotation.transformInverse(moveVector, moveVector);

    var movement = player.getDeltaMovement();
    if (movement.lengthSqr() > 0.01f) {
      var orientation = PerspectiveMath.directionToEulerDeg(movement.toVector3f(), new Vector2f());
      player.setYRot(orientation.y);
    }

    impulse.x = -moveVector.x;
    impulse.y = -moveVector.z;
  }

  private static double getFrustumHalfHeight(double distance, float fovDeg) {
    return distance * Math.tan(Math.toRadians(fovDeg) / 2);
  }

  private double getDistance(double now) {
    return frustumHalfHeight / smoothFovHalfTan.get(now);
  }

  private float getFieldOfViewValue() {
    return (float)
        (2 * Math.atan(smoothFovHalfTan.get(System.currentTimeMillis())) * 180d / Math.PI);
  }
}
