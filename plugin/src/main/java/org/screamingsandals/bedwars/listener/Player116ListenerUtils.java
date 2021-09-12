package org.screamingsandals.bedwars.listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.utils.Sounds;
import org.screamingsandals.lib.event.player.SPlayerInteractEvent;
import org.screamingsandals.lib.item.Item;

public class Player116ListenerUtils {
    public static boolean processAnchorDeath(GameImpl game, CurrentTeam team) {
        RespawnAnchor anchor = (RespawnAnchor) team.teamInfo.bed.getBlock().as(Block.class).getBlockData();
        int charges = anchor.getCharges();
        boolean isBed = true;
        if (charges <= 0) {
            isBed = false;
        } else {
            anchor.setCharges(charges - 1);
            team.teamInfo.bed.getBlock().as(Block.class).setBlockData(anchor);
            if (anchor.getCharges() == 0) {
                Sounds.playSound(team.teamInfo.bed.as(Location.class), MainConfig.getInstance().node("target-block", "respawn-anchor", "sound", "deplete").getString(), Sounds.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1, 1);
                game.updateScoreboard();
            } else {
                Sounds.playSound(team.teamInfo.bed.as(Location.class), MainConfig.getInstance().node("target-block", "respawn-anchor", "sound", "used").getString(), Sounds.BLOCK_GLASS_BREAK, 1, 1);
            }
        }
        return isBed;
    }

    public static boolean anchorCharge(SPlayerInteractEvent event, GameImpl game, Item stack) {
        boolean anchorFilled = false;
        RespawnAnchor anchor = (RespawnAnchor) event.getBlockClicked().as(Block.class).getBlockData();
        int charges = anchor.getCharges();
        charges++;
        if (charges <= anchor.getMaximumCharges()) {
            anchorFilled = true;
            anchor.setCharges(charges);
            event.getBlockClicked().as(Block.class).setBlockData(anchor);
            stack.setAmount(stack.getAmount() - 1);
            Sounds.playSound(event.getBlockClicked().getLocation().as(Location.class), MainConfig.getInstance().node("target-block", "respawn-anchor", "sound", "charge").getString(), Sounds.BLOCK_RESPAWN_ANCHOR_CHARGE, 1, 1);
            game.updateScoreboard();
        }
        return anchorFilled;
    }

    public static boolean isAnchorEmpty(Block anchor) {
        return ((RespawnAnchor) anchor.getBlockData()).getCharges() == 0;
    }
}
