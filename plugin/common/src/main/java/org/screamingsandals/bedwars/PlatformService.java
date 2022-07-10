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
import org.screamingsandals.lib.block.BlockHolder;
import org.screamingsandals.lib.block.state.BlockStateHolder;
import org.screamingsandals.lib.entity.EntityBasic;
import org.screamingsandals.lib.event.player.SPlayerBlockBreakEvent;
import org.screamingsandals.lib.event.player.SPlayerBlockPlaceEvent;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.AbstractService;
import org.screamingsandals.lib.world.LocationHolder;

@AbstractService(
        replaceRule = "org.screamingsandals.bedwars.{platform}.{Platform}PlatformService"
)
public abstract class PlatformService {

    public static PlatformService getInstance() {
        return ServiceManager.get(PlatformService.class);
    }

    @NotNull
    public abstract FakeDeath getFakeDeath();

    @NotNull
    public abstract EntityUtils getEntityUtils();

    public abstract void respawnPlayer(@NotNull PlayerWrapper player, long delay);

    public abstract void reloadPlugin(@NotNull CommandSenderWrapper sender);

    public abstract void spawnEffect(@NotNull PlayerWrapper player, @NotNull LocationHolder location, @NotNull String value);

    @Nullable
    public abstract PlayerWrapper getSourceOfTnt(@NotNull EntityBasic tnt);

    @NotNull
    public abstract SPlayerBlockPlaceEvent fireFakeBlockPlaceEvent(@NotNull BlockHolder block, @NotNull BlockStateHolder originalState, @NotNull BlockHolder clickedBlock, @NotNull Item item, @NotNull PlayerWrapper player, boolean canBuild);

    @NotNull
    public abstract SPlayerBlockBreakEvent fireFakeBlockBreakEvent(@NotNull BlockHolder block, @NotNull PlayerWrapper player);

    @NotNull
    public abstract BWRegion getLegacyRegion();
}
