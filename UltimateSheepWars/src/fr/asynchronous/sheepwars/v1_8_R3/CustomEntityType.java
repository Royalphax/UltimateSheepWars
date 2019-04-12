package fr.asynchronous.sheepwars.v1_8_R3;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.util.MathUtils;
import fr.asynchronous.sheepwars.core.util.ReflectionUtils;
import fr.asynchronous.sheepwars.core.version.ICustomEntityType;
import fr.asynchronous.sheepwars.v1_8_R3.entity.CustomSheep;
import fr.asynchronous.sheepwars.v1_8_R3.entity.EntityBossBar;
import fr.asynchronous.sheepwars.v1_8_R3.entity.EntityCancelMove;
import fr.asynchronous.sheepwars.v1_8_R3.entity.EntityMeteor;
import fr.asynchronous.sheepwars.v1_8_R3.entity.firework.FireworkSpawner;
import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.BiomeBase.BiomeMeta;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EntityFireball;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntitySheep;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.EntityWither;

public enum CustomEntityType {
	SHEEP("Sheep", 91, EntityType.SHEEP, EntitySheep.class, CustomSheep.class),
	@SuppressWarnings({"unchecked", "rawtypes"})
	WITHER("WitherBoss", 64, EntityType.WITHER, (Class) EntityWither.class, (Class) EntityBossBar.class),
	@SuppressWarnings({"unchecked", "rawtypes"})
	CANCEL_MOVE("ArmorStand", 30, EntityType.ARMOR_STAND, (Class) EntityArmorStand.class, (Class) EntityCancelMove.class),
	@SuppressWarnings({"unchecked", "rawtypes"})
	METEOR("Fireball", 12, EntityType.FIREBALL, (Class) EntityFireball.class, (Class) EntityMeteor.class);

	private String name;
	private int id;
	private EntityType entityType;
	private Class<? extends EntityInsentient> nmsClass;
	private Class<? extends EntityInsentient> customClass;

	private CustomEntityType(String name, int id, EntityType entityType, Class<? extends EntityInsentient> nmsClass, Class<? extends EntityInsentient> customClass) {
		this.name = name;
		this.id = id;
		this.entityType = entityType;
		this.nmsClass = nmsClass;
		this.customClass = customClass;
	}

	public String getName() {
		return name;
	}

	public int getID() {
		return id;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public Class<? extends EntityInsentient> getNMSClass() {
		return nmsClass;
	}

	public Class<? extends EntityInsentient> getCustomClass() {
		return customClass;
	}

	public static class GlobalMethods implements ICustomEntityType {

		public void registerEntities() {
			for (CustomEntityType entity : values()) {
				a(entity.getCustomClass(), entity.getName(), entity.getID());
			}
			BiomeBase[] biomes;
			try {
				biomes = (BiomeBase[]) getPrivateStatic(BiomeBase.class, "biomes");
			} catch (Exception e) {
				return;
			}
			for (BiomeBase biomeBase : biomes) {
				if (biomeBase == null) {
					break;
				}
				if (biomeBase.equals(BiomeBase.HELL)) {
					continue;
				}
				// 1.8 field name
				for (String field : new String[]{"aw", "at", "au", "av"})
					try {
						Field list = BiomeBase.class.getDeclaredField(field);
						list.setAccessible(true);
						@SuppressWarnings("unchecked")
						List<BiomeMeta> mobList = (List<BiomeMeta>) list.get(biomeBase);
						// Write in our custom class.
						for (BiomeMeta meta : mobList)
							for (CustomEntityType entity : values())
								if (entity.getNMSClass().equals(meta.b))
									meta.b = entity.getCustomClass();
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}

		/**
		 * Unregister our entities to prevent memory leaks. Call on disable.
		 */
		@SuppressWarnings("rawtypes")
		public void unregisterEntities() {
			for (CustomEntityType entity : values()) {
				try {
					((Map) getPrivateStatic(EntityTypes.class, "c")).remove(entity.getCustomClass());
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					((Map) getPrivateStatic(EntityTypes.class, "e")).remove(entity.getCustomClass());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			for (CustomEntityType entity : values())
				try {
					a(entity.getNMSClass(), entity.getName(), entity.getID());
				} catch (Exception e) {
					e.printStackTrace();
				}

			BiomeBase[] biomes;
			try {
				biomes = (BiomeBase[]) getPrivateStatic(BiomeBase.class, "biomes");
			} catch (Exception exc) {
				return;
			}
			for (BiomeBase biomeBase : biomes) {
				if (biomeBase == null)
					break;
				if (biomeBase == BiomeBase.HELL) {
					continue;
				}

				for (String field : new String[]{"aw", "at", "au", "av"})
					try {
						Field list = BiomeBase.class.getDeclaredField(field);
						list.setAccessible(true);
						@SuppressWarnings("unchecked")
						List<BiomeMeta> mobList = (List<BiomeMeta>) list.get(biomeBase);

						for (BiomeMeta meta : mobList)
							for (CustomEntityType entity : values())
								if (entity.getCustomClass().equals(meta.b))
									meta.b = entity.getNMSClass();
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}

		@Override
		public void spawnInstantExplodingFirework(Location location, FireworkEffect effect, ArrayList<Player> players) {
			FireworkSpawner.spawn(location, effect, players);
		}

		@Override
		public Fireball spawnFireball(Location location, Player sender) {
			final EntityMeteor meteor = new EntityMeteor((net.minecraft.server.v1_8_R3.World) ((CraftWorld) location.getWorld()).getHandle(), sender);
			meteor.setPosition(location.getX() + (double) MathUtils.random(-20, 20), location.getY() + 40.0, location.getZ() + (double) MathUtils.random(-20, 20));
			((CraftWorld) location.getWorld()).getHandle().addEntity((net.minecraft.server.v1_8_R3.Entity) meteor);
			return (Fireball) meteor.getBukkitEntity();
		}

	}

	private static Object getPrivateStatic(@SuppressWarnings("rawtypes") Class clazz, String f) throws Exception {
		return ReflectionUtils.getField(clazz, true, f).get(null);
	}

	@SuppressWarnings("unchecked")
	private static void a(Class<?> paramClass, String paramString, int paramInt) {
		try {
			((Map<String, Class<?>>) getPrivateStatic(EntityTypes.class, "c")).put(paramString, paramClass);
			((Map<Class<?>, String>) getPrivateStatic(EntityTypes.class, "d")).put(paramClass, paramString);
			((Map<Integer, Class<?>>) getPrivateStatic(EntityTypes.class, "e")).put(paramInt, paramClass);
			((Map<Class<?>, Integer>) getPrivateStatic(EntityTypes.class, "f")).put(paramClass, paramInt);
			((Map<String, Integer>) getPrivateStatic(EntityTypes.class, "g")).put(paramString, paramInt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}