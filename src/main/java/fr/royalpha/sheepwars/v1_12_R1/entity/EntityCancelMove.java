package fr.royalpha.sheepwars.v1_12_R1.entity;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;

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
		setNoGravity(true);
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
		final PacketPlayOutAttachEntity packet = new PacketPlayOutAttachEntity(((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) player).getHandle(), this);
		sendPacket(this.player, packet);
	}
    
    public void updateClientEntityLocation() {
    	final Location loc = this.player.getLocation();
		setPositionRotation(loc.getX(), loc.getY() - 1, loc.getZ(), loc.getYaw(), loc.getPitch());
		
		final PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(this);
		sendPacket(this.player, packet);
	}
    
    public void unrideClientEntity() {
    	final PacketPlayOutAttachEntity packet = new PacketPlayOutAttachEntity(((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) player).getHandle(), null);
		sendPacket(this.player, packet);
	}
    
    private static void sendPacket(Player player, Packet<?> packet) {
		((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
}
