package io.github.leawind.perspectiveapi.demo.internal.utils;

import io.github.leawind.perspectiveapi.demo.internal.bridge.mixin.ClientInputAccessor;
import net.minecraft.world.phys.Vec2;

public final class DemoUtils {
  private DemoUtils() {}

  public static void setMoveVector(net.minecraft.client.player.ClientInput input, Vec2 v) {
    var cia = (ClientInputAccessor) input;

    /*? if >1.21 {*/
    cia.setMoveVector(v);
    /*? } else {*/
    /*cia.setLeftImpulse(v.x);
    cia.setForwardImpulse(v.y);
    *//*? }*/
  }
}
