package io.github.leawind.perspectiveapi.demo.internal.logic;

import io.github.leawind.perspectiveapi.api.PerspectiveAPI;
import io.github.leawind.perspectiveapi.demo.internal.bridge.events.GameClientEvents;
import io.github.leawind.perspectiveapi.demo.internal.logic.modifiers.ExplosionShakeModifier;
import io.github.leawind.perspectiveapi.demo.internal.logic.modifiers.ExplosionShakeState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ModEvents {
  private static final Logger LOGGER = LoggerFactory.getLogger(ModEvents.class);

  public static void register() {
    GameClientEvents.EXPLOSION.on(
        ctx -> ExplosionShakeState.INSTANCE.addEvent(ctx.center(), ctx.radius()));

    // Register the explosion shake modifier
    PerspectiveAPI.getModifierChain()
        .register(ExplosionShakeModifier.ID, Integer.MAX_VALUE, new ExplosionShakeModifier());
  }
}
