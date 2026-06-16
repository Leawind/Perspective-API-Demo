/*? if fabric {*/
package com.example.platform.fabric;

import com.example.internal.logic.ModEntrypoint;
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
