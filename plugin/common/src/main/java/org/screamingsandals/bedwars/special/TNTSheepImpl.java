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
import org.screamingsandals.lib.entity.Entities;
import org.screamingsandals.lib.entity.LivingEntity;
import org.screamingsandals.lib.entity.PrimedTnt;
import org.screamingsandals.lib.entity.animal.Sheep;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.item.ItemStack;
import org.screamingsandals.lib.item.builder.ItemStackFactory;
import org.screamingsandals.lib.tasker.DefaultThreads;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.world.Location;

import java.util.Objects;

@Getter
@EqualsAndHashCode(callSuper = true)
public class TNTSheepImpl extends SpecialItemImpl implements TNTSheep {
    private final Location initialLocation;
    private final ItemStack item;
    private final double speed;
    private final double followRange;
    private final double maxTargetDistance;
    private final int explosionTime;
    private Sheep entity;
    private PrimedTnt tnt;

    public TNTSheepImpl(GameImpl game, BedWarsPlayer player, TeamImpl team, Location loc, ItemStack item,
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
        var color = team.getColor();
        var target = MiscUtils.findTarget(game, player, maxTargetDistance);

        if (target == null) {
            Message
                    .of(LangKeys.SPECIALS_TNTSHEEP_NO_TARGET_FOUND)
                    .prefixOrDefault(game.getCustomPrefixComponent())
                    .send(player);
            return;
        }

        entity = (Sheep) Objects.requireNonNull(Entities.spawn("sheep", initialLocation, en -> {
            ((Sheep) en).woolColor(color.getDyeColor());

            //noinspection DataFlowIssue
            PlatformService.getInstance().getEntityUtils().makeMobAttackTarget(((LivingEntity) en), speed, followRange, 0)
                    .attackTarget(target);
        }));

        tnt = (PrimedTnt) Objects.requireNonNull(Entities.spawn("tnt", initialLocation, en -> {
            var tnt1 = (PrimedTnt) en;
            tnt1.fuseTicks(explosionTime);
            tnt1.isIncendiary(false);
        }));
        entity.addPassenger(tnt);

        game.registerSpecialItem(this);
        var entitiesManager = EntitiesManagerImpl.getInstance();
        entitiesManager.addEntityToGame(entity, game);
        entitiesManager.addEntityToGame(tnt, game);

        var stack = item.withAmount(1);
        try {
            if (player.getPlayerInventory().getItemInOffHand().equals(stack)) {
                player.getPlayerInventory().setItemInOffHand(ItemStackFactory.getAir());
            } else {
                player.getPlayerInventory().removeItem(stack);
            }
        } catch (Throwable e) {
            player.getPlayerInventory().removeItem(stack);
        }
        player.forceUpdateInventory();

        Tasker.runDelayed(DefaultThreads.GLOBAL_THREAD, () -> {
                    tnt.remove();
                    entity.remove();
                    game.unregisterSpecialItem(TNTSheepImpl.this);
                }, explosionTime + 13, TaskerTime.TICKS);
    }
}
