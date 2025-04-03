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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.screamingsandals.bedwars.api.special.Golem;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.entities.EntitiesManagerImpl;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.ai.AiManager;
import org.screamingsandals.lib.attribute.AttributeType;
import org.screamingsandals.lib.entity.LivingEntity;
import org.screamingsandals.lib.entity.Entities;
import org.screamingsandals.lib.entity.type.EntityType;
import org.screamingsandals.lib.item.ItemStack;
import org.screamingsandals.lib.item.builder.ItemStackFactory;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.world.Location;

import java.util.List;
import java.util.Objects;

@Getter
@EqualsAndHashCode(callSuper = true)
public class GolemImpl extends SpecialItemImpl implements Golem {
    private LivingEntity entity;
    private final Location location;
    private final ItemStack item;
    private final double speed;
    private final double followRange;
    private final double health;
    private final String name;
    private final boolean showName;

    public GolemImpl(GameImpl game, BedWarsPlayer player, TeamImpl team,
                     ItemStack item, Location location, double speed, double followRange, double health,
                     String name, boolean showName) {
        super(game, player, team);
        this.location = location;
        this.item = item;
        this.speed = speed;
        this.followRange = followRange;
        this.health = health;
        this.name = name;
        this.showName = showName;
    }

    public void spawn() {
        entity = (LivingEntity) Objects.requireNonNull(Entities.spawn("iron_golem", location, en -> {
            var lv = (LivingEntity) en;

            lv.setHealth(health);
            lv.setCustomName(name
                    .replace("%teamcolor%", MiscUtils.toLegacyColorCode(team.getColor().getTextColor()))
                    .replace("%team%", team.getName()));
            lv.setCustomNameVisible(showName);
            lv.setInvulnerable(false);

            lv.getOrCreateAttribute(AttributeType.of("generic.movement_speed"), 0).setBaseValue(speed);
            lv.getOrCreateAttribute(AttributeType.of("generic.follow_range"), 0).setBaseValue(followRange);

            var goalSelector = AiManager.goalSelector(lv);
            if (goalSelector != null) {
                goalSelector.removeAll();

                goalSelector.addFloatGoal(0);
                goalSelector.addMeleeAttackGoal(1, 1, false);
                goalSelector.addRandomStrollGoal(2, 1);
                goalSelector.addRandomLookAroundGoal(3);

                goalSelector.addHurtByTargetGoal(1, List.of());
                goalSelector.addNearestAttackableTargetGoal(2, EntityType.of("player"), false);
                goalSelector.addNearestAttackableTargetGoal(3, EntityType.of("iron_golem"), false);
            }
        }));

        game.registerSpecialItem(this);
        EntitiesManagerImpl.getInstance().addEntityToGame(entity, game);
        if (!MainConfig.getInstance().node("specials", "dont-show-success-messages").getBoolean()) {
            MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_GOLEM_CREATED));
        }

        //TODO - make this better by checking full inventory
        var stack = item.withAmount(1);
        try {
            if (player.getPlayerInventory().getItemInOffHand().equals(stack)) {
                player.getPlayerInventory().setItemInOffHand(ItemStackFactory.getAir());
            } else {
                player.getPlayerInventory().removeItem(stack);
            }
        } catch (Throwable ignored) {
            player.getPlayerInventory().removeItem(stack);
        }

        player.forceUpdateInventory();
    }
}
