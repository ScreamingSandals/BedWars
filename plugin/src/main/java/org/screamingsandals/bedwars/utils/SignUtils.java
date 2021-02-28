package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.material.Directional;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.lib.signs.ClickableSign;

import java.util.Optional;

@UtilityClass
public class SignUtils {
    public Optional<Block> getBlockBehindSign(ClickableSign sign) {
        return Optional.ofNullable(getGlassBehind(sign));
    }

    private Block getGlassBehind(ClickableSign sign) {
        var bukkitLoc = sign.getLocation().asOptional(Location.class);
        if (bukkitLoc.isPresent()) {
            Block block = bukkitLoc.get().getBlock();

            if (!(block.getState() instanceof Sign)) {
                return null;
            }

            if (!Main.isLegacy()) {
                if (block.getState().getBlockData() instanceof Directional) {
                    Directional directional = (Directional) block.getState().getBlockData();
                    BlockFace blockFace = directional.getFacing().getOppositeFace();
                    return block.getRelative(blockFace);
                }
            } else {
                if (block.getState().getData() instanceof org.bukkit.material.Directional) {
                    org.bukkit.material.Directional directional = (org.bukkit.material.Directional) block.getState().getData();
                    BlockFace blockFace = directional.getFacing().getOppositeFace();
                    return block.getRelative(blockFace);
                }
            }
            return block.getRelative(BlockFace.DOWN);
        }
        return null;
    }
}
