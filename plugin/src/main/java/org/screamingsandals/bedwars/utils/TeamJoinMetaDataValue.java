/*
 * Copyright (C) 2023 ScreamingSandals
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

package org.screamingsandals.bedwars.utils;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.game.Team;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class TeamJoinMetaDataValue implements MetadataValue {
    private Team team;
    private boolean teamJoin = true;

    public TeamJoinMetaDataValue(Team team) {
        this.team = team;
    }

    @Override
    public boolean asBoolean() {
        return true;
    }

    @Override
    public byte asByte() {
        return this.asBoolean() ? (byte) 1 : (byte) 0;
    }

    @Override
    public double asDouble() {
        return this.asBoolean() ? 1 : 0;
    }

    @Override
    public float asFloat() {
        return this.asBoolean() ? 1F : 0F;
    }

    @Override
    public int asInt() {
        return this.asBoolean() ? 1 : 0;
    }

    @Override
    public long asLong() {
        return this.asBoolean() ? 1 : 0;
    }

    @Override
    public short asShort() {
        return this.asBoolean() ? (short) 1 : (short) 0;
    }

    @Override
    public String asString() {
        return this.asBoolean() ? "true" : "false";
    }

    @Override
    public Plugin getOwningPlugin() {
        return Main.getInstance();
    }

    public Team getTeam() {
        return this.team;
    }

    @Override
    public void invalidate() {
        this.teamJoin = false;
    }

    @Override
    public Object value() {
        return this.teamJoin;
    }

}
