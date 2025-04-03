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

package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import org.screamingsandals.bedwars.PlatformService;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.events.PostSpawnEffectEventImpl;
import org.screamingsandals.bedwars.events.PreSpawnEffectEventImpl;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.block.Block;
import org.screamingsandals.lib.entity.projectile.FireworkRocket;
import org.screamingsandals.lib.entity.type.EntityType;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.firework.FireworkEffect;
import org.screamingsandals.lib.item.ItemStack;
import org.screamingsandals.lib.item.builder.ItemStackFactory;
import org.screamingsandals.lib.particle.*;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.utils.math.Vector3D;
import org.screamingsandals.lib.world.Location;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class SpawnEffects {
    public void spawnEffect(GameImpl game, BedWarsPlayer player, String particleName) {
        spawnEffect(game, player.getLocation(), particleName);
    }

    public void spawnEffect(GameImpl game, Location location, String particleName) {
        var firstEvent = new PreSpawnEffectEventImpl(game, location, particleName);
        EventManager.fire(firstEvent);

        if (firstEvent.isCancelled()) {
            return;
        }

        var effect = MainConfig.getInstance().node((Object[]) particleName.split("\\."));
        if (effect.hasChild("type")) {
            try {
                var type = effect.node("type").getString("");
                if (type.equalsIgnoreCase("List")) {
                    if (effect.hasChild("list")) {
                        effect.node("list").childrenList().forEach(node -> {
                            try {
                                useEffect(node.node("type").getString(""), node, location, game);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        });
                    }
                } else {
                    useEffect(type, effect, location, game);
                }

            } catch (Throwable ignored) {
            }
        }

        EventManager.fire(new PostSpawnEffectEventImpl(game, location, particleName));
    }

    private void useEffect(String type, ConfigurationNode effect, Location location, GameImpl game) {
        if (type.equalsIgnoreCase("Particle")) {
            if (effect.hasChild("value")) {
                var value = effect.node("value").getString("");
                var count = effect.node("count").getInt(1);
                var offsetX = effect.node("offsetX").getDouble();
                var offsetY = effect.node("offsetY").getDouble();
                var offsetZ = effect.node("offsetZ").getDouble();
                var extra = effect.node("extra").getDouble(1);
                var longDistance = effect.node("longDistance").getBoolean();

                var particleType = ParticleType.ofNullable(value);

                var data = effect.node("data");
                if (particleType != null) {
                    ParticleData particleData = null;

                    if (!data.empty()) {
                        var clazz = particleType.expectedDataClass();
                        if (clazz == Block.class) {
                            particleData = Block.ofNullable(data.getString(""));
                        } else if (clazz == DustOptions.class) {
                            particleData = new DustOptions(
                                    Color.rgb(
                                            data.node("color", "red").getInt(),
                                            data.node("color", "green").getInt(),
                                            data.node("color", "blue").getInt()
                                    ),
                                    data.node("site").getFloat()
                            );
                        } else if (clazz == DustTransition.class) {
                            particleData = new DustTransition(
                                    Color.rgb(
                                            data.node("fromColor", "red").getInt(),
                                            data.node("fromColor", "green").getInt(),
                                            data.node("fromColor", "blue").getInt()
                                    ),
                                    Color.rgb(
                                            data.node("toColor", "red").getInt(),
                                            data.node("toColor", "green").getInt(),
                                            data.node("toColor", "blue").getInt()
                                    ),
                                    data.node("site").getFloat()
                            );
                        } else if (clazz == ItemStack.class) {
                            particleData = ItemStackFactory.build(data.getString(""));
                        }
                    }

                    var particle = new Particle(
                            particleType,
                            count,
                            new Vector3D(offsetX, offsetY, offsetZ),
                            extra,
                            longDistance,
                            particleData
                    );

                    game.getConnectedPlayers().forEach(p -> p.sendParticle(particle, location));
                }
            }
        } else if (type.equalsIgnoreCase("Effect")) {
            if (effect.hasChild("value")) {
                var value = effect.node("value").getString("");
                PlatformService.getInstance().spawnEffect(location, value);
            }
        } else if (type.equalsIgnoreCase("Firework")) {
            EntityType.of("minecraft:firework_rocket").spawn(location, en -> {
                ((FireworkRocket) en).setEffect(effect.node("effects").childrenList()
                        .stream()
                        .map(FireworkEffect::of)
                        .collect(Collectors.toList()), effect.node("power").getInt(1));
            });
        }
    }
}
