package com.example.internal.logic;

import com.example.internal.impl.ExamplePerspective;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientRawInputEvent;
import io.github.leawind.perspectiveapi.api.PerspectiveManager;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ModEvents {
  private static final Logger LOGGER = LoggerFactory.getLogger(ModEvents.class);

  public static void register() {
    ClientLifecycleEvent.CLIENT_STARTED.register((minecraft) -> {});

    ClientGuiEvent.SET_SCREEN.register(
        (screen) -> {
          // TODO 如果是主屏幕，进入世界
          return CompoundEventResult.pass();
        });

    ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(
        (player) -> {
          LOGGER.info("Player joined");
          // TODO
        });

    ClientRawInputEvent.KEY_PRESSED.register(
        (minecraft, action, event) -> {
          if (action == 1) {
            switch (event.key()) {
              case InputConstants.KEY_BACKSLASH ->
                  PerspectiveManager.get().cycler().switchToNextAvailable();
              case InputConstants.KEY_RBRACKET -> {
                // TODO
                LOGGER.info("Hey!!!");
              }
            }
          }

          return EventResult.pass();
        });

    ClientRawInputEvent.MOUSE_SCROLLED.register(
        (minecraft, x, y) -> {

          return EventResult.pass();
        });
  }
}
