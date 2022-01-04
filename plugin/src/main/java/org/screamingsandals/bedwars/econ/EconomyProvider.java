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

package org.screamingsandals.bedwars.econ;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.plugin.PluginManager;
import org.screamingsandals.lib.utils.PlatformType;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.Provider;

@Service
public final class EconomyProvider {
    private static Economy INSTANCE = null;

    @Provider(level = Provider.Level.POST_ENABLE)
    public static @Nullable Economy provideEconomy() {
        final var econ = provideEconomy0();
        INSTANCE = econ;
        return econ;
    }

    private static @Nullable Economy provideEconomy0() {
        if (PluginManager.isEnabled(PluginManager.createKey("Vault").orElseThrow()) && PluginManager.getPlatformType() == PlatformType.BUKKIT) {
            PlayerMapper.getConsoleSender().sendMessage(
                    MiscUtils.BW_PREFIX.append(
                            Component.text("Using Vault for economy.")
                    )
            );
            return new VaultEconomy();
        }
        return null;
    }

    /**
     * <p>Retrieves the economy instance.</p>
     * <p><strong>Use a {@link Provider}, if you use a {@link Service}.</strong></p>
     *
     * @return the economy instance, null if no economy is used
     */
    @ApiStatus.Internal
    public static @Nullable Economy getEconomy() {
        return INSTANCE;
    }
}
