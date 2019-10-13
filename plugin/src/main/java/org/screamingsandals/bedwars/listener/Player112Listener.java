package org.screamingsandals.bedwars.listener;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class Player112Listener implements Listener {
    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (Main.isPlayerInGame(player)) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
            Game game = gPlayer.getGame();
            if (game.getStatus() == GameStatus.WAITING || gPlayer.isSpectator) {
                event.setCancelled(true);
            }
        }

    }
}
