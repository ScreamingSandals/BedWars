package org.screamingsandals.bedwars.game.target;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.game.target.TargetBlock;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.SerializableGameComponent;
import org.screamingsandals.bedwars.game.SerializableGameComponentLoader;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.world.LocationHolder;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Objects;
import java.util.Optional;

@Data
public class TargetBlockImpl implements TargetBlock, SerializableGameComponent {
    @NotNull
    private final LocationHolder targetBlock;
    private boolean valid = true;

    @Override
    public boolean isEmpty() {
        if (!isValid()) {
            return false;
        }

        var blockType = targetBlock.getBlock().getType();
        if (!blockType.isSameType("respawn_anchor")) {
            return false;
        }

        var charges = blockType.getInt("charges");
        return charges.map(a -> a == 0).orElse(true);
    }

    @Override
    public int getCharge() {
        if (!isValid()) {
            return 0;
        }

        var blockType = targetBlock.getBlock().getType();
        if (!blockType.isSameType("respawn_anchor")) {
            return 1;
        }

        return blockType.getInt("charges").orElse(1);
    }

    @Override
    public void saveTo(@NotNull ConfigurationNode node) throws SerializationException {
        node.node("type").set("block");
        node.node("loc").set(MiscUtils.writeLocationToString(targetBlock));
    }

    public static class Loader implements SerializableGameComponentLoader<TargetBlockImpl> {
        public static final Loader INSTANCE = new Loader();

        @Override
        @NotNull
        public Optional<TargetBlockImpl> load(@NotNull GameImpl game, @NotNull ConfigurationNode node) throws ConfigurateException {
            if (!node.node("type").getString("").equals("block")) {
                return Optional.empty();
            }

            return Optional.of(new TargetBlockImpl(MiscUtils.readLocationFromString(game.getWorld(), Objects.requireNonNull(node.node("loc").getString()))));
        }
    }
}
