package org.screamingsandals.bedwars.utils;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.game.Game;
import org.bukkit.Location;

public class GameSign {
    private final Location loc;
    private final String game;

    public GameSign(Location loc, String game) {
        this.loc = loc;
        this.game = game;
    }

    public Location getLocation() {
        return loc;
    }

    public Game getGame() {
        return Main.getGame(game);
    }

    public String getGameName() {
        return game;
    }
}
