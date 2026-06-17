package com.example.internal.bridge.mixin;

import com.example.internal.bridge.events.GameClientEvents;
import com.example.internal.utils.DemoUtils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
  @Shadow public net.minecraft.client.player.ClientInput input;

  /// TODO 1.20.4 ?: [1.20.4, 1.21.11)
  @Inject(
      method = "aiStep",
      at =
          @At(
              value = "INVOKE",
              /*? if >1.21 {*/
              target = "Lnet/minecraft/client/player/ClientInput;tick()V",
              /*? } else {*/
              /*target = "Lnet/minecraft/client/player/Input;tick(ZF)V",
              *//*? }*/
              shift = At.Shift.AFTER))
  private void perspective$afterInputTick(CallbackInfo ci) {
    var moveVector = new Vector2f(input.getMoveVector().x, input.getMoveVector().y);
    GameClientEvents.TICK_KEYBOARD_INPUT.emit(moveVector);
    DemoUtils.setMoveVector(input, new Vec2(moveVector.x, moveVector.y));
  }
}
