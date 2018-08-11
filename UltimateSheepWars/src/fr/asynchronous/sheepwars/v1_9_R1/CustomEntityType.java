package fr.asynchronous.sheepwars.v1_9_R1;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.util.MathUtils;
import fr.asynchronous.sheepwars.core.util.ReflectionUtils;
import fr.asynchronous.sheepwars.core.version.ICustomEntityType;
import fr.asynchronous.sheepwars.v1_9_R1.entity.CustomSheep;
import fr.asynchronous.sheepwars.v1_9_R1.entity.EntityMeteor;
import fr.asynchronous.sheepwars.v1_9_R1.entity.firework.FireworkSpawner;
import net.minecraft.server.v1_9_R1.BiomeBase;
import net.minecraft.server.v1_9_R1.BiomeBase.BiomeMeta;
import net.minecraft.server.v1_9_R1.Biomes;
import net.minecraft.server.v1_9_R1.EntityFireball;
import net.minecraft.server.v1_9_R1.EntityInsentient;
import net.minecraft.server.v1_9_R1.EntitySheep;
import net.minecraft.server.v1_9_R1.EntityTypes;

public enum CustomEntityType {
	@SuppressWarnings({"unchecked", "rawtypes"})
	SHEEP("Sheep", 91, EntityType.SHEEP, (Class) EntitySheep.class, (Class) CustomSheep.class),
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
				biomes = new BiomeBase[] { Biomes.a, Biomes.c, Biomes.d, Biomes.e, Biomes.f, Biomes.g, Biomes.h, Biomes.i, Biomes.j, Biomes.k, Biomes.l, Biomes.m, Biomes.n, Biomes.o, Biomes.p, Biomes.q, Biomes.r, Biomes.s, Biomes.t, Biomes.u, Biomes.v, Biomes.w, Biomes.x, Biomes.y, Biomes.z, Biomes.A, Biomes.B, Biomes.C, Biomes.D, Biomes.E, Biomes.F, Biomes.G, Biomes.H, Biomes.I, Biomes.J, Biomes.K, Biomes.L, Biomes.M, Biomes.N, Biomes.O, Biomes.P, Biomes.Q, Biomes.R, Biomes.S, Biomes.T, Biomes.U, Biomes.V, Biomes.W, Biomes.X, Biomes.Y, Biomes.Z, Biomes.aa, Biomes.ab, Biomes.ac, Biomes.ad, Biomes.ae, Biomes.af, Biomes.ag, Biomes.ah, Biomes.ai, Biomes.aj, Biomes.ak};
	        } catch (Exception exc) {
	        	new ExceptionManager(exc).register(true);
				return;
			}
			for (BiomeBase biomeBase : biomes) {
				if (biomeBase == null) {
					break;
				}
				if (biomeBase.equals(BiomeBase.getBiome(8))) {
					continue;
				}
				for (String field : new String[]{"u", "v", "w", "x"})
					try {
						Field list = BiomeBase.class.getDeclaredField(field);
						list.setAccessible(true);
						@SuppressWarnings("unchecked")
						List<BiomeMeta> mobList = (List<BiomeMeta>) list.get(biomeBase);
						for (BiomeMeta meta : mobList)
							for (CustomEntityType entity : values())
								if (entity.getNMSClass().equals(meta.b))
									meta.b = entity.getCustomClass();
					} catch (Exception e) {
						new ExceptionManager(e).register(true);
					}
			}
		}

		@SuppressWarnings("rawtypes")
		public void unregisterEntities() {
			for (CustomEntityType entity : values()) {
				try {
					((Map) getPrivateStatic(EntityTypes.class, "c")).remove(entity.getCustomClass());
				} catch (Exception e) {
					new ExceptionManager(e).register(true);
				}

				try {
					((Map) getPrivateStatic(EntityTypes.class, "e")).remove(entity.getCustomClass());
				} catch (Exception e) {
					new ExceptionManager(e).register(true);
				}
			}

			for (CustomEntityType entity : values())
				try {
					a(entity.getNMSClass(), entity.getName(), entity.getID());
				} catch (Exception e) {
					new ExceptionManager(e).register(true);
				}

			BiomeBase[] biomes;
			try {
				biomes = new BiomeBase[] { Biomes.a, Biomes.c, Biomes.d, Biomes.e, Biomes.f, Biomes.g, Biomes.h, Biomes.i, Biomes.j, Biomes.k, Biomes.l, Biomes.m, Biomes.n, Biomes.o, Biomes.p, Biomes.q, Biomes.r, Biomes.s, Biomes.t, Biomes.u, Biomes.v, Biomes.w, Biomes.x, Biomes.y, Biomes.z, Biomes.A, Biomes.B, Biomes.C, Biomes.D, Biomes.E, Biomes.F, Biomes.G, Biomes.H, Biomes.I, Biomes.J, Biomes.K, Biomes.L, Biomes.M, Biomes.N, Biomes.O, Biomes.P, Biomes.Q, Biomes.R, Biomes.S, Biomes.T, Biomes.U, Biomes.V, Biomes.W, Biomes.X, Biomes.Y, Biomes.Z, Biomes.aa, Biomes.ab, Biomes.ac, Biomes.ad, Biomes.ae, Biomes.af, Biomes.ag, Biomes.ah, Biomes.ai, Biomes.aj, Biomes.ak};
	        } catch (Exception exc) {
	        	new ExceptionManager(exc).register(true);
				return;
			}
			for (BiomeBase biomeBase : biomes) {
				if (biomeBase == null)
					break;
				if (biomeBase == BiomeBase.getBiome(8)) {
					continue;
				}

				for (String field : new String[]{"u", "v", "w", "x"})
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
						new ExceptionManager(e).register(true);
					}
			}
		}

		@Override
		public void spawnInstantExplodingFirework(Location location, FireworkEffect effect, ArrayList<Player> players) {
			FireworkSpawner.spawn(location, effect, players);
		}

		@Override
		public Fireball spawnFireball(Location location, Player sender) {
			final EntityMeteor meteor = new EntityMeteor((net.minecraft.server.v1_9_R1.World) ((CraftWorld) location.getWorld()).getHandle(), sender);
			meteor.setPosition(location.getX() + (double) MathUtils.random(-20, 20), location.getY() + 40.0, location.getZ() + (double) MathUtils.random(-20, 20));
			((CraftWorld) location.getWorld()).getHandle().addEntity((net.minecraft.server.v1_9_R1.Entity) meteor);
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
			new ExceptionManager(e).register(true);
		}
	}
}