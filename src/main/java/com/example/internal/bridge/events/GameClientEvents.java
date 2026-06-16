package com.example.internal.bridge.events;

import com.example.internal.bridge.events.context.CameraSetupContext;
import io.github.leawind.inventory.event.SingleEventEmitter;
import net.minecraft.client.Minecraft;

public final class GameClientEvents {
  private GameClientEvents() {}

  public static final SingleEventEmitter<Minecraft> READY = new SingleEventEmitter<>();

  public static final SingleEventEmitter<CameraSetupContext> JOINED_WORLD =
      new SingleEventEmitter<>();
}
