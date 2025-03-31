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

@Init(services = {
        ArrowBlockerListener.class,
        AutoIgniteableTNTListener.class,
        GolemListener.class,
        LuckyBlockAddonListener.class,
        MagnetShoesListener.class,
        PermaItemListener.class,
        ProtectionWallListener.class,
        RescuePlatformListener.class,
        TeamChestListener.class,
        ThrowableFireballListener.class,
        TNTSheepListener.class,
        TrackerListener.class,
        TrapListener.class,
        WarpPowderListener.class,
        BridgeEggListener.class,
        PopUpTowerListener.class
})
package org.screamingsandals.bedwars.special;

import org.screamingsandals.bedwars.special.listener.*;
import org.screamingsandals.lib.utils.annotations.Init;