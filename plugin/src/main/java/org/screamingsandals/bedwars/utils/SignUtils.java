package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.lib.signs.ClickableSign;
import org.screamingsandals.lib.utils.BlockFace;
import org.screamingsandals.lib.block.BlockHolder;
import org.screamingsandals.lib.world.LocationHolder;

import java.util.Optional;

@UtilityClass
public class SignUtils {
    public Optional<BlockHolder> getBlockBehindSign(ClickableSign sign) {
        return Optional.ofNullable(getGlassBehind(sign));
    }

    private BlockHolder getGlassBehind(ClickableSign sign) {
            var location = sign.getLocation().as(LocationHolder.class);
            var block = location.getBlock();

            var state = block.getBlockState();
            if (state.isEmpty() || (state.get().as(BlockState.class) instanceof Sign)) {
                return null;
            }
            var bState = state.get().as(BlockState.class);

            if (!BedWarsPlugin.isLegacy()) {
                if (bState.getBlockData() instanceof org.bukkit.block.data.Directional) {
                    var directional = (org.bukkit.block.data.Directional) bState.getBlockData();
                    var blockFace = directional.getFacing().getOppositeFace();
                    return location.add(BlockFace.valueOf(blockFace.name()).getDirection()).getBlock();
                }
            } else {
                if (bState.getData() instanceof org.bukkit.material.Directional) {
                    var directional = (org.bukkit.material.Directional) bState.getData();
                    var blockFace = directional.getFacing().getOppositeFace();
                    return location.add(BlockFace.valueOf(blockFace.name()).getDirection()).getBlock();
                }
            }
            return location.add(BlockFace.DOWN.getDirection()).getBlock();
    }
}
