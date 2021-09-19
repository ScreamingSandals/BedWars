package org.screamingsandals.bedwars.listener;

import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.lib.block.BlockTypeHolder;
import org.screamingsandals.lib.event.player.SPlayerInteractEvent;

public class Player113ListenerUtils {
    public static void yummyCake(SPlayerInteractEvent event, GameImpl game) {
        var cake = event.getBlockClicked().getType();
        if (cake.get("bites").map("0"::equals).orElse(true)) {
            game.getRegion().putOriginalBlock(event.getBlockClicked().getLocation(), event.getBlockClicked().getBlockState().orElseThrow());
        }
        var bites = cake.get("bites").map(Integer::parseInt).orElse(0) + 1;
        cake = cake.with("bites", String.valueOf(bites));

        if (bites >= 6) {
            game.bedDestroyed(event.getBlockClicked().getLocation(), event.getPlayer(), false, false, true);
            event.getBlockClicked().setType(BlockTypeHolder.air());
        } else {
            event.getBlockClicked().setType(cake);
        }
    }
}
