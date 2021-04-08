package fr.royalpha.sheepwars.v1_15_R1.entity;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Player;

public class EntityCancelMove extends EntityArmorStand
{
    public Player player;

    public EntityCancelMove(EntityTypes<? extends EntityArmorStand> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityCancelMove(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    public EntityCancelMove(final Player player) {
        super(net.minecraft.server.v1_15_R1.EntityTypes.ARMOR_STAND, ((CraftWorld) player.getWorld()).getHandle());
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
        this.passengers.add(((org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer) this.player).getHandle());
        final PacketPlayOutMount packet = new PacketPlayOutMount(this);
        sendPacket(this.player, packet);
    }

    public void updateClientEntityLocation() {
        final Location loc = this.player.getLocation();
        setPositionRotation(loc.getX(), loc.getY() - 1, loc.getZ(), loc.getYaw(), loc.getPitch());

        final PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(this);
        sendPacket(this.player, packet);
    }

    public void unrideClientEntity() {
        this.passengers.add(((org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer) this.player).getHandle());
        final PacketPlayOutMount packet = new PacketPlayOutMount(this);
        sendPacket(this.player, packet);
    }

    private static void sendPacket(Player player, Packet<?> packet) {
        ((org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
