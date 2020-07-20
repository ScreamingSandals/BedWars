package org.screamingsandals.lib.signmanager;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;

public class SignBlock {
    private final Location loc;
    private final String name;

    public SignBlock(Location loc, String name) {
        this.loc = loc;
        this.name = name;
    }

    public Location getLocation() {
        return loc;
    }

    public String getName() {
        return name;
    }

    public Optional<Block> getBlockBehindSign() {
        return Optional.ofNullable(getGlassBehind());
    }

    private Block getGlassBehind() {
        final Block block = loc.getBlock();

        if (!(block instanceof Sign)) {
            return null;
        }

        final WallSign wallSign = (WallSign) block.getState().getBlockData();
        final BlockFace blockFace = wallSign.getFacing().getOppositeFace();

        final Block glassBlock = block.getRelative(blockFace);

        if (glassBlock.getType().name().contains("GLASS")) {
            return glassBlock;
        }

        return null;
    }
}
