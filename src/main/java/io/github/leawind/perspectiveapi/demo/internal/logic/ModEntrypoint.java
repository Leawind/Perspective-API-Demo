package io.github.leawind.perspectiveapi.demo.internal.logic;

public final class ModEntrypoint {
  private ModEntrypoint() {}

  public static void initialize() {
    ModEvents.register();
  }
}
