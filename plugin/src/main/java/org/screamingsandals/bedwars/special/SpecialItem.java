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

package org.screamingsandals.bedwars.special;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.Team;
import org.bukkit.entity.Player;

public abstract class SpecialItem implements org.screamingsandals.bedwars.api.special.SpecialItem {

    protected Game game;
    protected Player player;
    protected Team team;

    public SpecialItem(Game game, Player player, Team team) {
        this.game = game;
        this.player = player;
        this.team = team;
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Team getTeam() {
        return team;
    }

}
