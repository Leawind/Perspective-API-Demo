package com.example.internal.logic;

import io.github.leawind.perspectiveapi.api.PerspectiveManager;

public final class ModEntrypoint {
  private ModEntrypoint() {}

  public static void initialize() {
    registerPerspectives();

    ModEvents.register();
  }

  private static void registerPerspectives() {
    var manager = PerspectiveManager.get();
  }
}
