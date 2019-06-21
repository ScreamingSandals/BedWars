package misat11.bw.utils;

import java.util.List;
import java.util.Map;

import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import misat11.bw.Main;
import misat11.bw.api.Game;
import misat11.bw.api.events.BedwarsPostSpawnEffectEvent;
import misat11.bw.api.events.BedwarsPreSpawnEffectEvent;

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

			} catch (Throwable e) {
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
				double offsetX = (double) effect.getOrDefault("offsetX", 0);
				double offsetY = (double) effect.getOrDefault("offsetY", 0);
				double offsetZ = (double) effect.getOrDefault("offsetZ", 0);
				double extra = (double) effect.getOrDefault("extra", 1);
				try {
					Particle particle = Particle.valueOf(value.toUpperCase());
					player.getWorld().spawnParticle(particle, player.getLocation(), count, offsetX, offsetY, offsetZ,
							extra);
				} catch (Throwable e) {
					if (Main.isNMS()) {
						try {
							List<Player> players = game.getConnectedPlayers();
							Class<?> clazz = Class.forName(
									"misat11.bw.lib.nms." + Main.getNMSVersion().toLowerCase() + ".ParticleSpawner");
							clazz.getDeclaredMethod("spawnParticle", List.class, String.class, float.class, float.class,
									float.class).invoke(null, players, value, player.getLocation().getX(),
											player.getLocation().getY(), player.getLocation().getZ());
						} catch (Throwable e2) {

						}
					}
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
