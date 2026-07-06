package io.github.leawind.perspectiveapi.demo.internal.bridge.events.context;

import net.minecraft.world.phys.Vec3;

public record ExplosionContext(Vec3 center, float radius) {}
