package io.github.leawind.perspectiveapi.demo.internal.logic;

import io.github.leawind.perspectiveapi.api.PerspectiveAPI;
import io.github.leawind.perspectiveapi.demo.internal.bridge.events.GameClientEvents;
import io.github.leawind.perspectiveapi.demo.internal.logic.modifiers.ExplosionShakeModifier;
import io.github.leawind.perspectiveapi.demo.internal.logic.modifiers.ExplosionShakeState;

public final class ModEvents {
  public static void register() {
    GameClientEvents.EXPLOSION.on(
        ctx -> ExplosionShakeState.INSTANCE.addEvent(ctx.center(), ctx.radius()));

    PerspectiveAPI.runWhenReady(
        ExplosionShakeModifier.ID,
        () ->
            PerspectiveAPI.getModifierChain()
                .register(
                    ExplosionShakeModifier.ID,
                    Integer.MAX_VALUE,
                    new ExplosionShakeModifier()));
  }
}
