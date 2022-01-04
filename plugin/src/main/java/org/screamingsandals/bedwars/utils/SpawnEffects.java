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

package org.screamingsandals.bedwars.utils;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.material.MaterialData;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.lib.nms.particles.Particles;
import org.screamingsandals.bedwars.api.events.BedwarsPostSpawnEffectEvent;
import org.screamingsandals.bedwars.api.events.BedwarsPreSpawnEffectEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.List;
import java.util.Map;

public class SpawnEffects {
    public static void spawnEffect(Game game, Player player, String particleName) {
        BedwarsPreSpawnEffectEvent firstEvent = new BedwarsPreSpawnEffectEvent(game, player, particleName);
        Main.getInstance().getServer().getPluginManager().callEvent(firstEvent);

        if (firstEvent.isCancelled()) {
            return;
        }

        ConfigurationSection effect = Main.getConfigurator().config.getConfigurationSection(particleName);
        if (effect != null && effect.isSet("type")) {
            try {
                String type = effect.getString("type");
                if (type.equalsIgnoreCase("List")) {
                    if (effect.isSet("list")) {
                        List<Map<String, Object>> sections = (List<Map<String, Object>>) effect.getList("list");
                        for (Map<String, Object> section : sections) {
                            useEffect((String) section.get("type"), section, player, game);
                        }
                    }
                } else {
                    useEffect(type, effect.getValues(false), player, game);
                }

            } catch (Throwable ignored) {
                ignored.printStackTrace();
            }
        }

        BedwarsPostSpawnEffectEvent secondEvent = new BedwarsPostSpawnEffectEvent(game, player, particleName);
        Main.getInstance().getServer().getPluginManager().callEvent(secondEvent);
    }

    private static void useEffect(String type, Map<String, Object> effect, Player player, Game game) throws Throwable {
        if (type.equalsIgnoreCase("Particle")) {
            if (effect.containsKey("value")) {
                String value = (String) effect.get("value");
                int count = (int) effect.getOrDefault("count", 1);
                double offsetX = ((Number) effect.getOrDefault("offsetX", 0)).doubleValue();
                double offsetY = ((Number) effect.getOrDefault("offsetY", 0)).doubleValue();
                double offsetZ = ((Number) effect.getOrDefault("offsetZ", 0)).doubleValue();
                double extra = ((Number) effect.getOrDefault("extra", 1)).doubleValue();

                if (effect.containsKey("data")) {
                    Object data = effect.get("data");

                    Particle particle = Particle.valueOf(value);

                    if (particle.getDataType().equals(MaterialData.class)) {
                        data = Material.getMaterial(data.toString().toUpperCase()).getNewData((byte) 0);
                    } else if (particle.getDataType().equals(Particle.DustOptions.class)) {
                        Map<String, Object> map = (Map<String, Object>) data;
                        data = new Particle.DustOptions((Color) map.get("color"), ((Number) map.get("size")).floatValue());
                    } else if (!Main.isLegacy()) {
                        data = SpawnEffectsFlattening.convert(particle, data);
                    }

                    Object finalData = data;
                    game.getConnectedPlayers().forEach(p -> p.spawnParticle(particle, player.getLocation(), count, offsetX, offsetY, offsetZ, extra, finalData));
                } else {
                    Particles.sendParticles(game.getConnectedPlayers(), value, player.getLocation(), count, offsetX, offsetY, offsetZ, extra);
                }
            }
        } else if (type.equalsIgnoreCase("Effect")) {
            if (effect.containsKey("value")) {
                String value = (String) effect.get("value");
                Effect particle = Effect.valueOf(value.toUpperCase());
                player.getWorld().playEffect(player.getLocation(), particle, 1);
            }
        } else if (type.equalsIgnoreCase("Firework")) {
            Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
            FireworkMeta meta = firework.getFireworkMeta();
            int power = (int) effect.getOrDefault("power", 1);
            meta.setPower(power);
            List<FireworkEffect> fireworkEffects = (List<FireworkEffect>) effect.get("effects");
            if (fireworkEffects != null) {
                meta.addEffects(fireworkEffects);
            }
            firework.setFireworkMeta(meta);
        }
    }
}
