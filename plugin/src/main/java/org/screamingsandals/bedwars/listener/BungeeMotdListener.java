package org.screamingsandals.bedwars.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.game.Game;

public class BungeeMotdListener implements Listener {

    @EventHandler
    public void onServerListPing(ServerListPingEvent slpe) {
        if (Main.getGameNames().isEmpty()) {
            return;
        }

        Game game = Main.getGame(Main.getGameNames().get(0));

        if (game == null) {
            return;
        }

        String string = null;

        switch (game.getStatus()) {
            case DISABLED:
                string = Main.getConfigurator().config.getString("bungee.motd.disabled");
                break;
            case GAME_END_CELEBRATING:
            case RUNNING:
                string = Main.getConfigurator().config.getString("bungee.motd.running");
                break;
            case REBUILDING:
                string = Main.getConfigurator().config.getString("bungee.motd.rebuilding");
                break;
            case WAITING:
                if (game.countPlayers() >= game.getMaxPlayers()) {
                    string = Main.getConfigurator().config.getString("bungee.motd.waiting_full");
                } else {
                    string = Main.getConfigurator().config.getString("bungee.motd.waiting");
                }
                break;
        }

        if (string == null) {
            return; // WTF??
        }

        slpe.setMotd(string.replace("%name%", game.getName()).replace("%current%", Integer.toString(game.countPlayers())).replace("%max%", Integer.toString(game.getMaxPlayers())));
    }
}
