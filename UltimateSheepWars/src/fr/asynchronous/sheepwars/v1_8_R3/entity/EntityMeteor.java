package fr.asynchronous.sheepwars.v1_8_R3.entity;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityFireball;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.MovingObjectPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_8_R3.World;

public class EntityMeteor extends EntityFireball
{
    private final float speedModifier = 1.05f;
    private final float impactPower = 4.0f;
    //private boolean explosion;
    
    public EntityMeteor(final World world) {
        super(world);
    }
    
    public EntityMeteor(final World world, final Player shooter) {
        super(world);
        this.shooter = (EntityLiving)((CraftPlayer)shooter).getHandle();
    }
    
    public void t_() {
        if (this.inWater) {
            this.world.createExplosion((Entity)this.shooter, this.locX, this.locY, this.locZ, impactPower, true, true);
            this.die();
        }
        else {
        	super.t_();
            /*if (this.explosion) {
                location.getWorld().playEffect(location, Effect.SMOKE, 1);
            }*/
            this.motX *= speedModifier;
            this.motY *= speedModifier;
            this.motZ *= speedModifier;
            playParticles(Particles.EXPLOSION_NORMAL, this.getBukkitEntity().getLocation(), 0.0f, 0.0f, 0.0f, 1, 0.1f);
            playParticles(Particles.SMOKE_NORMAL, this.getBukkitEntity().getLocation(), 0.0f, 0.0f, 0.0f, 1, 0.2f);
        }
    }
    
    public void a(final MovingObjectPosition movingobjectposition) {
        this.world.createExplosion((Entity)this.shooter, this.locX, this.locY, this.locZ, impactPower, true, true);
        this.die();
    }
    
    public void playParticles(Particles particle, Location location, Float fx, Float fy, Float fz, int amount,
			Float particleData, int... list) {
		ArrayList<OfflinePlayer> copy = new ArrayList<>(PlayerData.getParticlePlayers());
    	if (!copy.isEmpty())
    	{
			PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.a(particle.getId()), true, (float)location.getX(), (float)location.getY(), (float)location.getZ(), fx, fy, fz, particleData, amount, list);
			for (OfflinePlayer p : copy)
			{
				if ((p.isOnline()) && (p != null) && (p instanceof org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer))
				{
					((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
				}
			}
    	}
	}
}
