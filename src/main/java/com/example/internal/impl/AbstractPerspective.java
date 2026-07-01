package com.example.internal.impl;

import io.github.leawind.perspectiveapi.api.Perspective;
import io.github.leawind.perspectiveapi.api.context.PerspectiveContext;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.jspecify.annotations.NonNull;

public abstract class AbstractPerspective implements Perspective {
  public final Vector3d position = new Vector3d();
  public final Quaternionf rotation = new Quaternionf();

  @Override
  public void applyTransform(
      @NonNull PerspectiveContext ctx, @NonNull Vector3d position, @NonNull Quaternionf rotation) {
    position.set(this.position);
    rotation.set(this.rotation);
  }
}
