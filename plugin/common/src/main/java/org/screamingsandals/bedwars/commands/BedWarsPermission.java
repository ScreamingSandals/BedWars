/*
 * Copyright (C) 2025 ScreamingSandals
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

package org.screamingsandals.bedwars.commands;

import lombok.RequiredArgsConstructor;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.lib.sender.permissions.Permission;
import org.screamingsandals.lib.sender.permissions.SimplePermission;

@RequiredArgsConstructor
public enum BedWarsPermission {
    ADMIN_PERMISSION("admin"),
    OTHER_STATS_PERMISSION("other-stats"),
    JOIN_PERMISSION("join"),
    JOIN_GROUP_PERMISSION("join-group"),
    LEAVE_PERMISSION("leave"),
    GAMES_INVENTORY_PERMISSION("gamesinv"),
    AUTOJOIN_PERMISSION("autojoin"),
    LIST_PERMISSION("list"),
    REJOIN_PERMISSION("rejoin"),
    STATS_PERMISSION("stats"),
    LEADERBOARD_PERMISSION("leaderboard"),
    PARTY_PERMISSION("party"),
    ALL_JOIN_PERMISSION("all-join"),
    DISABLE_ALL_JOIN_PERMISSION("disable-all-join"),
    START_ITEM_PERMISSION("start-item"),
    FORCE_JOIN_PERMISSION("force-join"),
    BYPASS_FLIGHT_PERMISSION("bypass-flight");

    private final String permissionNodeKey;

    public Permission asPermission() {
        var node = MainConfig.getInstance().node("permissions", permissionNodeKey);
        var permission = node.node("key").getString("bw.unknown-permission");
        var defaultAllowed = node.node("default").getBoolean(false);

        return SimplePermission.of(permission, defaultAllowed);
    }
}
