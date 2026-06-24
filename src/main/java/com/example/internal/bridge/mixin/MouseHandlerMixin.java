package com.example.internal.bridge.mixin;

import com.example.internal.bridge.events.GameClientEvents;
import com.example.internal.bridge.events.MouseScrollContext;
import com.example.internal.bridge.events.context.MouseTurnPlayerContext;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

  @Unique private final MouseTurnPlayerContext context = new MouseTurnPlayerContext();

  @Shadow private double accumulatedDX;
  @Shadow private double accumulatedDY;

  /// 在根据鼠标位移转动玩家前触发
  /*? if >=1.21.11 {*/
  @ModifyArgs(
      method = "turnPlayer(D)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
  /*? } else {*/
  /*@ModifyArgs(
      method = "turnPlayer()V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
  */
  /*? } */
  private void preTurnPlayer(Args args) {
    double xo = args.get(0);
    double yo = args.get(1);
    context.setup(xo, yo);

    GameClientEvents.MOUSE_TURN_PLAYER.emit(context);

    if (context.cancelDefault) {
      args.set(0, 0D);
      args.set(1, 0D);
    }
  }

  @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
  private void onMouseScroll(long handle, double xOffset, double yOffset, CallbackInfo ci) {
    if (xOffset == 0 && yOffset == 0) return;
    var ctx = new MouseScrollContext();
    ctx.xOffset = xOffset;
    ctx.yOffset = yOffset;
    ctx.cancelDefault = false;
    GameClientEvents.MOUSE_SCROLL.emit(ctx);
    if (ctx.cancelDefault) {
      ci.cancel();
    }
  }
}
