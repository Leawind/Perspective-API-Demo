package io.github.leawind.perspectiveapi.demo.internal.impl;

import io.github.leawind.perspectiveapi.api.Perspective;
import io.github.leawind.perspectiveapi.api.PerspectiveHelper;
import io.github.leawind.perspectiveapi.api.context.PerspectiveContext;
import io.github.leawind.perspectiveapi.internal.bridge.Bridge;
import net.minecraft.client.CameraType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public class SimpleThirdPersonPerspective implements Perspective {
  public static final Identifier ID = Bridge.createIdentifier("example", "simple_third_person");
  public static final SimpleThirdPersonPerspective INSTANCE = new SimpleThirdPersonPerspective();

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

  @Override
  public void renderTick(PerspectiveContext context) {
    Entity entity = context.entity();
    if (entity == null) {
      return;
    }

    PerspectiveHelper.eulerDegToQuat(entity.getRotationVector(), rotation);

    var backward = PerspectiveHelper.getBackwardVector(rotation, new Vector3f());
    var right = PerspectiveHelper.getRightVector(rotation, new Vector3f());
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
