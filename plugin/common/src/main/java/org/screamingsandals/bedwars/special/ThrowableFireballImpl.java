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

package org.screamingsandals.bedwars.special;

import lombok.Getter;
import org.screamingsandals.bedwars.api.special.ThrowableFireball;
import org.screamingsandals.bedwars.entities.EntitiesManagerImpl;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.lib.entity.projectile.Fireball;

import java.util.Objects;

@Getter
public class ThrowableFireballImpl extends SpecialItemImpl implements ThrowableFireball {
    private final float damage;
    private final boolean incendiary;
    private final boolean damagesThrower;

    public ThrowableFireballImpl(GameImpl game, BedWarsPlayer player, TeamImpl team, float damage, boolean incendiary, boolean damagesThrower) {
        super(game, player, team);
        this.damage = damage;
        this.incendiary = incendiary;
        this.damagesThrower = damagesThrower;
    }

    @Override
    public boolean damagesThrower() {
        return damagesThrower;
    }

    @Override
    public void run() {
        var fireball = (Fireball) Objects.requireNonNull(player.launchProjectile("minecraft:fireball"));
        fireball.isIncendiary(false);
        fireball.yield(damage);
        fireball.setShooter(damagesThrower ? null : player);
        EntitiesManagerImpl.getInstance().addEntityToGame(fireball, PlayerManagerImpl.getInstance().getGameOfPlayer(player).orElseThrow());
    }
}
