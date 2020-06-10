package fr.asynchronous.sheepwars.v1_8_R3.entity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.asynchronous.sheepwars.api.PlayerData;
import fr.asynchronous.sheepwars.v1_8_R3.util.BossBar;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.World;

public class EntityBossBar extends EntityWither {
	private static final float ENTITY_MAX_HEALTH = 300.0f; // 200 si EnderDragon
	private static final float ENTITY_MIN_HEALTH = 1.0f;
	private static final float ENTITY_DISTANCE   = 32.0f;

	private final boolean      isBossBarEntity;
	private Player             viewer;
	private BossBar            bar;

	public EntityBossBar(final World world) {
		super(world);
		this.isBossBarEntity = false;
	}

	public EntityBossBar(final Player viewer, final BossBar bar) {
		super(((CraftWorld) viewer.getWorld()).getHandle());
		this.isBossBarEntity = true;
		this.viewer = viewer;
		this.bar = bar;

		setCustomName(PlayerData.getPlayerData(viewer).getLanguage().getMessage(bar.getMessage()));
		setCustomNameVisible(false);
		setHealth(bar.getProgress() * ENTITY_MAX_HEALTH);
		setInvisible(true);
		// -- Wither properties ---
		this.getDataWatcher().watch(7, 0);
		this.getDataWatcher().watch(8, (byte) 1);
		this.getDataWatcher().watch(20, 880);
		// --- --- --- --- ---- ---
		final Location spawn = getEntityLocation(viewer.getEyeLocation().clone());
		setPosition(spawn.getX(), spawn.getY(), spawn.getZ());
	}

	public Location getEntityLocation(Location base) {
		final Vector dir = base.getDirection().multiply(ENTITY_DISTANCE);
		return dir.add(base.toVector()).toLocation(base.getWorld());
	}

	public void spawnClientEntity() {
		final PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(this);
		sendPacket(this.viewer, packet);
	}

	public void destroyClientEntity() {
		final PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(this.getId());
		sendPacket(this.viewer, packet);
	}

	public void updateClientEntityName() {
		final DataWatcher dWatcher = this.getDataWatcher();
		dWatcher.watch(2, PlayerData.getPlayerData(this.viewer).getLanguage().getMessage(this.bar.getMessage()));

		final PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(this.getId(), dWatcher, true);
		sendPacket(this.viewer, packet);
	}

	public void updateClientEntityHealth() {
		final DataWatcher dWatcher = this.getDataWatcher();
		dWatcher.watch(6, MathHelper.a(this.bar.getProgress() * ENTITY_MAX_HEALTH, ENTITY_MIN_HEALTH, ENTITY_MAX_HEALTH));

		final PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(this.getId(), dWatcher, true);
		sendPacket(this.viewer, packet);
	}

	public void updateClientEntityLocation() {
		final Location spawn = getEntityLocation(this.viewer.getEyeLocation().clone());
		setPosition(spawn.getX(), spawn.getY(), spawn.getZ());

		final PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(this);
		sendPacket(this.viewer, packet);
	}

	public boolean isBossBarEntity() {
		return this.isBossBarEntity;
	}

	private static void sendPacket(Player player, Packet<?> packet) {
		((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
}
