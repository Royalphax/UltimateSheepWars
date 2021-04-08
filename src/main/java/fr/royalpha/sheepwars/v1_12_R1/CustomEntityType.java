package fr.royalpha.sheepwars.v1_12_R1;

import fr.royalpha.sheepwars.core.util.MathUtils;
import fr.royalpha.sheepwars.core.version.ICustomEntityType;
import fr.royalpha.sheepwars.v1_12_R1.entity.CustomSheep;
import fr.royalpha.sheepwars.v1_12_R1.entity.EntityCancelMove;
import fr.royalpha.sheepwars.v1_12_R1.entity.EntityMeteor;
import fr.royalpha.sheepwars.v1_12_R1.entity.firework.FireworkSpawner;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public enum CustomEntityType {
	@SuppressWarnings({"unchecked", "rawtypes"})
	SHEEP("CustomSheep", 91, EntityType.SHEEP, EntitySheep.class, CustomSheep.class),
	@SuppressWarnings({"unchecked", "rawtypes"})
	CANCEL_MOVE("CancelMoveEntity", 30, EntityType.ARMOR_STAND, EntityArmorStand.class, EntityCancelMove.class),
	@SuppressWarnings({"unchecked", "rawtypes"})
	METEOR("Meteor", 12, EntityType.FIREBALL, EntityFireball.class, EntityMeteor.class);

	private String name;
	private int id;
	private EntityType entityType;
	private Class<? extends Entity> nmsClass;
	private Class<? extends Entity> customClass;
	private MinecraftKey key;
	private MinecraftKey oldKey;

	private CustomEntityType(String name, int id, EntityType entityType, Class<? extends Entity> nmsClass, Class<? extends Entity> customClass) {
		this.name = name;
		this.id = id;
		this.entityType = entityType;
		this.nmsClass = nmsClass;
		this.customClass = customClass;
		this.key = new MinecraftKey(name);
		this.oldKey = EntityTypes.b.b(nmsClass);
	}

	private void register() {
		EntityTypes.d.add(key);
		EntityTypes.b.a(id, key, customClass);
	}

	private void unregister() {
		EntityTypes.d.remove(key);
		EntityTypes.b.a(id, oldKey, nmsClass);
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

	public Class<?> getCustomClass() {
		return customClass;
	}
	
	public static class GlobalMethods implements ICustomEntityType {

		public void registerEntities() {
			for (CustomEntityType ce : CustomEntityType.values()) ce.register();
		}

		@SuppressWarnings({"unchecked"})
		public void unregisterEntities() {
			for (CustomEntityType ce : CustomEntityType.values()) ce.unregister();
		}

		@Override
		public void spawnInstantExplodingFirework(Location location, FireworkEffect effect, ArrayList<Player> players) {
			FireworkSpawner.spawn(location, effect, players);
		}

		@Override
		public Fireball spawnFireball(Location location, Player sender) {
			final EntityMeteor meteor = new EntityMeteor((net.minecraft.server.v1_12_R1.World) ((CraftWorld) location.getWorld()).getHandle(), sender);
			meteor.setPosition(location.getX() + (double) MathUtils.random(-20, 20), location.getY() + 40.0, location.getZ() + (double) MathUtils.random(-20, 20));
			((CraftWorld) location.getWorld()).getHandle().addEntity((net.minecraft.server.v1_12_R1.Entity) meteor);
			return (Fireball) meteor.getBukkitEntity();
		}

	}
}