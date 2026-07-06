package io.github.leawind.perspectiveapi.demo.internal.bridge.mixin;

import io.github.leawind.perspectiveapi.demo.internal.bridge.events.GameClientEvents;
import io.github.leawind.perspectiveapi.demo.internal.bridge.events.context.ExplosionContext;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

  @Inject(method = "handleExplosion", at = @At("HEAD"))
  private void onHandleExplosion(ClientboundExplodePacket packet, CallbackInfo ci) {
    /*? if >=1.21.11 {*/
    GameClientEvents.EXPLOSION.emit(new ExplosionContext(packet.center(), packet.radius()));
    /*? } else {*/
    /*GameClientEvents.EXPLOSION.emit(new ExplosionContext(
        new net.minecraft.world.phys.Vec3(packet.getX(), packet.getY(), packet.getZ()),
        packet.getPower()));
    *//*? }*/
  }
}
