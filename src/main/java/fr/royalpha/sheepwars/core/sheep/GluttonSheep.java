package fr.royalpha.sheepwars.core.sheep;

import fr.royalpha.sheepwars.core.handler.Particles;
import fr.royalpha.sheepwars.core.handler.SheepAbility;
import fr.royalpha.sheepwars.core.handler.Sounds;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.Plugin;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.api.SheepWarsSheep;
import fr.royalpha.sheepwars.core.util.RandomUtils;

public class GluttonSheep extends SheepWarsSheep
{
	private static final int RADIUS = 2;
	
    public GluttonSheep() {
		super(Message.Messages.GLUTTON_SHEEP_NAME, DyeColor.GREEN, 5, false, true, 0.8f, SheepAbility.EAT_BLOCKS);
	}

	@Override
	public boolean onGive(Player player) {
		return true;
	}

	@Override
	public void onSpawn(Player player, Sheep bukkitSheep, Plugin plugin) {
		// Do nothing 
	}

	@Override
	public boolean onTicking(Player player, long ticks, Sheep bukkitSheep, Plugin plugin) {
		final Location sheepLoc = bukkitSheep.getLocation();
		for (int x = -RADIUS; x < RADIUS; ++x) {
			for (int y = -RADIUS; y < RADIUS; ++y) {
				for (int z = -RADIUS; z < RADIUS; ++z) {
					final Location blockLoc = sheepLoc.clone().add(x, y, z);
					if (blockLoc.distance(sheepLoc) <= RADIUS && blockLoc.getBlock().getType() != Material.AIR) {
						blockLoc.getBlock().setType(Material.AIR);
						if (RandomUtils.getRandomByPercent(30)) {
							Sounds.playSoundAll(sheepLoc, Sounds.EAT, 1f, 1f);
							SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.SLIME, blockLoc, 0.05f, 0.05f, 0.05f, 5, 0.1f);
						}
					}
				}
			}
		}
		if (ticks <= 60L && ticks % 3L == 0L) {
            if (ticks == 60L) { 
            	Sounds.playSoundAll(bukkitSheep.getLocation(), Sounds.FUSE, 1f, 1f);
            }
            bukkitSheep.setColor((bukkitSheep.getColor() == DyeColor.WHITE) ? DyeColor.GREEN : DyeColor.WHITE);
        }
        return false;
	}

	@Override
	public void onFinish(Player player, Sheep bukkitSheep, boolean death, Plugin plugin) {
		if (!death) {
        	SheepWarsPlugin.getVersionManager().getWorldUtils().createExplosion(player, bukkitSheep.getLocation(), 3.7f);
        }
	}
}
