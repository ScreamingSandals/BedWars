package org.screamingsandals.bedwars.game;

import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.game.ItemSpawnerTypeHolder;
import org.screamingsandals.bedwars.api.game.LocalGame;
import org.screamingsandals.bedwars.api.variants.Variant;
import org.screamingsandals.bedwars.variants.VariantImpl;
import org.screamingsandals.bedwars.variants.VariantManagerImpl;

@Data
@Accessors(fluent = true)
public class ItemSpawnerTypeHolderImpl implements ItemSpawnerTypeHolder {
    private final @NotNull String configKey;

    @Override
    public @Nullable ItemSpawnerTypeImpl toSpawnerType(@NotNull LocalGame game) {
        return toSpawnerType(game.getGameVariant());
    }

    @Override
    public @Nullable ItemSpawnerTypeImpl toSpawnerType(@Nullable Variant variant) {
        if (variant == null) {
            variant = VariantManagerImpl.getInstance().getDefaultVariant();
        }
        if (!(variant instanceof VariantImpl)) {
            throw new IllegalArgumentException("Invalid variant type: " + variant);
        }
        return ((VariantImpl) variant).getItemSpawnerType(configKey);
    }
}
