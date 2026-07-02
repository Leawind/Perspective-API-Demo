package com.example.internal.bridge.events;

import com.example.internal.bridge.events.context.MouseTurnPlayerContext;
import io.github.leawind.perspectiveapi.internal.utils.event.SimpleEventEmitter;
import net.minecraft.client.Minecraft;
import org.joml.Vector2f;

public final class GameClientEvents {
  private GameClientEvents() {}

  public static final SimpleEventEmitter.Owned<MouseTurnPlayerContext> MOUSE_TURN_PLAYER =
      SimpleEventEmitter.create();

  public static final SimpleEventEmitter.Owned<MouseScrollContext> MOUSE_SCROLL =
      SimpleEventEmitter.create();

  public static final SimpleEventEmitter.Owned<Vector2f> TICK_KEYBOARD_INPUT =
      SimpleEventEmitter.create();

  public static final SimpleEventEmitter.Owned<Minecraft> HANDLE_KEYBINDS_START =
      SimpleEventEmitter.create();
}
