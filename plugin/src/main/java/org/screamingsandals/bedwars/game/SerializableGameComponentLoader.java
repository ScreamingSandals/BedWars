package org.screamingsandals.bedwars.game;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Optional;

public interface SerializableGameComponentLoader<T> {
    @NotNull
    Optional<T> load(@NotNull GameImpl game, @NotNull ConfigurationNode node) throws ConfigurateException;
}
