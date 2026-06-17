package com.example.internal.bridge.events.context;

public class MouseTurnPlayerContext {
  public double dx;
  public double dy;

  /// 是否取消默认操作
  ///
  /// 如果设置为 true，则后续的玩家旋转处理将会被取消，鼠标累积位移量也会被重置
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
