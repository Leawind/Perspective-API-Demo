/*? if fabric {*/
package io.github.leawind.perspectiveapi.demo.platform.fabric;

import io.github.leawind.perspectiveapi.demo.internal.logic.ModEntrypoint;
import net.fabricmc.api.ClientModInitializer;

@SuppressWarnings("unused")
public final class Entrypoint implements ClientModInitializer {
  public void onInitializeClient() {
    ModEntrypoint.initialize();
    initialize();
  }

  private void initialize() {}
}
/*?}*/
