package org.screamingsandals.bedwars.game;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public interface SerializableGameComponent {
    void saveTo(@NotNull ConfigurationNode node) throws SerializationException;
}
