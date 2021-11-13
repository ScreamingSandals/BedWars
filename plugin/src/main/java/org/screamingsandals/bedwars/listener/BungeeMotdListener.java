package org.screamingsandals.bedwars.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.Plugin;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.methods.ShouldRunControllable;

@Service
public class BungeeMotdListener implements Listener {
    @ShouldRunControllable
    public boolean isEnabled() {
        return MainConfig.getInstance().node("bungee", "enabled").getBoolean() && MainConfig.getInstance().node("bungee", "motd", "enabled").getBoolean();
    }

    @OnPostEnable
    public void onPostEnable(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent slpe) {
        var games = GameManagerImpl.getInstance().getGames();
        if (games.isEmpty()) {
            return;
        }

        GameImpl game = games.get(0);

        if (game == null) {
            return;
        }

        String string = null;

        switch (game.getStatus()) {
            case DISABLED:
                string = MainConfig.getInstance().node("bungee", "motd", "disabled").getString();
                break;
            case GAME_END_CELEBRATING:
            case RUNNING:
                string = MainConfig.getInstance().node("bungee", "motd", "running").getString();
                break;
            case REBUILDING:
                string = MainConfig.getInstance().node("bungee", "motd", "rebuilding").getString();
                break;
            case WAITING:
                if (game.countPlayers() >= game.getMaxPlayers()) {
                    string = MainConfig.getInstance().node("bungee", "motd", "waiting_full").getString();
                } else {
                    string = MainConfig.getInstance().node("bungee", "motd", "waiting").getString();
                }
                break;
        }

        if (string == null) {
            return; // WTF??
        }

        slpe.setMotd(string.replace("%name%", game.getName()).replace("%displayName%", game.getDisplayName() != null ? game.getDisplayName() : game.getName()).replace("%current%", Integer.toString(game.countPlayers())).replace("%max%", Integer.toString(game.getMaxPlayers())));
    }
}
