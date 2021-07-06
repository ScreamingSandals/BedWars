package org.screamingsandals.bedwars.lib.nms.particles;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.screamingsandals.lib.bukkit.utils.nms.ClassStorage;
import org.screamingsandals.lib.utils.reflect.Reflect;

// Dropping 1.8 support completely
@Deprecated
public class Particles {
	public static void sendParticles(List<Player> viewers, String particleName, Location loc, int count, double offsetX,
		double offsetY, double offsetZ, double extra) {
		for (Player player : viewers) {
			try {
				player.spawnParticle(Particle.valueOf(particleName.toUpperCase()), loc.getX(), loc.getY(), loc.getZ(),
					count, offsetX, offsetY, offsetZ, extra);
			} catch (Throwable t) {
				try {
					Object selectedParticle = null;
					particleName = particleName.toUpperCase();
					for (Object obj : ClassStorage.NMS.EnumParticle.getEnumConstants()) {
						if (particleName.equalsIgnoreCase((String) Reflect.getMethod(obj, "b").invoke())) {
							selectedParticle = obj;
							break;
						}
					}
					Object packet = ClassStorage.NMS.PacketPlayOutWorldParticles
						.getConstructor(ClassStorage.NMS.EnumParticle, boolean.class, float.class, float.class, float.class, float.class,
							float.class, float.class, float.class, int.class, int[].class)
						.newInstance(selectedParticle, true, loc.getX(), loc.getY(), loc.getZ(), offsetX, offsetY,
							offsetZ, extra, count, new int[] {});
					ClassStorage.sendPacket(player, packet);
				} catch (Throwable ignored) {

				}
			}
		}
	}
}
