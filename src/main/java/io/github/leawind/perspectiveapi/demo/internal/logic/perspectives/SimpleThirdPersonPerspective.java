package io.github.leawind.perspectiveapi.demo.internal.logic.perspectives;

import com.google.auto.service.AutoService;
import io.github.leawind.perspectiveapi.api.PerspectiveBehavior;
import io.github.leawind.perspectiveapi.api.PerspectiveContext;
import io.github.leawind.perspectiveapi.api.PerspectiveInfo;
import io.github.leawind.perspectiveapi.api.PerspectiveMath;
import io.github.leawind.perspectiveapi.api.PerspectiveState;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

@SuppressWarnings({"unused", "UnstableApiUsage"})
@AutoService(PerspectiveBehavior.class)
@PerspectiveInfo.Declaration(
    id = SimpleThirdPersonPerspective.ID,
    priority = 10,
    baseType = PerspectiveBehavior.BaseType.THIRD_PERSON_BACK)
public final class SimpleThirdPersonPerspective implements PerspectiveBehavior {
  public static final String ID = "perspective_api_demo.simple_third_person";

  private final Vector3f temp = new Vector3f();

  @Override
  public void applyCameraState(
      PerspectiveState.@NonNull Mutable state, @NonNull PerspectiveContext context) {

    Entity entity = context.cameraEntity();
    if (entity == null) {
      return;
    }

    var eye = entity.getEyePosition(context.partialTicks());

    double distance = state.position().distance(eye.x(), eye.y(), eye.z());

    var offset = new Vector3f();

    offset.add(PerspectiveMath.getRight(state.rotation(), temp).mul((float) distance * 0.3f));
    offset.add(PerspectiveMath.getUp(state.rotation(), temp).mul((float) distance * 0.2f));

    state.position().add(offset);
  }
}
