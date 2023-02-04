package fr.royalpha.sheepwars.v1_15_R1;

import com.google.common.collect.BiMap;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import fr.royalpha.sheepwars.core.version.ICustomEntityType;
import fr.royalpha.sheepwars.v1_15_R1.entity.CustomSheep;
import fr.royalpha.sheepwars.v1_15_R1.entity.EntityCancelMove;
import fr.royalpha.sheepwars.v1_15_R1.entity.EntityMeteor;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

public class CustomEntityType<T extends Entity> {

    public static CustomEntityType<EntityCancelMove> ENTITY_CANCEL_MOVE;
    public static CustomEntityType<CustomSheep> CUSTOM_SHEEP;
    public static CustomEntityType<EntityMeteor> ENTITY_METEOR;

    @Nullable
    private static Field REGISTRY_MAT_MAP;

    static {
        try {
            REGISTRY_MAT_MAP = RegistryMaterials.class.getDeclaredField("c");
        } catch (ReflectiveOperationException err) {
            err.printStackTrace();
            REGISTRY_MAT_MAP = null;
            // technically should only occur if server version changes or jar is modified in "weird" ways
        }
    }

    private final MinecraftKey key;
    private final Class<T> clazz;
    private final EntityTypes.b<T> maker;
    private net.minecraft.server.v1_15_R1.EntityTypes<? super T> parentType;
    private net.minecraft.server.v1_15_R1.EntityTypes<T> entityType;
    private boolean registered;

    public CustomEntityType(String name, Class<T> customEntityClass, net.minecraft.server.v1_15_R1.EntityTypes<? super T> parentType, EntityTypes.b<T> maker) {
        this.key = MinecraftKey.a(name);
        this.clazz = customEntityClass;
        this.parentType = parentType;
        this.maker = maker;
    }

    public boolean isRegistered() {
        return registered;
    }

    public org.bukkit.entity.Entity spawn(WorldServer server, Location loc) {
        Entity entity = entityType.spawnCreature(server,
                null, null, null, new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()),
                EnumMobSpawn.EVENT, true, false);
        return entity == null ? null : entity.getBukkitEntity();
    }

    public void register() throws IllegalStateException {
        if (registered || net.minecraft.server.v1_15_R1.IRegistry.ENTITY_TYPE.getOptional(key).isPresent()) {
            // throw new IllegalStateException(String.format
            //       ("Unable to register entity with key '%s' as it is already registered.", key));
            return;
        }
        Map<Object, Type<?>> dataTypes = (Map<Object, Type<?>>) DataConverterRegistry.a()
                .getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion()))
                .findChoiceType(DataConverterTypes.ENTITY_TREE).types();
        dataTypes.put(key.toString(), dataTypes.get(parentType.h().toString().replace("entity/", "")));
        EntityTypes.a<T> a = EntityTypes.a.a(maker, EnumCreatureType.CREATURE);
        entityType = a.a(key.getKey());
        net.minecraft.server.v1_15_R1.IRegistry.a(net.minecraft.server.v1_15_R1.IRegistry.ENTITY_TYPE, key.getKey(), entityType);
        registered = true;
    }

    public void unregister() throws IllegalStateException {
        if (!registered) {
            throw new IllegalArgumentException(String.format
                    ("Entity with key '%s' could not be unregistered, as it is not in the registry", key));
        }
        Map<Object, Type<?>> dataTypes = (Map<Object, Type<?>>) DataConverterRegistry.a()
                .getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion()))
                .findChoiceType(DataConverterTypes.ENTITY_TREE).types();
        dataTypes.remove(key.toString());
        try {
            if (REGISTRY_MAT_MAP == null) {
                throw new ReflectiveOperationException("Field not initially found");
            }
            REGISTRY_MAT_MAP.setAccessible(true);
            Object o = REGISTRY_MAT_MAP.get(net.minecraft.server.v1_15_R1.IRegistry.ENTITY_TYPE);
            ((BiMap<MinecraftKey, ?>) o).remove(key);
            REGISTRY_MAT_MAP.set(net.minecraft.server.v1_15_R1.IRegistry.ENTITY_TYPE, o);
            REGISTRY_MAT_MAP.setAccessible(false);
            registered = false;
        } catch (ReflectiveOperationException err) {
            err.printStackTrace();
        }
    }

    public static class GlobalMethods implements ICustomEntityType {

        public void registerEntities() {
            ENTITY_CANCEL_MOVE = new CustomEntityType<EntityCancelMove>("cancel_move", EntityCancelMove.class, EntityTypes.ARMOR_STAND, EntityCancelMove::new);
            ENTITY_CANCEL_MOVE.register();

            CUSTOM_SHEEP = new CustomEntityType<CustomSheep>("custom_sheep", CustomSheep.class, EntityTypes.SHEEP, CustomSheep::new);
            CUSTOM_SHEEP.register();

            ENTITY_METEOR = new CustomEntityType<EntityMeteor>("entity_meteor", EntityMeteor.class, EntityTypes.FIREBALL, EntityMeteor::new);
            ENTITY_METEOR.register();
        }

        public void unregisterEntities() {
            ENTITY_CANCEL_MOVE.unregister();
            CUSTOM_SHEEP.unregister();
        }

        @Override
        public void spawnInstantExplodingFirework(Location location, FireworkEffect effect, ArrayList<Player> players) {
            Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();
            fwm.addEffect(effect);
            fwm.setPower(0);
            fw.setFireworkMeta(fwm);
            fw.detonate();
        }

        @Override
        public Fireball spawnFireball(Location location, Player sender) {
            final EntityMeteor meteor = new EntityMeteor(((CraftWorld) location.getWorld()).getHandle(), sender);
            final org.bukkit.entity.Entity ent = CustomEntityType.ENTITY_METEOR.spawn(((CraftWorld) location.getWorld()).getHandle(), location);
            return (org.bukkit.entity.LargeFireball) ent;
        }
    }
}
