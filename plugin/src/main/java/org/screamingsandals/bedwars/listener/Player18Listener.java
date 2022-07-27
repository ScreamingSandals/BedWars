package org.screamingsandals.bedwars.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GameCreator;
import org.screamingsandals.bedwars.game.GamePlayer;

public class Player18Listener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        if (Main.isPlayerInGame(player)) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
            Game game = gPlayer.getGame();
            if (game.getOriginalOrInheritedDamageWhenPlayerIsNotInArena() && game.getStatus() == GameStatus.RUNNING
                    && !gPlayer.isSpectator) {
                if (!GameCreator.isInArea(event.getTo(), game.getPos1(), game.getPos2())) {
                    // TODO: Misat work your black magic calclations here :)
                    final double damageAmount = 5;
                    player.damage(damageAmount);
                }
            } else if (Main.getConfigurator().config.getBoolean("preventSpectatorFlyingAway", false) && gPlayer.isSpectator && (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING)) {
                if (!GameCreator.isInArea(event.getTo(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
