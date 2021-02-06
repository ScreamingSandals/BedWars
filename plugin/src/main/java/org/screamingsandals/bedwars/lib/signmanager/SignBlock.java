package org.screamingsandals.bedwars.lib.signmanager;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.utils.PreparedLocation;

public class SignBlock {
    private final PreparedLocation loc;
    private final String name;

    public SignBlock(PreparedLocation loc, String name) {
        this.loc = loc;
        this.name = name;
    }

    public PreparedLocation getLocation() {
        return loc;
    }

    public String getName() {
        return name;
    }

    public Optional<Block> getBlockBehindSign() {
        return Optional.ofNullable(getGlassBehind());
    }

    private Block getGlassBehind() {
        var bukkitLoc = loc.asOptional(Location.class);
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
