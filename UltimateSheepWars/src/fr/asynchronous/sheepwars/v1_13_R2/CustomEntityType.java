package fr.asynchronous.sheepwars.v1_13_R2;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.util.MathUtils;
import fr.asynchronous.sheepwars.core.version.ICustomEntityType;
import fr.asynchronous.sheepwars.v1_13_R2.entity.CustomSheep;
import fr.asynchronous.sheepwars.v1_13_R2.entity.EntityMeteor;
import fr.asynchronous.sheepwars.v1_13_R2.entity.firework.FireworkSpawner;
import net.minecraft.server.v1_13_R2.BiomeBase;
import net.minecraft.server.v1_13_R2.BiomeBase.BiomeMeta;
import net.minecraft.server.v1_13_R2.Biomes;
import net.minecraft.server.v1_13_R2.EntityFireball;
import net.minecraft.server.v1_13_R2.EntityInsentient;
import net.minecraft.server.v1_13_R2.EntitySheep;
import net.minecraft.server.v1_13_R2.EntityTypes;
import net.minecraft.server.v1_13_R2.MinecraftKey;

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
				updateClass(entity, entity.getCustomClass());
			}
			BiomeBase[] biomes;
			try {
				biomes = new BiomeBase[]{Biomes.a, Biomes.c, Biomes.d, Biomes.e, Biomes.f, Biomes.g, Biomes.h, Biomes.i, Biomes.j, Biomes.k, Biomes.l, Biomes.m, Biomes.n, Biomes.o, Biomes.p, Biomes.q, Biomes.r, Biomes.s, Biomes.t, Biomes.u, Biomes.v, Biomes.w, Biomes.x, Biomes.y, Biomes.z, Biomes.A, Biomes.B, Biomes.C, Biomes.D, Biomes.E, Biomes.F, Biomes.G, Biomes.H, Biomes.I, Biomes.J, Biomes.K, Biomes.L, Biomes.M, Biomes.N, Biomes.O, Biomes.P, Biomes.Q, Biomes.R, Biomes.S, Biomes.T, Biomes.U, Biomes.V, Biomes.W, Biomes.X, Biomes.Y, Biomes.Z, Biomes.aa, Biomes.ab, Biomes.ac, Biomes.ad, Biomes.ae, Biomes.af, Biomes.ag, Biomes.ah, Biomes.ai, Biomes.aj, Biomes.ak};
			} catch (Exception e) {
				new ExceptionManager(e).register(true);
				return;
			}
			for (BiomeBase biomeBase : biomes) {
				if (biomeBase == null) {
					break;
				}
				if (biomeBase.equals(BiomeBase.)) { // 8 = NETHER
					continue;
				}
				for (String field : new String[]{"t", "u", "v", "w"})
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

		@SuppressWarnings({"unchecked"})
		public void unregisterEntities() {
			for (CustomEntityType entity : values())
				try {
					updateClass(entity, entity.getNMSClass());
				} catch (Exception e) {
					new ExceptionManager(e).register(true);
				}

			BiomeBase[] biomes;
			try {
				biomes = new BiomeBase[]{Biomes.a, Biomes.c, Biomes.d, Biomes.e, Biomes.f, Biomes.g, Biomes.h, Biomes.i, Biomes.j, Biomes.k, Biomes.l, Biomes.m, Biomes.n, Biomes.o, Biomes.p, Biomes.q, Biomes.r, Biomes.s, Biomes.t, Biomes.u, Biomes.v, Biomes.w, Biomes.x, Biomes.y, Biomes.z, Biomes.A, Biomes.B, Biomes.C, Biomes.D, Biomes.E, Biomes.F, Biomes.G, Biomes.H, Biomes.I, Biomes.J, Biomes.K, Biomes.L, Biomes.M, Biomes.N, Biomes.O, Biomes.P, Biomes.Q, Biomes.R, Biomes.S, Biomes.T, Biomes.U, Biomes.V, Biomes.W, Biomes.X, Biomes.Y, Biomes.Z, Biomes.aa, Biomes.ab, Biomes.ac, Biomes.ad, Biomes.ae, Biomes.af, Biomes.ag, Biomes.ah, Biomes.ai, Biomes.aj, Biomes.ak};
			} catch (Exception e) {
				new ExceptionManager(e).register(true);
				return;
			}
			for (BiomeBase biomeBase : biomes) {
				if (biomeBase == null)
					break;
				if (biomeBase == BiomeBase.getBiome(8)) { // 8 = Hell
					continue;
				}

				for (String field : new String[]{"t", "u", "v", "w"})
					try {
						Field list = BiomeBase.class.getDeclaredField(field);
						list.setAccessible(true);
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
			final EntityMeteor meteor = new EntityMeteor((net.minecraft.server.v1_13_R2.World) ((CraftWorld) location.getWorld()).getHandle(), sender);
			meteor.setPosition(location.getX() + (double) MathUtils.random(-20, 20), location.getY() + 40.0, location.getZ() + (double) MathUtils.random(-20, 20));
			((CraftWorld) location.getWorld()).getHandle().addEntity((net.minecraft.server.v1_13_R2.Entity) meteor);
			return (Fireball) meteor.getBukkitEntity();
		}

	}

	private static void updateClass(CustomEntityType entity, Class<? extends EntityInsentient> clazz) {
		try {
			MinecraftKey localMinecraftKey = new MinecraftKey(entity.getName().toLowerCase());
		    EntityTypes.b.a(entity.getID(), localMinecraftKey, clazz);

		} catch (Exception e) {
			new ExceptionManager(e).register(true);
		}
	}
}