package misat11.bw.utils;

import java.util.List;

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

public class SpawnEffects {
	public static <T> void spawnEffect(Game game, Player player, String particleName) {
		ConfigurationSection effect = Main.getConfigurator().config.getConfigurationSection(particleName);
		if (effect != null && effect.isSet("type")) {
			try {
				String type = effect.getString("type");
				if (type.equalsIgnoreCase("Particle")) {
					if (effect.isSet("value")) {
						String value = effect.getString("value");
						int count = effect.getInt("count", 1);
						double offsetX = effect.getDouble("offsetX", 0);
						double offsetY = effect.getDouble("offsetY", 0);
						double offsetZ = effect.getDouble("offsetZ", 0);
						double extra = effect.getDouble("extra", 1);
						try {
							Particle particle = Particle.valueOf(value.toUpperCase());
							player.getWorld().spawnParticle(particle, player.getLocation(), count, offsetX, offsetY, offsetZ, extra);
						} catch (Throwable e) {
							if (Main.isNMS()) {
								try {
									List<Player> players = game.getConnectedPlayers();
									Class<?> clazz = Class.forName("misat11.bw.nms." + Main.getNMSVersion().toLowerCase()
											+ ".ParticleSpawner");
									clazz.getDeclaredMethod("spawnParticle", List.class, String.class, float.class,
											float.class, float.class)
											.invoke(null, players, value,
													player.getLocation().getX(), player.getLocation().getY(),
													player.getLocation().getZ());
								} catch (Throwable e2) {

								}
							}
						}
					}
				} else if (type.equalsIgnoreCase("Effect")) {
					if (effect.isSet("value")) {
						String value = effect.getString("value");
						Effect particle = Effect.valueOf(value.toUpperCase());
						player.getWorld().playEffect(player.getLocation(), particle, 1);
					}
				} else if (type.equalsIgnoreCase("Firework")) {
					Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(),
							EntityType.FIREWORK);
					FireworkMeta meta = firework.getFireworkMeta();
					int power = effect.getInt("power", 1);
					meta.setPower(power);
					List<FireworkEffect> fireworkEffects = (List<FireworkEffect>) effect.getList("effects");
					if (fireworkEffects != null) {
						meta.addEffects(fireworkEffects);
					}
					firework.setFireworkMeta(meta);
				}
			} catch (Throwable e) {
			}
		}
	}
}
