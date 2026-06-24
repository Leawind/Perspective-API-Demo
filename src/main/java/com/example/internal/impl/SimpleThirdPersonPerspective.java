package com.example.internal.impl;

import io.github.leawind.perspectiveapi.api.PerspectiveHelper;
import io.github.leawind.perspectiveapi.api.context.PerspectiveRenderTickContext;
import io.github.leawind.perspectiveapi.internal.bridge.Bridge;
import net.minecraft.client.CameraType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public class SimpleThirdPersonPerspective extends AbstractPerspective {
  public static final SimpleThirdPersonPerspective INSTANCE = new SimpleThirdPersonPerspective();

  public static final Identifier ID = Bridge.createIdentifier("example", "simple_third_person");

  @Override
  public @NonNull Identifier id() {
    return ID;
  }

  @Override
  public @NonNull CameraType cameraType() {
    return CameraType.THIRD_PERSON_BACK;
  }

  @Override
  public void renderTick(PerspectiveRenderTickContext context) {
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

  // TODO override fov

  @Override
  public Float getFieldOfView() {
    return null;
  }
}
