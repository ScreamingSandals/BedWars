package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.events.PostSpawnEffectEventImpl;
import org.screamingsandals.bedwars.events.PreSpawnEffectEventImpl;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.block.BlockTypeHolder;
import org.screamingsandals.lib.entity.EntityFirework;
import org.screamingsandals.lib.entity.type.EntityTypeHolder;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.firework.FireworkEffectHolder;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.lib.item.builder.ItemFactory;
import org.screamingsandals.lib.particle.*;
import org.screamingsandals.lib.utils.math.Vector3D;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.stream.Collectors;

@UtilityClass
public class SpawnEffects {
    public void spawnEffect(GameImpl game, BedWarsPlayer player, String particleName) {
        var firstEvent = new PreSpawnEffectEventImpl(game, player, particleName);
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
                                useEffect(node.node("type").getString(""), node, player, game);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        });
                    }
                } else {
                    useEffect(type, effect, player, game);
                }

            } catch (Throwable ignored) {
            }
        }

        EventManager.fire(new PostSpawnEffectEventImpl(game, player, particleName));
    }

    private void useEffect(String type, ConfigurationNode effect, BedWarsPlayer player, GameImpl game) {
        if (type.equalsIgnoreCase("Particle")) {
            if (effect.hasChild("value")) {
                var value = effect.node("value").getString("");
                var count = effect.node("count").getInt(1);
                var offsetX = effect.node("offsetX").getDouble();
                var offsetY = effect.node("offsetY").getDouble();
                var offsetZ = effect.node("offsetZ").getDouble();
                var extra = effect.node("extra").getDouble(1);
                var longDistance = effect.node("longDistance").getBoolean();

                var particleType = ParticleTypeHolder.ofOptional(value);

                var data = effect.node("data");
                if (particleType.isPresent()) {
                    ParticleData particleData = null;

                    if (!data.empty()) {
                        var clazz = particleType.get().expectedDataClass();
                        if (clazz == BlockTypeHolder.class) {
                            particleData = BlockTypeHolder.ofOptional(data.getString("")).orElse(null);
                        } else if (clazz == DustOptions.class) {
                            particleData = new DustOptions(
                                    TextColor.color(
                                            data.node("color", "red").getInt(),
                                            data.node("color", "green").getInt(),
                                            data.node("color", "blue").getInt()
                                    ),
                                    data.node("site").getFloat()
                            );
                        } else if (clazz == DustTransition.class) {
                            particleData = new DustTransition(
                                    TextColor.color(
                                            data.node("fromColor", "red").getInt(),
                                            data.node("fromColor", "green").getInt(),
                                            data.node("fromColor", "blue").getInt()
                                    ),
                                    TextColor.color(
                                            data.node("toColor", "red").getInt(),
                                            data.node("toColor", "green").getInt(),
                                            data.node("toColor", "blue").getInt()
                                    ),
                                    data.node("site").getFloat()
                            );
                        } else if (clazz == Item.class) {
                            particleData = ItemFactory.build(data.getString("")).orElse(null);
                        }
                    }

                    var particle = new ParticleHolder(
                            particleType.get(),
                            count,
                            new Vector3D(offsetX, offsetY, offsetZ),
                            extra,
                            longDistance,
                            particleData
                    );

                    game.getConnectedPlayers().forEach(p -> p.sendParticle(particle, player.getLocation()));
                }
            }
        } else if (type.equalsIgnoreCase("Effect")) {
            if (effect.hasChild("value")) {
                var value = effect.node("value").getString("");
                var particle = Effect.valueOf(value.toUpperCase());
                player.as(Player.class).getWorld().playEffect(player.getLocation().as(Location.class), particle, 1);
            }
        } else if (type.equalsIgnoreCase("Firework")) {
            var firework = EntityTypeHolder.of("minecraft:firework_rocket").<EntityFirework>spawn(player.getLocation()).orElseThrow();
            firework.setEffect(effect.node("effects").childrenList()
                    .stream()
                    .map(FireworkEffectHolder::of)
                    .collect(Collectors.toList()), effect.node("power").getInt(1));
        }
    }
}
