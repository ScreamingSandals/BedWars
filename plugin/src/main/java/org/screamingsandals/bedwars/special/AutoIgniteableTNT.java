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

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;

public class AutoIgniteableTNT extends SpecialItem implements org.screamingsandals.bedwars.api.special.AutoIgniteableTNT {

    private int explosionTime;
    private float damage;
    private boolean damagePlacer;

    public AutoIgniteableTNT(Game game, Player player, Team team, int explosionTime, boolean damagePlacer, float damage) {
        super(game, player, team);
        this.explosionTime = explosionTime;
        this.damagePlacer = damagePlacer;
        this.damage = damage;
    }

    @Override
    public int getExplosionTime() {
        return explosionTime;
    }

    @Override
    public boolean isAllowedDamagingPlacer() {
        return damagePlacer;
    }

    @Override
    public float getDamage() {
        return damage;
    }

    @Override
    public void spawn(Location location) {
        TNTPrimed tnt = (TNTPrimed) location.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
        Main.getInstance().registerEntityToGame(tnt, game);
        tnt.setYield(damage);
        tnt.setFuseTicks(explosionTime * 20);

        if (!damagePlacer) {
            tnt.setMetadata(player.getUniqueId().toString(), new FixedMetadataValue(Main.getInstance(), null));
        }
        tnt.setMetadata("autoignited", new FixedMetadataValue(Main.getInstance(), null));

        new BukkitRunnable() {
            public void run() {
                Main.getInstance().unregisterEntityFromGame(tnt);
            }
        }.runTaskLater(Main.getInstance(), explosionTime + 10);
    }

}
