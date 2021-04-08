package fr.royalpha.sheepwars.core.particle;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.handler.Particles;
import fr.royalpha.sheepwars.core.handler.Particles.ParticleEffect.ParticleEffectType;

public class WitchSpiralEffect implements ParticleEffectType {

	float step = 0.0F;
    float i = 0.0F;

	@Override
	public void update(Player player, Boolean moving) {
		Location loc = player.getLocation();
		if (!moving)
        {
          double x = Math.sin(0.3141592653589793D * this.step) * 1.0D;
          double y = 0.3D * this.i;
          double z = Math.cos(0.3141592653589793D * this.step) * 1.0D;
          Vector v = new Vector(x, y, z);
          loc.add(v);
          SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.SPELL_WITCH, loc, 0.0f, 0.0f, 0.0f, 1, 0.0f);
          this.step += 1.0F;
          this.i = ((float)(this.i + 0.1D));
          if (this.i > 6.0F) {
            this.i = 0.0F;
          }
        }
        else
        {
        	SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.SPELL_WITCH, loc, 0.2f, 0.2f, 0.2f, 1, 0.1f);
        }
	}
}
