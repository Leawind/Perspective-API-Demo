package com.example.internal.impl;

import com.google.auto.service.AutoService;
import io.github.leawind.perspectiveapi.api.PerspectiveManager;
import io.github.leawind.perspectiveapi.api.spi.PerspectiveRegistrar;

@SuppressWarnings("unused")
@AutoService(PerspectiveRegistrar.class)
public class ExamplePerspectiveRegistrar implements PerspectiveRegistrar {

  @Override
  public void register(PerspectiveManager manager) {
    manager.registry().register(SimpleThirdPersonPerspective.INSTANCE);
    manager.registry().register(FreeThirdPersonPerspective.INSTANCE);
    manager.registry().register(FreeCameraPerspective.INSTANCE);

    manager.cycler().add(SimpleThirdPersonPerspective.ID, 110);
    manager.cycler().add(FreeThirdPersonPerspective.ID, 120);
    manager.cycler().add(FreeCameraPerspective.ID, 130);
  }
}
