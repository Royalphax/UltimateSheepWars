package fr.royalpha.sheepwars.v1_12_R1.entity;

import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.core.handler.Particles;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class EntityMeteor extends EntityFireball
{
    private final float speedModifier = 1.05f;
    private final float impactPower = 3.0f;
    
    public EntityMeteor(final World world) {
        super(world);
    }
    
    public EntityMeteor(final World world, final Player shooter) {
        super(world);
        if (shooter instanceof EntityLiving)
        {
        	this.shooter = (EntityLiving) shooter;
        }
    }
    
    public void B_() {
        if (this.inWater) {
            this.world.createExplosion((Entity)this.shooter, this.locX, this.locY, this.locZ, impactPower, true, true);
            this.die();
        }
        else {
        	super.B_();
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
				if ((p.isOnline()) && (p != null) && (p instanceof org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer))
				{
					((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
				}
			}
    	}
	}
}
