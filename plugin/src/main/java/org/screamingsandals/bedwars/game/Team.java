package org.screamingsandals.bedwars.game;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.lib.world.LocationHolder;

public class Team implements Cloneable, org.screamingsandals.bedwars.api.Team<LocationHolder> {
    public TeamColor color;
    public String name;
    public LocationHolder bed;
    public LocationHolder spawn;
    public int maxPlayers;
    public Game game;

    public Team clone() {
        Team t = new Team();
        t.color = this.color;
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

    @Override
    public LocationHolder getTeamSpawn() {
        return spawn;
    }

    @Override
    public LocationHolder getTargetBlock() {
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
