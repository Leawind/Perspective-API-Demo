package io.github.leawind.perspectiveapi.demo.internal.logic.perspectives;

import com.google.auto.service.AutoService;
import io.github.leawind.perspectiveapi.api.PerspectiveManager;
import io.github.leawind.perspectiveapi.api.spi.PerspectiveRegistrar;
import io.github.leawind.perspectiveapi.demo.internal.logic.modifiers.ExplosionShakeModifier;

@SuppressWarnings("unused")
@AutoService(PerspectiveRegistrar.class)
public class ExamplePerspectiveRegistrar implements PerspectiveRegistrar {

  @Override
  public void register(PerspectiveManager manager) {
    manager.registry().register(SimpleThirdPersonPerspective.INSTANCE);
    manager.registry().register(FreeThirdPersonPerspective.INSTANCE);
    manager.registry().register(FreeCameraPerspective.INSTANCE);

    manager.cycler().add(SimpleThirdPersonPerspective.ID, 110);
    manager.cycler().add(FreeThirdPersonPerspective.ID, 120);
    manager.cycler().add(FreeCameraPerspective.ID, 130);

    manager
        .modifiers()
        .register(
            ExplosionShakeModifier.ID, Integer.MAX_VALUE, new ExplosionShakeModifier());
  }
}
