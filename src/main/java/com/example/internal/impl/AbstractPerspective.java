package com.example.internal.impl;

import io.github.leawind.perspectiveapi.api.Perspective;
import io.github.leawind.perspectiveapi.api.PerspectiveState;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.jspecify.annotations.NonNull;

public abstract class AbstractPerspective implements Perspective {
  protected final Vector3d position = new Vector3d();
  protected final Quaternionf rotation = new Quaternionf();
  protected final PerspectiveState state = new PerspectiveState();

  @Override
  public @NonNull PerspectiveState getState() {
    state.position.set(position);
    state.hasPosition = true;
    state.rotation.set(rotation);
    state.hasRotation = true;
    return state;
  }
}
