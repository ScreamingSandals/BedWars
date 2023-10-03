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

package org.screamingsandals.bedwars.lib.nms.particles;

import static org.screamingsandals.bedwars.lib.nms.utils.ClassStorage.*;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.lib.nms.accessors.ClientboundLevelParticlesPacketAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.EnumParticleAccessor;

// we are not using this class anyways (unless someone run bw on 1.8)
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
					for (Object obj : EnumParticleAccessor.TYPE.get().getEnumConstants()) {
						if (particleName.equalsIgnoreCase((String) getMethod(obj, "b").invoke())) {
							selectedParticle = obj;
							break;
						}
					}
					Object packet = ClientboundLevelParticlesPacketAccessor.CONSTRUCTOR_0.get()
						.newInstance(selectedParticle, true, loc.getX(), loc.getY(), loc.getZ(), offsetX, offsetY,
							offsetZ, extra, count, new int[] {});
					sendPacket(player, packet);
				} catch (Throwable ignored) {

				}
			}
		}
	}
}
