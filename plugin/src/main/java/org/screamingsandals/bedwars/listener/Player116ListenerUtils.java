package org.screamingsandals.bedwars.listener;

import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.utils.Sounds;
import org.screamingsandals.lib.block.BlockHolder;
import org.screamingsandals.lib.event.player.SPlayerInteractEvent;
import org.screamingsandals.lib.item.Item;

public class Player116ListenerUtils {
    public static boolean processAnchorDeath(GameImpl game, CurrentTeam team) {
        var anchor = team.getTargetBlock().getBlock().getType();
        int charges = anchor.get("charges").map(Integer::parseInt).orElse(0);
        if (charges > 0) {
            var c = charges - 1;
            team.getTargetBlock().getBlock().setType(anchor.with("charges", String.valueOf(c)));
            if (c == 0) {
                Sounds.playSound(team.getTargetBlock(), MainConfig.getInstance().node("target-block", "respawn-anchor", "sound", "deplete").getString(), Sounds.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1, 1);
                game.updateScoreboard();
            } else {
                Sounds.playSound(team.getTargetBlock(), MainConfig.getInstance().node("target-block", "respawn-anchor", "sound", "used").getString(), Sounds.BLOCK_GLASS_BREAK, 1, 1);
            }
            return true;
        }
        return false;
    }

    public static boolean anchorCharge(SPlayerInteractEvent event, GameImpl game, Item stack) {
        boolean anchorFilled = false;
        var anchor = event.getBlockClicked().getType();
        int charges = anchor.get("charges").map(Integer::parseInt).orElse(0);
        charges++;
        if (charges <= 4) {
            anchorFilled = true;
            event.getBlockClicked().setType(anchor.with("charges", String.valueOf(charges)));
            stack.setAmount(stack.getAmount() - 1);
            Sounds.playSound(event.getBlockClicked().getLocation(), MainConfig.getInstance().node("target-block", "respawn-anchor", "sound", "charge").getString(), Sounds.BLOCK_RESPAWN_ANCHOR_CHARGE, 1, 1);
            game.updateScoreboard();
        }
        return anchorFilled;
    }

    public static boolean isAnchorEmpty(BlockHolder anchor) {
        var charges = anchor.getType().get("charges");
        return charges.map("0"::equals).orElse(true);
    }
}
