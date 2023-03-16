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

package org.screamingsandals.bedwars.lib.nms.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.lib.nms.accessors.*;

public class ClassStorage {

	public static final boolean NMS_BASED_SERVER = safeGetClass("org.bukkit.craftbukkit.Main") != null;
	public static final boolean IS_SPIGOT_SERVER = safeGetClass("org.spigotmc.SpigotConfig") != null;
	public static final String NMS_VERSION = checkNMSVersion();

	public static final class NMS {
		/* not used by bw since 1.9 */
		public static final Class<?> EnumParticle = safeGetClass("{nms}.EnumParticle"); // why is it even used
		public static final Class<?> PacketPlayOutWorldParticles = PacketPlayOutWorldParticlesAccessor.getType();
	}
	
	private static String checkNMSVersion() {
		/* Useless since MC 1.17 */
		/* if NMS is not found, finding class will fail, but we still need some string */
		String nmsVersion = "nms_not_found"; 
		
		if (NMS_BASED_SERVER) {
			String packName = Bukkit.getServer().getClass().getPackage().getName();
			nmsVersion = packName.substring(packName.lastIndexOf('.') + 1);
		}
		
		return nmsVersion;
	}
	
	public static Class<?> safeGetClass(String... clazz) {
		for (String claz : clazz) {
			try {
				return Class.forName(claz
					// CRAFTBUKKIT/SPIGOT/PAPER before 1.17
					.replace("{obc}", "org.bukkit.craftbukkit." + NMS_VERSION)
					.replace("{nms}", "net.minecraft.server." + NMS_VERSION)
					// CAULDRON BASED
					.replace("{f:ent}", "net.minecraft.entity")
					.replace("{f:goal}", "net.minecraft.entity.ai.goal")
					.replace("{f:nbt}", "net.minecraft.nbt")
					.replace("{f:net}", "net.minecraft.network")
					.replace("{f:nms}", "net.minecraft.server")
					.replace("{f:util}", "net.minecraft.util")
					.replace("{f:world}", "net.minecraft.world")
				);
			} catch (ClassNotFoundException ignored) {
			}
		}
		return null;
	}
	
	public static ClassMethod getMethod(String className, String names, Class<?>... params) {
		return getMethod(safeGetClass(className), names.split(","), params); 
	}
	
	public static ClassMethod getMethod(Class<?> clazz, String names, Class<?>... params) {
		return getMethod(clazz, names.split(","), params); 
	}
	
	public static ClassMethod getMethod(String className, String[] names, Class<?>... params) {
		return getMethod(safeGetClass(className), names, params); 
	}
	
	public static ClassMethod getMethod(Class<?> clazz, String[] names, Class<?>... params) {
		for (String name : names) {
			try {
				Method method = clazz.getMethod(name.trim(), params);
				return new ClassMethod(method);
			} catch (Throwable t) {
				try {
					Class<?> claz2 = clazz;
					do {
						try {
							Method method = claz2.getDeclaredMethod(name.trim(), params);
							method.setAccessible(true);
							return new ClassMethod(method);
						} catch (Throwable t2) {
						}
					} while ((claz2 = claz2.getSuperclass()) != null && claz2 != Object.class);
				} catch (Throwable t2) {
				}
			}
		}
		return new ClassMethod(null);
	}
	public static InstanceMethod getMethod(Object instance, String names, Class<?>...params) {
		ClassMethod method = getMethod(instance.getClass(), names.split(","), params);
		return new InstanceMethod(instance, method.getReflectedMethod());
	}
	
	public static InstanceMethod getMethod(Object instance, String[] names, Class<?>...params) {
		ClassMethod method = getMethod(instance.getClass(), names, params);
		return new InstanceMethod(instance, method.getReflectedMethod());
	}

	public static InstanceMethod getMethod(Object instance, Method method) {
		return new InstanceMethod(instance, method);
	}

	public static ClassMethod getMethod(Method method) {
		return new ClassMethod(method);
	}

	public static Object getField(Field field) {
		return getField(null, field);
	}

	public static Object getField(Object instance, Field field) {
		try {
			field.setAccessible(true);
			return field.get(instance);
		} catch (Throwable ignored) {}
		return null;
	}
	
	public static Object getField(Object instance, String names) {
		return getField(instance.getClass(), names.split(","), instance);
	}
	
	public static Object getField(Object instance, String[] names) {
		return getField(instance.getClass(), names, instance);
	}
	
	public static Object getField(Class<?> clazz, String names) {
		return getField(clazz, names.split(","), null);
	}
	
	public static Object getField(Class<?> clazz, String[] names) {
		return getField(clazz, names, null);
	}

	public static Object getField(Class<?> clazz, String names, Object instance) {
		return getField(clazz, names.split(","), instance);
	}
	
