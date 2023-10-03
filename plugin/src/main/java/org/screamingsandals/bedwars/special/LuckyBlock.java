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

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.Team;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.simpleinventories.utils.StackParser;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class LuckyBlock extends SpecialItem implements org.screamingsandals.bedwars.api.special.LuckyBlock {

    private List<Map<String, Object>> luckyBlockData;
    private Location placedLocation = null;
    private boolean isPlaced = false;

    public LuckyBlock(Game game, Player player, Team team, List<Map<String, Object>> luckyBlockData) {
        super(game, player, team);

        this.luckyBlockData = luckyBlockData;

        game.registerSpecialItem(this);
    }

    public void place(Location loc) {
        this.placedLocation = loc;
        this.isPlaced = true;
    }

    public void process(Player broker) {
        game.unregisterSpecialItem(this);

        Random rand = new Random();
        int element = rand.nextInt(luckyBlockData.size());

        Map<String, Object> map = luckyBlockData.get(element);

        String type = (String) map.getOrDefault("type", "nothing");
        switch (type) {
            case "item":
                Object potentialStack = map.get("stack");
                ItemStack stack = potentialStack instanceof ItemStack ? ((ItemStack) potentialStack).clone() : StackParser.parse(map.get("stack"));
                placedLocation.getWorld().dropItem(placedLocation, stack);
                break;
            case "potion":
                PotionEffect potionEffect = StackParser.getPotionEffect(map.get("effect"));
                if (potionEffect != null) {
                    broker.addPotionEffect(potionEffect);
                }
                break;
            case "tnt":
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        TNTPrimed tnt = (TNTPrimed) placedLocation.getWorld().spawnEntity(placedLocation, EntityType.PRIMED_TNT);
                        tnt.setFuseTicks(0);
                    }
                }.runTaskLater(Main.getInstance(), 10L);
                break;
            case "teleport":
                broker.teleport(broker.getLocation().add(0, (int) map.get("height"), 0));
                break;
        }

        if (map.containsKey("message")) {
            broker.sendMessage((String) map.get("message"));
        }

    }

    @Override
    public boolean isPlaced() {
        return isPlaced;
    }

    @Override
    public Location getBlockLocation() {
        return placedLocation;
    }

}
