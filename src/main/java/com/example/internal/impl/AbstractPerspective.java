package com.example.internal.impl;

import io.github.leawind.perspectiveapi.api.Perspective;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.jspecify.annotations.NonNull;

public abstract class AbstractPerspective implements Perspective {
  protected final Vector3d position = new Vector3d();
  protected final Quaternionf rotation = new Quaternionf();

  @Override
  public @NonNull Vector3dc getPosition() {
    return position;
  }

  @Override
  public @NonNull Quaternionfc getRotation() {
    return rotation;
  }
}
