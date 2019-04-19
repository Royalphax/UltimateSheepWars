package fr.asynchronous.sheepwars.v1_8_R3.entity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.World;

public class EntityCancelMove extends EntityArmorStand
{
	public Player player;
	
    public EntityCancelMove(final World world) {
        super(world);
    }
    
    public EntityCancelMove(final Player player) {
        super(((CraftWorld) player.getWorld()).getHandle());
        this.player = player;
		setInvisible(true);
		setSmall(true);
		setGravity(false);
    }
    
    public void spawnClientEntity() {
		final PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(this);
		sendPacket(this.player, packet);
	}

    public void destroyClientEntity() {
    	final PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(this.getId());
		sendPacket(this.player, packet);
	}

    public void rideClientEntity() {
		final PacketPlayOutAttachEntity packet = new PacketPlayOutAttachEntity(0, ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle(), this);
		sendPacket(this.player, packet);
	}
    
    public void updateClientEntityLocation() {
    	final Location loc = this.player.getLocation();
		setPositionRotation(loc.getX(), loc.getY() - 1, loc.getZ(), loc.getYaw(), loc.getPitch());
		
		final PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(this);
		sendPacket(this.player, packet);
	}
    
    public void unrideClientEntity() {
    	final PacketPlayOutAttachEntity packet = new PacketPlayOutAttachEntity(0, ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle(), null);
		sendPacket(this.player, packet);
	}
    
    private static void sendPacket(Player player, Packet<?> packet) {
		((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
}
