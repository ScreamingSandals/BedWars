package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.lib.nms.particles.Particles;
import org.screamingsandals.bedwars.api.events.BedwarsPostSpawnEffectEvent;
import org.screamingsandals.bedwars.api.events.BedwarsPreSpawnEffectEvent;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.screamingsandals.lib.utils.ConfigurateUtils;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.stream.Collectors;

@UtilityClass
public class SpawnEffects {
    public void spawnEffect(Game game, Player player, String particleName) {
        BedwarsPreSpawnEffectEvent firstEvent = new BedwarsPreSpawnEffectEvent(game, player, particleName);
        Bukkit.getServer().getPluginManager().callEvent(firstEvent);

        if (firstEvent.isCancelled()) {
            return;
        }

        var effect = Main.getConfigurator().node((Object[]) particleName.split("\\."));
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

        BedwarsPostSpawnEffectEvent secondEvent = new BedwarsPostSpawnEffectEvent(game, player, particleName);
        Bukkit.getServer().getPluginManager().callEvent(secondEvent);
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

                Particles.sendParticles(game.getConnectedPlayers(), value, player.getLocation(), count, offsetX, offsetY, offsetZ, extra);
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
                    .map(ConfigurationSerialization::deserializeObject)
                    .filter(obj -> obj instanceof FireworkEffect)
                    .map(obj -> (FireworkEffect) obj)
                    .collect(Collectors.toList());
            meta.addEffects(fireworkEffects);
            firework.setFireworkMeta(meta);
        }
    }
}
