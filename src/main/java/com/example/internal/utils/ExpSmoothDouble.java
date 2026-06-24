package com.example.internal.utils;

public final class ExpSmoothDouble {
  private double halflife;

  private double previousTime;
  private double previousValue;

  private double targetValue;

  public ExpSmoothDouble(double halflife, double initValue) {
    this.halflife = halflife;
    previousTime = 0;
    previousValue = targetValue = initValue;
  }

  public double getHalflife() {
    return halflife;
  }

  public ExpSmoothDouble setHalflife(double halflife) {
    this.halflife = halflife;
    return this;
  }

  public double getTarget() {
    return targetValue;
  }

  public void setTarget(double target) {
    this.targetValue = target;
  }

  public double get(double now) {
    if (halflife <= 0) {
      previousTime = now;
      return previousValue = targetValue;
    }

    double decayFactor = Math.pow(0.5, (now - previousTime) / halflife);
    double gap = targetValue - previousValue;
    double smoothValue = previousValue + gap * (1 - decayFactor);

    previousTime = now;
    return previousValue = smoothValue;
  }
}
