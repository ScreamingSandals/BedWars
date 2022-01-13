/*
 * Copyright (C) 2022 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

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

    @Override
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
