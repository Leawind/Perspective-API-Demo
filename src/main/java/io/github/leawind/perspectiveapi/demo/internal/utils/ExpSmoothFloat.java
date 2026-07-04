package io.github.leawind.perspectiveapi.demo.internal.utils;

public final class ExpSmoothFloat {
  private double halflife;

  private double previousTime;
  private float previousValue;

  private float targetValue;

  public ExpSmoothFloat(double halflife, float initValue) {
    this.halflife = halflife;
    previousTime = 0;
    previousValue = targetValue = initValue;
  }

  public double getHalflife() {
    return halflife;
  }

  public ExpSmoothFloat setHalflife(double halflife) {
    this.halflife = halflife;
    return this;
  }

  public float getTarget() {
    return targetValue;
  }

  public void setTarget(float target) {
    this.targetValue = target;
  }

  public float get(double now) {
    if (halflife <= 0) {
      previousTime = now;
      return previousValue = targetValue;
    }

    double decayFactor = Math.pow(0.5, (now - previousTime) / halflife);
    double gap = targetValue - previousValue;
    float smoothValue = previousValue + (float) (gap * (1 - decayFactor));

    previousTime = now;
    return previousValue = smoothValue;
  }
}
