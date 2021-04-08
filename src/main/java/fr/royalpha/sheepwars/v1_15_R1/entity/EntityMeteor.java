package fr.royalpha.sheepwars.v1_15_R1.entity;

import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.core.handler.Particles;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class EntityMeteor extends EntityLargeFireball {
    private final float speedModifier = 1.05f;
    private final float impactPower = 3.0f;

    public EntityMeteor(EntityTypes<? extends EntityLargeFireball> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityMeteor(final World world, final Player shooter) {
        super(EntityTypes.FIREBALL, world);
        if (shooter instanceof EntityLiving) {
            this.shooter = (EntityLiving) shooter;
        }
    }

    @Override
    public void tick() {
        if (this.inWater) {
            this.world.createExplosion((Entity) this.shooter, this.locX(), this.locY(), this.locZ(), impactPower, true, Explosion.Effect.BREAK);
            this.die();
        } else {
            super.tick();
            this.setMot(getMot().getX() * speedModifier, getMot().getY() * speedModifier, getMot().getZ() * speedModifier);
            playParticles(Particles.EXPLOSION_NORMAL, this.getBukkitEntity().getLocation(), 0.0f, 0.0f, 0.0f, 1, 0.1f);
            playParticles(Particles.SMOKE_NORMAL, this.getBukkitEntity().getLocation(), 0.0f, 0.0f, 0.0f, 1, 0.2f);
        }
    }

    @Override
    public void a(final MovingObjectPosition movingobjectposition) {
        this.world.createExplosion((Entity) this.shooter, this.locX(), this.locY(), this.locZ(), impactPower, true, Explosion.Effect.BREAK);
        this.die();
    }

    public void playParticles(Particles particle, Location location, Float fx, Float fy, Float fz, int amount, Float particleData, int... list) {
        ArrayList<OfflinePlayer> copy = new ArrayList<>(PlayerData.getParticlePlayers());
        if (!copy.isEmpty())
            try {
                for (OfflinePlayer p : copy)
                    if ((p.isOnline()) && (p != null))
                        ((Player)p).spawnParticle(org.bukkit.Particle.valueOf(particle.toString()), location, amount, (double) fx, (double) fy, (double) fz, (double) particleData);
            } catch (Exception ex) {
                // Do nothing
            }
    }
}