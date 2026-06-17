package com.example.platform.neoforge;

/*? if neoforge {*/
/*import com.example.PerspectiveAPIDemo;
import com.example.internal.logic.ModEntrypoint;
/^?   if >=1.21.11 {^/
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(value = PerspectiveAPIDemo.MOD_ID, dist = Dist.CLIENT)
public final class Entrypoint {
  public Entrypoint(IEventBus modBus) {
    ModEntrypoint.initialize();
    initialize();
  }

  private void initialize() {}

  @EventBusSubscriber(modid = PerspectiveAPIDemo.MOD_ID)
  public static class EventHandler {
  }
}

/^?   } else {^/
/^import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(value = PerspectiveAPIDemo.MOD_ID)
public final class Entrypoint {
  public Entrypoint(IEventBus modBus) {
    if (FMLEnvironment.dist != Dist.CLIENT) {
      return;
    }
    ModEntrypoint.initialize();
    initialize();
  }

  private void initialize() {}
}
^//^?   }^/

*//*? }*/
