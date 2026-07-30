package io.github.leawind.perspectiveapi.demo.internal.logic.perspectives;

import com.google.auto.service.AutoService;
import io.github.leawind.perspectiveapi.api.PerspectiveAPI;
import io.github.leawind.perspectiveapi.api.PerspectiveBehavior;
import io.github.leawind.perspectiveapi.api.PerspectiveMath;
import io.github.leawind.perspectiveapi.api.PerspectiveState;
import io.github.leawind.perspectiveapi.api.ProjectionMode;
import io.github.leawind.perspectiveapi.api.context.PerspectiveContext;
import io.github.leawind.perspectiveapi.demo.internal.bridge.events.GameClientEvents;
import io.github.leawind.perspectiveapi.demo.internal.bridge.events.MouseScrollContext;
import io.github.leawind.perspectiveapi.demo.internal.utils.ExpSmoothDouble;
import io.github.leawind.perspectiveapi.demo.internal.utils.ExpSmoothFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

/// Orthographic variant of vanilla third person.
///
/// - Scroll changes the visible world height.
/// - Sprint + scroll changes the fixed distance from the camera to the player.
@SuppressWarnings({"unused", "UnstableApiUsage"})
@AutoService(PerspectiveBehavior.class)
@PerspectiveBehavior.Info(
    id = OrthographicThirdPersonPerspective.ID,
    priority = 10,
    baseType = PerspectiveBehavior.BaseType.THIRD_PERSON_BACK)
public final class OrthographicThirdPersonPerspective implements PerspectiveBehavior {
  public static final String ID = "perspective_api_demo.orthographic_third_person";

  private static final float MIN_ORTHOGRAPHIC_HEIGHT = 0.0625f;
  private static final float MAX_ORTHOGRAPHIC_HEIGHT = 1024.0f;
  private static final double MIN_CAMERA_DISTANCE = 0.125;
  private static final double MAX_CAMERA_DISTANCE = 2048.0;
  private static final double SCROLL_BASE = 1.1487;

  private final Vector3f backward = new Vector3f();
  private final ExpSmoothFloat smoothOrthographicHeight = new ExpSmoothFloat(0.01, 6.0f);
  private final ExpSmoothDouble smoothCameraDistance = new ExpSmoothDouble(0.01, 8.0);

  @Override
  public void init() {
    GameClientEvents.MOUSE_SCROLL.on(this::onMouseScroll);
  }

  @Override
  public void applyCameraState(
      PerspectiveState.@NonNull Mutable state, @NonNull PerspectiveContext context) {
    Entity entity = context.cameraEntity();
    if (entity == null) return;

    double now = GLFW.glfwGetTime();
    double cameraDistance = smoothCameraDistance.get(now);
    var eyePosition = entity.getEyePosition(context.partialTicks());
    PerspectiveMath.getBackward(state.rotation(), backward);
    state
        .position()
        .set(eyePosition.x, eyePosition.y, eyePosition.z)
        .add(backward.x * cameraDistance, backward.y * cameraDistance, backward.z * cameraDistance);
    state.setProjectionMode(ProjectionMode.ORTHOGRAPHIC);
    state.setOrthographicHeight(smoothOrthographicHeight.get(now));
  }

  private void onMouseScroll(MouseScrollContext input) {
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft.isPaused() || !PerspectiveAPI.isCurrent(ID)) return;

    double zoomFactor = Math.pow(SCROLL_BASE, -input.yOffset);
    if (minecraft.options.keySprint.isDown()) {
      smoothCameraDistance.setTarget(
          clamp(
              smoothCameraDistance.getTarget() * zoomFactor,
              MIN_CAMERA_DISTANCE,
              MAX_CAMERA_DISTANCE));
    } else {
      smoothOrthographicHeight.setTarget(
          (float)
              clamp(
                  smoothOrthographicHeight.getTarget() * zoomFactor,
                  MIN_ORTHOGRAPHIC_HEIGHT,
                  MAX_ORTHOGRAPHIC_HEIGHT));
    }
    input.cancelDefault = true;
  }

  @SuppressWarnings("MathClampMigration")
  private double clamp(double v, double min, double max) {
    return Math.max(min, Math.min(max, v));
  }
}
