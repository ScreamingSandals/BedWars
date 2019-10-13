package org.screamingsandals.bedwars.game;

import org.screamingsandals.bedwars.api.game.Game;
import org.bukkit.Location;

public class Team implements Cloneable, org.screamingsandals.bedwars.api.Team {
    public TeamColor color;
    public boolean newColor;
    public String name;
    public Location bed;
    public Location spawn;
    public int maxPlayers;
    public Game game;

    public Team clone() {
        Team t = new Team();
        t.color = this.color;
        t.newColor = this.newColor;
        t.name = this.name;
        t.bed = this.bed;
        t.spawn = this.spawn;
        t.maxPlayers = this.maxPlayers;
        t.game = this.game;
        return t;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public org.screamingsandals.bedwars.api.TeamColor getColor() {
        return color.toApiColor();
    }

    public boolean isNewColor() {
        return newColor;
    }

    @Override
    public Location getTeamSpawn() {
        return spawn;
    }

    @Override
    public Location getTargetBlock() {
        return bed;
    }

    @Override
    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public Game getGame() {
        return game;
    }
}
