package io.github.leawind.perspectiveapi.demo.internal.logic.perspectives;

import com.google.auto.service.AutoService;
import io.github.leawind.perspectiveapi.api.PerspectiveBehavior;
import io.github.leawind.perspectiveapi.api.PerspectiveMath;
import io.github.leawind.perspectiveapi.api.context.PerspectiveContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

@AutoService(PerspectiveBehavior.class)
@PerspectiveBehavior.Info(
    id = "perspective_api_demo.simple_third_person",
    priority = 10,
    nameKey = "perspective.perspective_api_demo.simple_third_person.name",
    baseType = PerspectiveBehavior.BaseType.THIRD_PERSON_BACK)
@SuppressWarnings("unused")
public class SimpleThirdPersonPerspective implements PerspectiveBehavior {

  public final Vector3d position = new Vector3d();
  public final Quaternionf rotation = new Quaternionf();

  @Override
  public void renderTickWhenActive(PerspectiveContext context) {
    Entity entity = context.entity();
    if (entity == null) {
      return;
    }

    Vec2 rotVec = entity.getRotationVector();
    PerspectiveMath.eulerDegToQuat(new Vector2f(rotVec.x, rotVec.y), rotation);

    var backward = PerspectiveMath.getBackward(rotation, new Vector3f());
    var right = PerspectiveMath.getRight(rotation, new Vector3f());
    var pos = entity.getEyePosition(context.partialTicks());
    position.set(pos.x, pos.y + 1, pos.z).add(backward.mul(2.5f)).add(right.mul(1));
  }

  @Override
  public void applyTransform(
      @NonNull PerspectiveContext ctx, @NonNull Vector3d position, @NonNull Quaternionf rotation) {
    position.set(this.position);
    rotation.set(this.rotation);
  }
}
