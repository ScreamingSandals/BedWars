package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.lib.bukkit.block.state.SignBlockStateHolder;
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
            if (state.isEmpty() || (state.get() instanceof SignBlockStateHolder)) {
                return null;
            }
            var sState = state.get();

            if (!BedWarsPlugin.isLegacy()) {
                var data = sState.getType().get("facing");
                if (data.isPresent()) {
                    return location.add(BlockFace.valueOf(data.get()).getOppositeFace().getDirection()).getBlock();
                } else {
                    return location.add(BlockFace.DOWN.getDirection()).getBlock();
                }
            } else {
                if (sState.getType().isSameType("standing_sign")) {
                    return location.add(BlockFace.DOWN.getDirection()).getBlock();
                } else {
                    var data = sState.getType().legacyData();
                    switch (data) {
                        case 3:
                            return location.add(BlockFace.NORTH.getDirection()).getBlock();
                        case 4:
                            return location.add(BlockFace.EAST.getDirection()).getBlock();
                        case 5:
                            return location.add(BlockFace.WEST.getDirection()).getBlock();
                        case 2:
                        default:
                            return location.add(BlockFace.SOUTH.getDirection()).getBlock();
                    }
                }
            }
    }
}
