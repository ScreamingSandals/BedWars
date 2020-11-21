package org.screamingsandals.bedwars.lib.nms.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ClassStorage {

	public static final boolean NMS_BASED_SERVER = safeGetClass("org.bukkit.craftbukkit.Main") != null;
	public static final boolean IS_SPIGOT_SERVER = safeGetClass("org.spigotmc.SpigotConfig") != null;
	public static final boolean IS_PAPER_SERVER = safeGetClass("com.destroystokyo.paper.PaperConfig") != null;
	public static final String NMS_VERSION = checkNMSVersion();

	public static final class NMS {
		public static final Class<?> ChatSerializer = safeGetClass("{nms}.IChatBaseComponent$ChatSerializer", "{nms}.ChatSerializer", "{f:util}.text.ITextComponent$Serializer");
		public static final Class<?> DataWatcher = safeGetClass("{nms}.DataWatcher", "{f:net}.datasync.EntityDataManager");
		public static final Class<?> Entity = safeGetClass("{nms}.Entity", "{f:ent}.Entity");
		public static final Class<?> EntityArmorStand = safeGetClass("{nms}.EntityArmorStand", "{f:ent}.item.ArmorStandEntity", "{f:ent}.item.EntityArmorStand");
		public static final Class<?> EntityCreature = safeGetClass("{nms}.EntityCreature", "{f:ent}.CreatureEntity", "{f:ent}.EntityCreature");
		public static final Class<?> EntityInsentient = safeGetClass("{nms}.EntityInsentient", "{f:ent}.MobEntity", "{f:ent}.EntityLiving");
		public static final Class<?> EntityLiving = safeGetClass("{nms}.EntityLiving", "{f:ent}.LivingEntity", "{f:ent}.EntityLivingBase");
		public static final Class<?> EntityPlayer = safeGetClass("{nms}.EntityPlayer", "{f:ent}.player.ServerPlayerEntity", "{f:ent}.player.EntityPlayerMP");
		public static final Class<?> EnumClientCommand = safeGetClass("{nms}.PacketPlayInClientCommand$EnumClientCommand", "{nms}.EnumClientCommand", "{f:net}.play.client.CClientStatusPacket$State", "{f:net}.play.client.CPacketClientStatus$State");
		public static final Class<?> EnumParticle = safeGetClass("{nms}.EnumParticle");
		public static final Class<?> EnumTitleAction = safeGetClass("{nms}.PacketPlayOutTitle$EnumTitleAction", "{nms}.EnumTitleAction", "{f:net}.play.server.STitlePacket$Type", "{f:net}.play.server.SPacketTitle$Type");
		public static final Class<?> GenericAttributes = safeGetClass("{nms}.GenericAttributes", "{f:ent}.SharedMonsterAttributes");
		public static final Class<?> IChatBaseComponent = safeGetClass("{nms}.IChatBaseComponent", "{f:util}.text.ITextComponent");
		public static final Class<?> IAttribute = safeGetClass("{nms}.IAttribute", "{nms}.AttributeBase", "{f:ent}.ai.attributes.IAttribute", "{f:ent}.ai.attributes.Attribute"); // since 1.16, IAttribute no longer exists
		public static final Class<?> MinecraftServer = safeGetClass("{nms}.MinecraftServer", "{f:nms}.MinecraftServer");
		public static final Class<?> NBTTagCompound = safeGetClass("{nms}.NBTTagCompound", "{f:nbt}.CompoundNBT", "{f:nbt}.NBTTagCompound");
		public static final Class<?> NetworkManager = safeGetClass("{nms}.NetworkManager", "{f:net}.NetworkManager");
		public static final Class<?> Packet = safeGetClass("{nms}.Packet", "{f:net}.IPacket", "{f:net}.Packet");
		public static final Class<?> PacketLoginInStart = safeGetClass("{nms}.PacketLoginInStart", "{f:net}.login.client.CLoginStartPacket", "{f:net}.login.client.CPacketLoginStart");
		public static final Class<?> PacketPlayInClientCommand = safeGetClass("{nms}.PacketPlayInClientCommand", "{f:net}.play.client.CClientStatusPacket", "{f:net}.play.client.CPacketClientStatus");
		public static final Class<?> PacketPlayInUseEntity = safeGetClass("{nms}.PacketPlayInUseEntity", "{f:net}.play.client.CUseEntityPacket", "{f:net}.play.client.CPacketUseEntity");
		public static final Class<?> PacketPlayOutEntityDestroy = safeGetClass("{nms}.PacketPlayOutEntityDestroy", "{f:net}.play.server.SDestroyEntitiesPacket", "{f:net}.play.server.SPacketDestroyEntities");
		public static final Class<?> PacketPlayOutEntityMetadata = safeGetClass("{nms}.PacketPlayOutEntityMetadata", "{f:net}.play.server.SEntityMetadataPacket", "{f:net}.play.server.SPacketEntityMetadata");
		public static final Class<?> PacketPlayOutEntityTeleport = safeGetClass("{nms}.PacketPlayOutEntityTeleport", "{f:net}.play.server.SEntityTeleportPacket", "{f:net}.play.server.SPacketEntityTeleport");
		public static final Class<?> PacketPlayOutExperience = safeGetClass("{nms}.PacketPlayOutExperience", "{f:net}.play.server.SSetExperiencePacket", "{f:net}.play.server.SPacketSetExperience");
		public static final Class<?> PacketPlayOutSpawnEntityLiving = safeGetClass("{nms}.PacketPlayOutSpawnEntityLiving", "{f:net}.play.server.SSpawnMobPacket", "{f:net}.play.server.SPacketSpawnMob");
		public static final Class<?> PacketPlayOutTitle = safeGetClass("{nms}.PacketPlayOutTitle", "{f:net}.play.server.STitlePacket", "{f:net}.play.server.SPacketTitle");
		public static final Class<?> PacketPlayOutWorldParticles = safeGetClass("{nms}.PacketPlayOutWorldParticles", "{f:net}.play.server.SSpawnParticlePacket", "{f:net}.play.server.SPacketParticles");
		public static final Class<?> PathfinderGoal = safeGetClass("{nms}.PathfinderGoal", "{f:goal}.Goal", "{f:ent}.ai.EntityAIBase");
		public static final Class<?> PathfinderGoalSelector = safeGetClass("{nms}.PathfinderGoalSelector", "{f:goal}.GoalSelector", "{f:ent}.ai.EntityAITasks");
		public static final Class<?> PathfinderGoalMeleeAttack = safeGetClass("{nms}.PathfinderGoalMeleeAttack", "{f:goal}.MeleeAttackGoal", "{f:ent}.ai.EntityAIAttackMelee");
		public static final Class<?> PathfinderGoalNearestAttackableTarget = safeGetClass("{nms}.PathfinderGoalNearestAttackableTarget", "{f:goal}.NearestAttackableTargetGoal", "{f:ent}.ai.EntityAINearestAttackableTarget");
		public static final Class<?> PlayerConnection = safeGetClass("{nms}.PlayerConnection", "{f:net}.play.ServerPlayNetHandler", "{f:net}.NetHandlerPlayServer");
		public static final Class<?> ServerConnection = safeGetClass("{nms}.ServerConnection", "{f:net}.NetworkSystem");
		public static final Class<?> World = safeGetClass("{nms}.World", "{f:world}.World");
		public static final Class<?> PacketPlayOutPlayerListHeaderFooter = safeGetClass("{nms}.PacketPlayOutPlayerListHeaderFooter", "{f:net}.play.server.SPlayerListHeaderFooterPacket", "{f:net}.play.server.SPacketPlayerListHeaderFooter");

		// 1.16
		public static final Class<?> AttributeModifiable = safeGetClass("{nms}.AttributeModifiable", "{f:ent}.ai.attributes.ModifiableAttributeInstance");
	}
	
	private static String checkNMSVersion() {
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
					// CRAFTBUKKIT/SPIGOT/PAPER
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
			} catch (ClassNotFoundException t) {
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
				Class<?> claz2 = clazz;
				do {
					try {
						Method method = claz2.getDeclaredMethod(name.trim(), params);
						method.setAccessible(true);
						return new ClassMethod(method);
					} catch (Throwable t2) {
					}
				} while ((claz2 = claz2.getSuperclass()) != null && claz2 != Object.class);
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
			return getField(handler, "playerConnection,field_71135_a");
		}
		return null;
	}
	
	public static boolean sendPacket(Player player, Object packet) {
		if (!NMS.Packet.isInstance(packet)) {
			return false;
		}
		Object connection = getPlayerConnection(player);
		if (connection != null) {
			getMethod(connection, "sendPacket,func_147359_a", NMS.Packet).invoke(packet);
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
		Object methodProfiler = getMethod(handler, "getMethodProfiler,func_217381_Z").invoke();
		if (methodProfiler == null) {
			methodProfiler = getField(handler, "methodProfiler,field_72984_F");
		}
		return methodProfiler;
	}
	
	public static Object obtainNewPathfinderSelector(Object handler) {
		try {
			Object world = getMethod(handler, "getWorld,func_130014_f_").invoke();
			try {
				// 1.16
				return NMS.PathfinderGoalSelector.getConstructor(Supplier.class).newInstance((Supplier<?>) () -> getMethodProfiler(world));
			} catch (Throwable ignored) {
				// Pre 1.16
				return NMS.PathfinderGoalSelector.getConstructors()[0].newInstance(getMethodProfiler(world));
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}
}
