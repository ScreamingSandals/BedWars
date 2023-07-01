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
import org.screamingsandals.bedwars.api.special.LuckyBlock;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.entity.Entities;
import org.screamingsandals.lib.item.builder.ItemStackFactory;
import org.screamingsandals.lib.item.meta.PotionEffect;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.tasker.DefaultThreads;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.world.Location;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Getter
@EqualsAndHashCode(callSuper = true)
public class LuckyBlockImpl extends SpecialItemImpl implements LuckyBlock {
    private final List<Map<String, Object>> luckyBlockData;
    private Location blockLocation;
    private boolean placed;

    public LuckyBlockImpl(GameImpl game, BedWarsPlayer player, TeamImpl team, List<Map<String, Object>> luckyBlockData) {
        super(game, player, team);
        this.luckyBlockData = luckyBlockData;
        game.registerSpecialItem(this);
    }

    public void place(Location loc) {
        this.blockLocation = loc;
        this.placed = true;
    }

    public void process(Player broker) {
        game.unregisterSpecialItem(this);

        var rand = new Random();
        var element = rand.nextInt(luckyBlockData.size());

        var map = luckyBlockData.get(element);

        var type = (String) map.getOrDefault("type", "nothing");
        switch (type) {
            case "item":
                var stack = Objects.requireNonNull(ItemStackFactory.build(map.get("stack")));
                Entities.dropItem(stack, blockLocation);
                break;
            case "potion":
                var potionEffect = PotionEffect.of(map.get("effect"));
                broker.addPotionEffect(potionEffect);
                break;
            case "tnt":
                Tasker.runDelayed(DefaultThreads.GLOBAL_THREAD, () -> {
                    var tnt = Objects.requireNonNull(Entities.spawn("tnt", blockLocation));
                    tnt.setMetadata("fuse_ticks", 0);
                }, 10, TaskerTime.TICKS);
                break;
            case "teleport":
                broker.teleport(broker.getLocation().add(0, (int) map.get("height"), 0));
                break;
        }

        if (map.containsKey("message")) {
            broker.sendMessage(Component.fromLegacy((String) map.get("message")));
        }
    }
}
