package io.github.leawind.perspectiveapi.demo.internal.logic.perspectives;

import com.google.auto.service.AutoService;
import io.github.leawind.perspectiveapi.api.PerspectiveAPI;
import io.github.leawind.perspectiveapi.api.PerspectiveRegistry;
import io.github.leawind.perspectiveapi.api.spi.PerspectiveRegistrar;
import io.github.leawind.perspectiveapi.demo.internal.logic.modifiers.ExplosionShakeModifier;

@SuppressWarnings("unused")
@AutoService(PerspectiveRegistrar.class)
public class ExamplePerspectiveRegistrar implements PerspectiveRegistrar {

  @Override
  public void register(PerspectiveRegistry registry) {
    registry.register(SimpleThirdPersonPerspective.INSTANCE);
    registry.register(FreeThirdPersonPerspective.INSTANCE);
    registry.register(FreeCameraPerspective.INSTANCE);

    PerspectiveAPI.getWheel().register(SimpleThirdPersonPerspective.ID, 110);
    PerspectiveAPI.getWheel().register(FreeThirdPersonPerspective.ID, 120);
    PerspectiveAPI.getWheel().register(FreeCameraPerspective.ID, 130);

    PerspectiveAPI.getModifierChain()
        .register(ExplosionShakeModifier.ID, Integer.MAX_VALUE, new ExplosionShakeModifier());
  }
}
