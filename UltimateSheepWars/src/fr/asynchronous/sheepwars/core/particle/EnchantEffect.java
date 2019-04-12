package fr.asynchronous.sheepwars.core.particle;

import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Particles.ParticleEffect.ParticleEffectType;

public class EnchantEffect implements ParticleEffectType {

	@Override
	public void update(Player player, Boolean moving) {
		if (!moving) {
			SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.ENCHANTMENT_TABLE, player.getLocation().add(0, 1, 0), 0.5F, 0.5F, 0.5F, 1, 0.1f);
		}
	}
}