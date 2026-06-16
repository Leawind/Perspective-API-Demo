package com.example.internal.impl;

import com.google.auto.service.AutoService;
import io.github.leawind.perspectiveapi.api.Perspective;
import io.github.leawind.perspectiveapi.api.PerspectiveHelper;
import io.github.leawind.perspectiveapi.api.anno.CyclicPerspective;
import io.github.leawind.perspectiveapi.api.context.PerspectiveTickContext;
import io.github.leawind.perspectiveapi.internal.logic.AbstractPerspective;
import io.github.leawind.perspectiveapi.platform.api.Services;
import net.minecraft.client.CameraType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("unused")
@CyclicPerspective
@AutoService(Perspective.class)
public class ExamplePerspective extends AbstractPerspective {
  public ExamplePerspective() {}

  private static final Identifier ID =
      Services.PLATFORM_HELPER.createIdentifier("example", "simple_third_person");

  @Override
  public @NonNull Identifier id() {
    return ID;
  }

  @Override
  public @NonNull CameraType cameraType() {
    return CameraType.THIRD_PERSON_BACK;
  }

  @Override
  public void tick(PerspectiveTickContext context) {
    Entity entity = context.entity();
    if (entity == null) {
      return;
    }

    PerspectiveHelper.getRotation(entity.getRotationVector(), rotation);

    var backward = PerspectiveHelper.getBackwardVector(rotation, new Vector3f());
    var right = PerspectiveHelper.getRightVector(rotation, new Vector3f());
    var pos = entity.getEyePosition(context.partialTicks());
    position.set(pos.x, pos.y + 1, pos.z).add(backward.mul(2.5f)).add(right.mul(1));
  }
}
