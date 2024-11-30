package org.screamingsandals.bedwars.api.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.variants.Variant;

/**
 * @since 0.3.0
 */
public interface ItemSpawnerTypeHolder {
    /**
     * @since 0.3.0
     */
    @NotNull String configKey();

    /**
     * @since 0.3.0
     */
    @Nullable ItemSpawnerType toSpawnerType(@NotNull LocalGame variant);

    /**
     * @since 0.3.0
     */
    @Nullable ItemSpawnerType toSpawnerType(@Nullable Variant variant);

    /**
     * @since 0.3.0
     */
    default boolean isValid(@NotNull LocalGame game) {
        return toSpawnerType(game) != null;
    }

    /**
     * @since 0.3.0
     */
    default boolean isValid(@Nullable Variant variant) {
        return toSpawnerType(variant) != null;
    }
}
