package io.github.leawind.perspectiveapi.demo.internal.logic.perspectives;

import com.google.auto.service.AutoService;
import io.github.leawind.perspectiveapi.api.PerspectiveAPI;
import io.github.leawind.perspectiveapi.api.PerspectiveBehavior;
import io.github.leawind.perspectiveapi.api.PerspectiveMath;
import io.github.leawind.perspectiveapi.api.context.PerspectiveContext;
import io.github.leawind.perspectiveapi.demo.internal.bridge.events.GameClientEvents;
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
@AutoService(PerspectiveBehavior.class)
@PerspectiveBehavior.Info(
    id = FreeThirdPersonPerspective.ID,
    priority = 10,
    nameKey = "perspective.perspective_api_demo.free_third_person.name",
    baseType = PerspectiveBehavior.BaseType.THIRD_PERSON_BACK)
@SuppressWarnings("unused")
public class FreeThirdPersonPerspective implements PerspectiveBehavior {
  public static final String ID = "perspective_api_demo.free_third_person";

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

  public void rotate(float deltaYaw, float deltaPitch) {
    eulerDeg.y += deltaYaw;
    eulerDeg.x = Math.max(-90f, Math.min(90f, eulerDeg.x + deltaPitch));
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

    GameClientEvents.MOUSE_SCROLL.on(
        ctx -> {
          if (Minecraft.getInstance().isPaused()) return;
          if (PerspectiveAPI.isCurrent(ID)) {
            float factor = (float) Math.pow(1.1487, -ctx.yOffset);
            smoothFovHalfTan.setTarget(smoothFovHalfTan.getTarget() * factor);
            ctx.cancelDefault = true;
          }
        });

    GameClientEvents.TICK_KEYBOARD_INPUT.on(
        impulse -> {
          if (PerspectiveAPI.isCurrent(ID)) {
            var minecraft = Minecraft.getInstance();
            if (minecraft == null) return;
            var player = minecraft.player;
            if (player == null) return;

            Vec2 playerRotVec = player.getRotationVector();
            Quaternionf playerRotation =
                PerspectiveMath.eulerDegToQuat(
                    new Vector2f(playerRotVec.x, playerRotVec.y), new Quaternionf());

            var moveVector = new Vector3f(-impulse.x, 0, -impulse.y);

            {
              rotation.transform(moveVector, moveVector);
              playerRotation.transformInverse(moveVector, moveVector);
            }

            {
              var movement = player.getDeltaMovement();
              if (movement.lengthSqr() > 0.01f) {
                var orientation =
                    PerspectiveMath.directionToEulerDeg(movement.toVector3f(), new Vector2f());
                player.setYRot(orientation.y);
              }
            }

            impulse.x = -moveVector.x;
            impulse.y = -moveVector.z;
          }
        });
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

    PerspectiveMath.eulerDegToQuat(eulerDeg, rotation);
    var backward = PerspectiveMath.getBackward(rotation, new Vector3f());
    double now = System.currentTimeMillis();
    position.set(eyePos.x, eyePos.y, eyePos.z).add(backward.mul((float) getDistance(now)));

    Vector3f viewVectorToEntity =
        new Vector3f(
            (float) (eyePos.x - position.x),
            (float) (eyePos.y - position.y),
            (float) (eyePos.z - position.z));

    PerspectiveMath.directionToQuat(viewVectorToEntity, rotation);
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
