package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.*;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.events.PostSpawnEffectEventImpl;
import org.screamingsandals.bedwars.events.PreSpawnEffectEventImpl;
import org.screamingsandals.bedwars.game.Game;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.utils.ConfigurateUtils;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class SpawnEffects {
    public void spawnEffect(Game game, BedWarsPlayer player, String particleName) {
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
                                useEffect(node.node("type").getString(""), node, player.as(Player.class), game);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        });
                    }
                } else {
                    useEffect(type, effect, player.as(Player.class), game);
                }

            } catch (Throwable ignored) {
            }
        }

        EventManager.fire(new PostSpawnEffectEventImpl(game, player, particleName));
    }

    private void useEffect(String type, ConfigurationNode effect, Player player, Game game) throws Throwable {
        if (type.equalsIgnoreCase("Particle")) {
            if (effect.hasChild("value")) {
                var value = effect.node("value").getString("");
                var count = effect.node("count").getInt(1);
                var offsetX = effect.node("offsetX").getDouble();
                var offsetY = effect.node("offsetY").getDouble();
                var offsetZ = effect.node("offsetZ").getDouble();
                var extra = effect.node("extra").getDouble(1);

                var data = effect.node("data");
                if (!data.empty()) {
                    Particle particle = Particle.valueOf(value);
                    Object dataO = ConfigurateUtils.raw(data);

                    if (particle.getDataType().equals(MaterialData.class)) {
                        dataO = Material.getMaterial(dataO.toString().toUpperCase()).getNewData((byte) 0);
                    } else if (particle.getDataType().equals(Particle.DustOptions.class)) {
                        var map = (Map<String, Object>) dataO;
                        dataO = new Particle.DustOptions((Color) map.get("color"), ((Number) map.get("size")).floatValue());
                    } else if (!Main.isLegacy()) {
                        dataO = SpawnEffectsFlattening.convert(particle, dataO);
                    }

                    var finalData = dataO;
                    game.getConnectedPlayers().forEach(p -> p.spawnParticle(particle, player.getLocation(), count, offsetX, offsetY, offsetZ, extra, finalData));
                } else {
                    for (var player1 : game.getConnectedPlayers()) {
                        player1.spawnParticle(Particle.valueOf(value.toUpperCase()), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(),
                                count, offsetX, offsetY, offsetZ, extra);
                    }
                }
            }
        } else if (type.equalsIgnoreCase("Effect")) {
            if (effect.hasChild("value")) {
                var value = effect.node("value").getString("");
                var particle = Effect.valueOf(value.toUpperCase());
                player.getWorld().playEffect(player.getLocation(), particle, 1);
            }
        } else if (type.equalsIgnoreCase("Firework")) {
            Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
            FireworkMeta meta = firework.getFireworkMeta();
            var power = effect.node("power").getInt(1);
            meta.setPower(power);
            var fireworkEffects = effect.node("effects").childrenList().stream()
                    .map(ConfigurateUtils::toMap)

                    // TODO: make better solution for mixing configurate and bukkit configuration serializable
                    .peek(stringMap -> ((Map<String,Object>) stringMap).put("colors", ((List) stringMap.get("colors")).stream().map(o -> ConfigurationSerialization.deserializeObject((Map<String,?>) o)).collect(Collectors.toList())))
                    .peek(stringMap -> ((Map<String,Object>) stringMap).put("fade-colors", ((List) stringMap.get("fade-colors")).stream().map(o -> ConfigurationSerialization.deserializeObject((Map<String,?>) o)).collect(Collectors.toList())))

                    .map(ConfigurationSerialization::deserializeObject)
                    .filter(obj -> obj instanceof FireworkEffect)
                    .map(obj -> (FireworkEffect) obj)
                    .collect(Collectors.toList());
            meta.addEffects(fireworkEffects);
            firework.setFireworkMeta(meta);
        }
    }
}
