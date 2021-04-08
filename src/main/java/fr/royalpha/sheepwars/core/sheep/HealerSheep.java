package fr.royalpha.sheepwars.core.sheep;

import fr.royalpha.sheepwars.api.SheepWarsTeam;
import fr.royalpha.sheepwars.core.handler.Particles;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.royalpha.sheepwars.api.SheepWarsAPI;
import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.api.SheepWarsSheep;


public class HealerSheep extends SheepWarsSheep
{
	private static final int RADIUS = 10;
	private static final String HEALER_SHEEP_LEVEL_METADATA = "healer_sheep_level";
	
    public HealerSheep() {
		super(Message.Messages.HEALER_SHEEP_NAME, DyeColor.PINK, 5, true, false);
	}

	@Override
	public boolean onGive(Player player) {
		return true;
	}

	@Override
	public void onSpawn(Player player, Sheep bukkitSheep, Plugin plugin) {
		int level = 1;
		for (Entity ent : player.getWorld().getNearbyEntities(player.getLocation(), RADIUS, RADIUS, RADIUS)) {
			if (ent.hasMetadata(SheepWarsAPI.SHEEPWARS_SHEEP_ID_METADATA)) {
				int id = (int) ent.getMetadata(SheepWarsAPI.SHEEPWARS_SHEEP_ID_METADATA).get(0).value();
				if (id != -1 && id == this.getId()) {
					level++;
				}
			}
		}
		bukkitSheep.setMetadata(HEALER_SHEEP_LEVEL_METADATA, new FixedMetadataValue(plugin, level));
	}

	@Override
	public boolean onTicking(Player player, long ticks, Sheep bukkitSheep, Plugin plugin) {
		final int level = (int) bukkitSheep.getMetadata(HEALER_SHEEP_LEVEL_METADATA).get(0).value();
		if (ticks % 20L == 0L) {
            final SheepWarsTeam playerTeam = PlayerData.getPlayerData(player).getTeam();
            for (final Entity entity : bukkitSheep.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
                if (entity instanceof Player) {
                    final Player nearby = (Player)entity;
                    final SheepWarsTeam team = PlayerData.getPlayerData(nearby).getTeam();
                    if (team != playerTeam) {
                        continue;
                    }
                    boolean hasLowerRegen = true;
                    for (PotionEffect effects : nearby.getActivePotionEffects()) {
                    	if (effects.getType() == PotionEffectType.REGENERATION && effects.getAmplifier() >= level)
                    		hasLowerRegen = false;
                    }
                    if (hasLowerRegen) {
                    	SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.HEART, nearby.getLocation().add(0, 2.25, 0), 0f, 0f, 0f, 1, 0.1f);
                    	nearby.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, level));
                    }
                }
            }
        }
        else if (ticks % 5L == 0L) {
        	SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.HEART, bukkitSheep.getLocation().add(0, 1.5, 0), 0.5f, 0.5f, 0.5f, level, 0.1f);
        }
        return false;
	}

	@Override
	public void onFinish(Player player, Sheep bukkitSheep, boolean death, Plugin plugin) {
		// Do nothing
	}
}
