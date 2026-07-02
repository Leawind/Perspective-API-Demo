package com.example.internal.bridge.mixin;

import com.example.internal.bridge.events.GameClientEvents;
import com.example.internal.bridge.events.MouseScrollContext;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
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
