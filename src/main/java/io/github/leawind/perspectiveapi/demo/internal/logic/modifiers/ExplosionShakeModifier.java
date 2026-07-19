package io.github.leawind.perspectiveapi.demo.internal.logic.modifiers;

import io.github.leawind.perspectiveapi.api.PerspectiveMath;
import io.github.leawind.perspectiveapi.api.PerspectiveModifier;
import io.github.leawind.perspectiveapi.api.context.PerspectiveContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

/// ```mcfunction
/// /execute at @p run summon minecraft:creeper ~ ~ ~ {ignited:1b,Fuse:0s,ExplosionRadius:13b}
/// ```
public class ExplosionShakeModifier implements PerspectiveModifier {
  public static final String ID = "perspective_api_demo.explosion_shake";

  private static final double MAX_SHAKE_DEGREES = 45;

  public static final double DISTANCE_FACTOR = 1.0d;

  private static final double GROW_TIME = 0.2;
  private static final double DECAY_HALF_LIFE = 0.06;

  private static final double FREQUENCY = 8.0;
  private static final double FREQ_1 = FREQUENCY;
  private static final double FREQ_2 = FREQUENCY * 1.3 + 0.7;
  private static final double FREQ_3 = FREQUENCY * 0.7 + 1.5;
  private static final double PHASE_1 = 0.0;
  private static final double PHASE_2 = 2.1;
  private static final double PHASE_3 = 4.2;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void applyTransform(
      @NonNull PerspectiveContext ctx, @NonNull Vector3d position, @NonNull Quaternionf rotation) {
    Entity entity = ctx.entity();
    if (entity == null) return;

    double now = GLFW.glfwGetTime();

    double totalInfluence = 0.0;
    for (var event : ExplosionShakeState.INSTANCE.getActiveEvents()) {
      double distance = new Vec3(position.x(), position.y(), position.z()).distanceTo(event.center);
      double deltaTime = now - event.time;
      double influence = getInfluence(event, distance, deltaTime);

      event.shouldRemove = influence <= 3e-4;
      totalInfluence += influence;
    }

    // \left(\frac{2\arctan\ x}{\pi}\right)^{0.9}
    double atanInfluence = Math.pow((2 * Math.atan(totalInfluence)) / Math.PI, 0.9);

    double shakeIntensity = atanInfluence * MAX_SHAKE_DEGREES;

    if (shakeIntensity < 1e-5) {
      return;
    }

    double s1 = Math.sin(now * FREQ_1 + PHASE_1);
    double s2 = Math.sin(now * FREQ_2 + PHASE_2);
    double s3 = Math.sin(now * FREQ_3 + PHASE_3);

    double yawShake = shakeIntensity * s1 * s2;
    double pitchShake = shakeIntensity * s2 * s3;
    double rollShake = shakeIntensity * s1 * s3;

    Quaternionf yawRot =
        new Quaternionf().rotationAxis((float) Math.toRadians(yawShake), PerspectiveMath.DOWN);
    Quaternionf pitchRot =
        new Quaternionf().rotationAxis((float) Math.toRadians(pitchShake), PerspectiveMath.RIGHT);
    Quaternionf rollRot =
        new Quaternionf()
            .rotationAxis((float) Math.toRadians(rollShake), PerspectiveMath.FORWARD);
    rotation.mul(pitchRot, rotation).mul(yawRot, rotation).mul(rollRot, rotation);
  }

  private static double getInfluence(
      ExplosionShakeState.ExplosionEvent event, double distance, double deltaTime) {
    double power = Math.pow(event.radius, 3);

    // 1 / (x^3 + 1)
    double distInfluence = 1.0 / (Math.pow(distance, 3) + 1d / 8);

    // \sin\left(\frac{\pi}{2T}x\right)
    double timeGrowInfluence =
        deltaTime >= GROW_TIME ? 1 : Math.sin(Math.PI * deltaTime / 2 / GROW_TIME);
    // e^{-\frac{\ln2}{H}x}
    double timeDecayInfluence = Math.exp(-Math.log(2) / DECAY_HALF_LIFE * deltaTime);

    return power
        * Math.pow(distInfluence, DISTANCE_FACTOR)
        * timeGrowInfluence
        * timeDecayInfluence;
  }
}
