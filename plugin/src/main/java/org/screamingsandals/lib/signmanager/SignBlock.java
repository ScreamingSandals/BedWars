package org.screamingsandals.lib.signmanager;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;

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
        Block block = loc.getBlock();

        if (!(block.getState() instanceof Sign)) {
            return null;
        }

        if (block.getState().getBlockData() instanceof Directional) {
            Directional directional = (Directional) block.getState().getBlockData();
            BlockFace blockFace = directional.getFacing().getOppositeFace();
            return block.getRelative(blockFace);
        }
        return block.getRelative(BlockFace.DOWN);
    }
}
