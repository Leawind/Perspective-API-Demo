package com.example.internal.bridge.events.context;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;

public class CameraSetupContext {
  public Camera camera;
  public Entity entity;
  public float partialTicks;

  public boolean cancelDefault;

  public CameraSetupContext() {}

  public void setup(Camera camera, Entity entity, float partialTicks) {
    this.camera = camera;
    this.entity = entity;
    this.partialTicks = partialTicks;
    this.cancelDefault = false;
  }

  public void cancelDefault() {
    this.cancelDefault = true;
  }
}
