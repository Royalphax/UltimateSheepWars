package fr.asynchronous.sheepwars.v1_11_R1;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.util.MathUtils;
import fr.asynchronous.sheepwars.core.util.ReflectionUtils;
import fr.asynchronous.sheepwars.core.version.ICustomEntityType;
import fr.asynchronous.sheepwars.v1_11_R1.entity.CustomSheep;
import fr.asynchronous.sheepwars.v1_11_R1.entity.EntityMeteor;
import fr.asynchronous.sheepwars.v1_11_R1.entity.firework.FireworkSpawner;
import net.minecraft.server.v1_11_R1.BiomeBase;
import net.minecraft.server.v1_11_R1.BiomeBase.BiomeMeta;
import net.minecraft.server.v1_11_R1.Biomes;
import net.minecraft.server.v1_11_R1.EntityFireball;
import net.minecraft.server.v1_11_R1.EntityInsentient;
import net.minecraft.server.v1_11_R1.EntitySheep;
import net.minecraft.server.v1_11_R1.EntityTypes;
import net.minecraft.server.v1_11_R1.MinecraftKey;
import net.minecraft.server.v1_11_R1.RegistryMaterials;

public enum CustomEntityType
{
    @SuppressWarnings({ "unchecked", "rawtypes" })
	SHEEP("Sheep", 91, EntityType.SHEEP, (Class)EntitySheep.class, (Class)CustomSheep.class),
    @SuppressWarnings({ "unchecked", "rawtypes" })
	METEOR("Fireball", 12, EntityType.FIREBALL, (Class)EntityFireball.class, (Class)EntityMeteor.class);
    
    private String name;
    private int id;
    private EntityType entityType;
    private Class<? extends EntityInsentient> nmsClass;
    private Class<? extends EntityInsentient> customClass;

    private CustomEntityType(String name, int id, EntityType entityType, Class<? extends EntityInsentient> nmsClass,
                             Class<? extends EntityInsentient> customClass) {
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

    public static class GlobalMethods implements ICustomEntityType
    {

    	public void registerEntities() {
            for (CustomEntityType entity : values()) {
                a(entity.getID(), entity.getName().toLowerCase(), entity.getCustomClass(), entity.getName());
            }
            BiomeBase[] biomes;
            try {
            	biomes = new BiomeBase[] { Biomes.a, Biomes.c, Biomes.d, Biomes.e, Biomes.f, Biomes.g, Biomes.h, Biomes.i, Biomes.m, Biomes.n, Biomes.o, Biomes.p, Biomes.q, Biomes.r, Biomes.s, Biomes.t, Biomes.u, Biomes.w, Biomes.x, Biomes.y, Biomes.z, Biomes.A, Biomes.B, Biomes.C, Biomes.D, Biomes.E, Biomes.F, Biomes.G, Biomes.H, Biomes.I, Biomes.J, Biomes.K, Biomes.L, Biomes.M, Biomes.N, Biomes.O};
                //biomes = (BiomeBase[]) getPrivateStatic(BiomeBase.class, "biomes");
            } catch (Exception e) {
                return;
            }
            for (BiomeBase biomeBase : biomes) {
                if (biomeBase == null) {
                    break;
                }
                if (biomeBase.equals(BiomeBase.getBiome(8))) { //8 = NETHER
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
                        e.printStackTrace();
                    }
            }
        }

        @SuppressWarnings({ "unchecked" })
    	public void unregisterEntities() {
            for (CustomEntityType entity : values()) {
                /*try {
                    ((Map) getStaticField(EntityTypes.class, "c")).remove(entity.getCustomClass());
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                try {
                    //((Map) getStaticField(EntityTypes.class, "e")).remove(entity.getCustomClass());
                    ((List<String>) getStaticField(EntityTypes.class, "g")).remove(entity.getID());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (CustomEntityType entity : values())
                try {
                	a(entity.getID(), entity.getName().toLowerCase(), entity.getNMSClass(), entity.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            BiomeBase[] biomes;
            try {
            	biomes = new BiomeBase[] { Biomes.a, Biomes.c, Biomes.d, Biomes.e, Biomes.f, Biomes.g, Biomes.h, Biomes.i, Biomes.m, Biomes.n, Biomes.o, Biomes.p, Biomes.q, Biomes.r, Biomes.s, Biomes.t, Biomes.u, Biomes.w, Biomes.x, Biomes.y, Biomes.z, Biomes.A, Biomes.B, Biomes.C, Biomes.D, Biomes.E, Biomes.F, Biomes.G, Biomes.H, Biomes.I, Biomes.J, Biomes.K, Biomes.L, Biomes.M, Biomes.N, Biomes.O};
                //biomes = (BiomeBase[]) getPrivateStatic(BiomeBase.class, "biomes");
            } catch (Exception exc) {
                return;
            }
            for (BiomeBase biomeBase : biomes) {
                if (biomeBase == null)
                    break;
                if (biomeBase == BiomeBase.getBiome(8)) { //8 = Hell
                    continue;
                }

                for (String field : new String[]{"u", "v", "w", "x"})
                    try {
                        Field list = BiomeBase.class.getDeclaredField(field);
                        list.setAccessible(true);
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
			final EntityMeteor meteor = new EntityMeteor((net.minecraft.server.v1_11_R1.World)((CraftWorld)location.getWorld()).getHandle(), sender);
	        meteor.setPosition(location.getX() + (double)MathUtils.random(-20, 20), location.getY() + 40.0, location.getZ() + (double)MathUtils.random(-20, 20));
	        ((CraftWorld)location.getWorld()).getHandle().addEntity((net.minecraft.server.v1_11_R1.Entity)meteor);
	        return (Fireball)meteor.getBukkitEntity();
		}
    	
    }

    private static Object getStaticField(Class<EntityTypes> clazz, String f) throws Exception {
    	return ReflectionUtils.getField(clazz, true, f).get(null);
    }

    @SuppressWarnings("unchecked")
	private static void a(int paramInt, String paramString1, Class<?> paramClass, String paramString2) {
        /*try {
            ((List<String>) getStaticField(EntityTypes.class, "g")).set(paramInt, paramString2);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    	MinecraftKey localMinecraftKey = new MinecraftKey(paramString1);
    	/*try {
    		((Map<MinecraftKey, Class<?>>) getStaticField(RegistrySimple.class, "c")).remove(localMinecraftKey);
			((Map<MinecraftKey, Class<?>>) getStaticField(RegistrySimple.class, "c")).put(localMinecraftKey, paramClass);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		try {
			RegistryMaterials<MinecraftKey, Class<?>> instance = RegistryMaterials.class.newInstance();
			Method method = ReflectionUtils.getMethod(RegistryMaterials.class, "a", Integer.class, MinecraftKey.class, Class.class);
			method.setAccessible(true);
	    	method.invoke(instance, paramInt, localMinecraftKey, paramClass);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			e.printStackTrace();
		}
    }
}