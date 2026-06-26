package com.example.internal.bridge.events.context;

public class MouseTurnPlayerContext {
  public double dx;
  public double dy;

  public boolean cancelDefault = false;

  public void setup(double dx, double dy) {

    this.dx = dx;
    this.dy = dy;

    cancelDefault = false;
  }

  public void cancelDefault() {
    cancelDefault = true;
  }
}
