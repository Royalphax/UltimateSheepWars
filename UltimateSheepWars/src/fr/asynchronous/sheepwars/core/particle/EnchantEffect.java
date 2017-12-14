package fr.asynchronous.sheepwars.core.particle;

import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Contributor.ParticleEffect.ParticleEffectType;
import fr.asynchronous.sheepwars.core.handler.Particles;

public class EnchantEffect implements ParticleEffectType {

	@Override
	public void update(Player player, Boolean moving) {
		if (!moving) {
			UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.ENCHANTMENT_TABLE, player.getLocation().add(0,1,0), 0.5F, 0.5F, 0.5F, 1, 0.1f);
		}
	}
}
