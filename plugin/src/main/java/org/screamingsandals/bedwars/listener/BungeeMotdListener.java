package org.screamingsandals.bedwars.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GameManager;

public class BungeeMotdListener implements Listener {

    @EventHandler
    public void onServerListPing(ServerListPingEvent slpe) {
        var games = GameManager.getInstance().getGames();
        if (games.isEmpty()) {
            return;
        }

        Game game = games.get(0);

        if (game == null) {
            return;
        }

        String string = null;

        switch (game.getStatus()) {
            case DISABLED:
                string = Main.getConfigurator().node("bungee", "motd", "disabled").getString();
                break;
            case GAME_END_CELEBRATING:
            case RUNNING:
                string = Main.getConfigurator().node("bungee", "motd", "running").getString();
                break;
            case REBUILDING:
                string = Main.getConfigurator().node("bungee", "motd", "rebuilding").getString();
                break;
            case WAITING:
                if (game.countPlayers() >= game.getMaxPlayers()) {
                    string = Main.getConfigurator().node("bungee", "motd", "waiting_full").getString();
                } else {
                    string = Main.getConfigurator().node("bungee", "motd", "waiting").getString();
                }
                break;
        }

        if (string == null) {
            return; // WTF??
        }

        slpe.setMotd(string.replace("%name%", game.getName()).replace("%current%", Integer.toString(game.countPlayers())).replace("%max%", Integer.toString(game.getMaxPlayers())));
    }
}
