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

package org.screamingsandals.bedwars.special;

import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;

public class ThrowableFireball extends SpecialItem implements org.screamingsandals.bedwars.api.special.ThrowableFireball {

    private float damage;
    private boolean incendiary;
    private boolean damagesThrower;

    public ThrowableFireball(Game game, Player player, Team team, float damage, boolean incendiary, boolean damagesThrower) {
        super(game, player, team);
        this.damage = damage;
        this.incendiary = incendiary;
        this.damagesThrower = damagesThrower;
    }

    @Override
    public float getDamage() {
        return damage;
    }

    @Override
    public boolean isIncendiary() {
        return incendiary;
    }

    @Override
    public boolean damagesThrower() {
        return damagesThrower;
    }

    @Override
    public void run() {
        Fireball fireball = player.launchProjectile(Fireball.class);
        // TODO allow setting perfect velocity (to player's exact facing instead of random spread)
        Main.getInstance().registerEntityToGame(fireball, game);
        fireball.setIsIncendiary(incendiary);
        fireball.setYield(damage);
        fireball.setBounce(false);
        fireball.setShooter(damagesThrower ? null : player);
        if (Main.getVersionNumber() <= 108) {
            fireball.teleport(fireball.getLocation().add(player.getEyeLocation().getDirection()));
        }
    }

}