	public static Object getField(Class<?> clazz, String[] names, Object instance) {
		for (String name : names) {
			try {
				Field field = clazz.getField(name.trim());
				Object result = field.get(instance);
				return result;
			} catch (Throwable t) {
				Class<?> claz2 = clazz;
				do {
					try {
						Field field = claz2.getDeclaredField(name.trim());
						field.setAccessible(true);
						Object result = field.get(instance);
						return result;
					} catch (Throwable t2) {
					}
				} while ((claz2 = claz2.getSuperclass()) != null && claz2 != Object.class);
			}
		}
		return null;
	}

	public static Object setField(Field field, Object value) {
		return setField(null, field, value);
	}

	public static Object setField(Object instance, Field field, Object value) {
		try {
			field.setAccessible(true);
			field.set(instance, value);
			return field.get(instance);
		} catch (Throwable ignored) {}
		return null;
	}
	
	public static Object setField(Object instance, String names, Object set) {
		return setField(instance.getClass(), names.split(","), instance, set);
	}
	
	public static Object setField(Object instance, String[] names, Object set) {
		return setField(instance.getClass(), names, instance, set);
	}
	
	public static Object setField(Class<?> clazz, String names, Object set) {
		return setField(clazz, names.split(","), null, set);
	}
	
	public static Object setField(Class<?> clazz, String[] names, Object set) {
		return setField(clazz, names, null, set);
	}

	public static Object setField(Class<?> clazz, String names, Object instance, Object set) {
		return setField(clazz, names.split(","), instance, set);
	}
	
	public static Object setField(Class<?> clazz, String[] names, Object instance, Object set) {
		for (String name : names) {
			try {
				Field field = clazz.getField(name.trim());
				field.set(instance, set);
				Object result = field.get(instance);
				return result;
			} catch (Throwable t) {
				Class<?> claz2 = clazz;
				do {
					try {
						Field field = claz2.getDeclaredField(name.trim());
						field.setAccessible(true);
						field.set(instance, set);
						Object result = field.get(instance);
						return result;
					} catch (Throwable t2) {
					}
				} while ((claz2 = claz2.getSuperclass()) != null && claz2 != Object.class);
			}
		}
		return null;
	}
	
	public static Object getHandle(Object obj) {
		return getMethod(obj, "getHandle").invoke();
	}
	
	public static Object getPlayerConnection(Player player) {
		Object handler = getMethod(player, "getHandle").invoke();
		if (handler != null) {
			return getField(handler, EntityPlayerAccessor.getFieldPlayerConnection());
		}
		return null;
	}
	
	public static boolean sendPacket(Player player, Object packet) {
		if (!PacketAccessor.getType().isInstance(packet)) {
			return false;
		}
		Object connection = getPlayerConnection(player);
		if (connection != null) {
			getMethod(connection, PlayerConnectionAccessor.getMethodSendPacket1()).invoke(packet);
			return true;
		}
		return false;
	}

	public static Object findEnumConstant(Class<?> enumClass, String constantNames) {
		return findEnumConstant(enumClass, constantNames.split(","));
	}
	
	public static Object findEnumConstant(Class<?> enumClass, String[] constantNames) {
		Object[] enums = enumClass.getEnumConstants();
		if (enums != null) {
			for (Object enumeration : enums) {
				Object name = getMethod(enumeration, "name").invoke();
				for (String constant : constantNames) {
					if (constant.equals(name)) {
						return enumeration;
					}
				}
			}
		}
		return null;
	}

	public static Object getMethodProfiler(World world) {
		return getMethodProfiler(getHandle(world));
	}

	public static Object getMethodProfiler(Object handler) {
		Object methodProfiler = getMethod(handler, WorldAccessor.getMethodGetMethodProfiler1()).invoke();
		if (methodProfiler == null) {
			methodProfiler = getField(handler, WorldAccessor.getFieldMethodProfiler());
		}
		return methodProfiler;
	}
	
	public static Object obtainNewPathfinderSelector(Object handler) {
		try {
			Object world = getMethod(handler, EntityAccessor.getMethodGetWorld1()).invoke();
			try {
				// 1.17
				return PathfinderGoalSelectorAccessor.getConstructor0().newInstance(getMethod(world, WorldAccessor.getMethodGetMethodProfilerSupplier1()).invoke());
			} catch (Throwable ignored) {
				try {
					// 1.16
					return PathfinderGoalSelectorAccessor.getConstructor0().newInstance((Supplier<?>) () -> getMethodProfiler(world));
				} catch (Throwable ignore) {
					// Pre 1.16
					return PathfinderGoalSelectorAccessor.getType().getConstructors()[0].newInstance(getMethodProfiler(world));
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}
}
