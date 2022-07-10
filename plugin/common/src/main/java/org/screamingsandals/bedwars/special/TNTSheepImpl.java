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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.screamingsandals.bedwars.PlatformService;
import org.screamingsandals.bedwars.api.special.TNTSheep;
import org.screamingsandals.bedwars.entities.EntitiesManagerImpl;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.entity.EntityBasic;
import org.screamingsandals.lib.entity.EntityLiving;
import org.screamingsandals.lib.entity.EntityMapper;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.lib.item.builder.ItemFactory;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.world.LocationHolder;

@Getter
@EqualsAndHashCode(callSuper = true)
public class TNTSheepImpl extends SpecialItemImpl implements TNTSheep {
    private final LocationHolder initialLocation;
    private final Item item;
    private final double speed;
    private final double followRange;
    private final double maxTargetDistance;
    private final int explosionTime;
    private EntityLiving entity;
    private EntityBasic tnt;

    public TNTSheepImpl(GameImpl game, BedWarsPlayer player, TeamImpl team, LocationHolder loc, Item item,
                        double speed, double followRange, double maxTargetDistance, int explosionTime) {
        super(game, player, team);
        this.initialLocation = loc;
        this.item = item;
        this.speed = speed;
        this.followRange = followRange;
        this.maxTargetDistance = maxTargetDistance;
        this.explosionTime = explosionTime * 20;
    }

    public void spawn() {
        var sheep = EntityMapper.<EntityLiving>spawn("sheep", initialLocation).orElseThrow();
        var color = team.getColor();
        var target = MiscUtils.findTarget(game, player, maxTargetDistance);

        sheep.setMetadata("color", color.woolData);

        if (target == null) {
            Message
                    .of(LangKeys.SPECIALS_TNTSHEEP_NO_TARGET_FOUND)
                    .prefixOrDefault(game.getCustomPrefixComponent())
                    .send(player);
            sheep.remove();
            return;
        }

        entity = sheep;
        PlatformService.getInstance().getEntityUtils().makeMobAttackTarget(sheep, speed, followRange, 0)
                .attackTarget(target);

        tnt = EntityMapper.spawn("tnt", initialLocation).orElseThrow();
        tnt.setMetadata("fuse_ticks", explosionTime);
        tnt.setMetadata("is_incendiary", false);
        sheep.addPassenger(tnt);

        game.registerSpecialItem(this);
        var entitiesManager = EntitiesManagerImpl.getInstance();
        entitiesManager.addEntityToGame(sheep, game);
        entitiesManager.addEntityToGame(tnt, game);

        var stack = item.withAmount(1);
        try {
            if (player.getPlayerInventory().getItemInOffHand().equals(stack)) {
                player.getPlayerInventory().setItemInOffHand(ItemFactory.getAir());
            } else {
                player.getPlayerInventory().removeItem(stack);
            }
        } catch (Throwable e) {
            player.getPlayerInventory().removeItem(stack);
        }
        player.forceUpdateInventory();

        Tasker.build(() -> {
                    tnt.remove();
                    sheep.remove();
                    game.unregisterSpecialItem(TNTSheepImpl.this);
                })
                .delay(explosionTime + 13, TaskerTime.TICKS)
                .start();
    }
}
