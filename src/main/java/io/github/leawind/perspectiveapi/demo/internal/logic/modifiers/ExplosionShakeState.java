package io.github.leawind.perspectiveapi.demo.internal.logic.modifiers;

import java.util.ArrayDeque;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

public final class ExplosionShakeState {
  public static final ExplosionShakeState INSTANCE = new ExplosionShakeState();

  private static final double MAX_AGE_SECONDS = 60.0;

  private final ArrayDeque<ExplosionEvent> events = new ArrayDeque<>();

  private ExplosionShakeState() {}

  public void addEvent(Vec3 center, float radius) {
    double now = GLFW.glfwGetTime();
    events.addLast(new ExplosionEvent(center, radius, now));
  }

  public Iterable<ExplosionEvent> getActiveEvents() {
    double now = GLFW.glfwGetTime();
    events.removeIf(explosionEvent -> (now - explosionEvent.time) > MAX_AGE_SECONDS);
    events.removeIf(ExplosionEvent::shouldRemove);
    return events;
  }

  public static final class ExplosionEvent {
    public final Vec3 center;
    public final float radius;
    public final double time;
    public boolean shouldRemove = false;

    public ExplosionEvent(Vec3 center, float radius, double time) {
      this.center = center;
      this.radius = radius;
      this.time = time;
    }

    public boolean shouldRemove() {
      return shouldRemove;
    }
  }
}
