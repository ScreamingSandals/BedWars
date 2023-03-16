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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.game.TeamColor;
import org.screamingsandals.bedwars.lib.nms.accessors.EntityIronGolemAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.EntityPlayerAccessor;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.bedwars.lib.nms.entity.EntityUtils;

import static org.screamingsandals.bedwars.lib.lang.I18n.i18nonly;

public class Golem extends SpecialItem implements org.screamingsandals.bedwars.api.special.Golem {
    private LivingEntity entity;
    private Location location;
    private ItemStack item;
    private double speed;
    private double followRange;
    private double health;
    private String name;
    private boolean showName;

    public Golem(Game game, Player player, Team team,
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

    @Override
    public LivingEntity getEntity() {
        return entity;
    }

    @Override
    public double getSpeed() {
        return speed;
    }

    @Override
    public double getFollowRange() {
        return followRange;
    }

    @Override
    public double getHealth() {
        return health;
    }

    public void spawn() {
        final TeamColor color = TeamColor.fromApiColor(team.getColor());
        final IronGolem golem = (IronGolem) location.getWorld().spawnEntity(location, EntityType.IRON_GOLEM);
        golem.setHealth(health);
        golem.setCustomName(name
                .replace("%teamcolor%", color.chatColor.toString())
                .replace("%team%", team.getName()));
        golem.setCustomNameVisible(showName);
        try {
            golem.setInvulnerable(false);
        } catch (Throwable ignored) {
            // Still can throw an exception on some old versions
        }
        
        entity = golem;

        EntityUtils.makeMobAttackTarget2(golem, speed, followRange, -1)
                .getTargetSelector()
                .hurtByTarget(1)
                .attackNearestTarget(2, EntityPlayerAccessor.getType())
                .attackNearestTarget(3, EntityIronGolemAccessor.getType());

        game.registerSpecialItem(this);
        Main.registerGameEntity(golem, (org.screamingsandals.bedwars.game.Game) game);
        if (!Main.getConfigurator().config.getBoolean("specials.dont-show-success-messages")) {
            MiscUtils.sendActionBarMessage(player, i18nonly("specials_golem_created"));
        }

        //TODO - make this better by checking full inventory
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            try {
                if (player.getInventory().getItemInOffHand().equals(item)) {
                    player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                } else {
                    player.getInventory().remove(item);
                }
            } catch (Throwable e) {
                player.getInventory().remove(item);
            }
        }

        player.updateInventory();
    }
}
