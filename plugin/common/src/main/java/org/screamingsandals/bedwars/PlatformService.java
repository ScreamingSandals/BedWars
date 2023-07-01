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

package org.screamingsandals.bedwars;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.region.BWRegion;
import org.screamingsandals.bedwars.utils.EntityUtils;
import org.screamingsandals.bedwars.utils.FakeDeath;
import org.screamingsandals.lib.block.BlockPlacement;
import org.screamingsandals.lib.block.snapshot.BlockSnapshot;
import org.screamingsandals.lib.entity.Entity;
import org.screamingsandals.lib.event.player.PlayerBlockBreakEvent;
import org.screamingsandals.lib.event.player.PlayerBlockPlaceEvent;
import org.screamingsandals.lib.item.ItemStack;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.AbstractService;
import org.screamingsandals.lib.world.Location;

@AbstractService("org.screamingsandals.bedwars.{platform}.{Platform}PlatformService")
public abstract class PlatformService {

    public static PlatformService getInstance() {
        return ServiceManager.get(PlatformService.class);
    }

    @NotNull
    public abstract FakeDeath getFakeDeath();

    @NotNull
    public abstract EntityUtils getEntityUtils();

    public abstract void respawnPlayer(@NotNull Player player, long delay);

    public abstract void reloadPlugin(@NotNull CommandSender sender);

    public abstract void spawnEffect(@NotNull Location location, @NotNull String value);

    @Nullable
    public abstract Player getSourceOfTnt(@NotNull Entity tnt);

    @NotNull
    public abstract PlayerBlockPlaceEvent fireFakeBlockPlaceEvent(@NotNull BlockPlacement block, @NotNull BlockSnapshot originalState, @NotNull BlockPlacement clickedBlock, @NotNull ItemStack item, @NotNull Player player, boolean canBuild);

    @NotNull
    public abstract PlayerBlockBreakEvent fireFakeBlockBreakEvent(@NotNull BlockPlacement block, @NotNull Player player);

    @NotNull
    public abstract BWRegion getLegacyRegion();

    public abstract @Nullable Object savePlatformScoreboard(@NotNull Player player);

    public abstract void restorePlatformScoreboard(@NotNull Player player, @NotNull Object scoreboard);
}
