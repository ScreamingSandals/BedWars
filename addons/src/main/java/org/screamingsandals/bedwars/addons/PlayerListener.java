package org.screamingsandals.bedwars.addons;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.screamingsandals.bedwars.api.game.Game;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final Game game = Main.getApi().getFirstWaitingGame();

        if (game != null) {
            game.joinToGame(event.getPlayer());
        } else if (!player.hasPermission("misat11.bw.admin")) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();

            out.writeUTF("Connect");
            out.writeUTF(Main.getApi().getHubServerName());

            player.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
        }
    }
}
