package io.github.leawind.perspectiveapi.demo.internal.bridge.mixin;

import io.github.leawind.perspectiveapi.demo.internal.bridge.events.GameClientEvents;
import io.github.leawind.perspectiveapi.demo.internal.bridge.events.context.MouseTurnPlayerContext;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
  @Unique private final MouseTurnPlayerContext context = new MouseTurnPlayerContext();

  @Inject(method = "turn(DD)V", at = @At("HEAD"), cancellable = true)
  private void beforeTurn(double xo, double yo, CallbackInfo ci) {
    Entity entity = (Entity) (Object) this;
    if (!(entity instanceof LocalPlayer)) {
      return;
    }

    context.setup(xo, yo);
    GameClientEvents.MOUSE_TURN_PLAYER.emit(context);
    if (context.cancelDefault) {
      ci.cancel();
    }
  }
}
