package io.github.leawind.perspectiveapi.demo.internal.bridge.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.client.player.ClientInput.class)
public interface ClientInputAccessor {

  /*? if >1.21 {*/
  @Accessor("moveVector")
  void setMoveVector(net.minecraft.world.phys.Vec2 v);
  /*? } else {*/
  /*@Accessor("leftImpulse")
  void setLeftImpulse(float v);

  @Accessor("forwardImpulse")
  void setForwardImpulse(float v);
  *//*? }*/
}
