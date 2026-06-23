package com.example.internal.bridge.events;

import com.example.internal.bridge.events.context.MouseTurnPlayerContext;
import io.github.leawind.inventory.event.SimpleEventEmitter;
import net.minecraft.client.Minecraft;
import org.joml.Vector2f;

public final class GameClientEvents {
  private GameClientEvents() {}

  public static final SimpleEventEmitter.Owned<MouseTurnPlayerContext> MOUSE_TURN_PLAYER =
      SimpleEventEmitter.create();

  /// 修改移动冲量，移动冲量由向前和向左的冲量组成
  ///
  /// 当玩家按下移动键（WASD）时，会触发此事件，并传递一个包含冲量值的上下文对象
  public static final SimpleEventEmitter.Owned<Vector2f> TICK_KEYBOARD_INPUT =
      SimpleEventEmitter.create();

  public static final SimpleEventEmitter.Owned<Minecraft> HANDLE_KEYBINDS_START =
      SimpleEventEmitter.create();
}
