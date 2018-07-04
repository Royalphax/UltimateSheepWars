package fr.asynchronous.sheepwars.core.kit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public class BetterBowKit extends KitManager {

	public static final int PERCENT_TO_KNOCKBACK = 20;
	public static final int PERCENT_TO_CRITICAL = 10;
	
	public BetterBowKit() {
		super(1, MsgEnum.KIT_BETTER_BOW_NAME, MsgEnum.KIT_BETTER_BOW_DESCRIPTION, "sheepwars.kit.betterbow", 10, 10, new ItemBuilder(Material.BOW));
	}

	@Override
	public boolean onEquip(Player player) {
		return true;
	}
	
	@EventHandler
    public void onProjectileLaunch(final ProjectileLaunchEvent event) {
		if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {
			final Arrow arrow = (Arrow) event.getEntity();
			final Player player = (Player) arrow.getShooter();
			final PlayerData data = PlayerData.getPlayerData(player);
			if (data.getKit().getId() == this.getId()) {
				boolean boostedArrow = false;
				if (RandomUtils.getRandomByPercent(PERCENT_TO_KNOCKBACK)) {
					arrow.setKnockbackStrength(2);
					boostedArrow = true;
				}
				if (RandomUtils.getRandomByPercent(PERCENT_TO_CRITICAL)) {
					arrow.setCritical(true);
					boostedArrow = true;
				}
				if (boostedArrow)
					boostedArrowAnimation(arrow);
			}
        }
    }
    
    private void boostedArrowAnimation(final Arrow arrow)
    {
    	new BukkitRunnable()
    	{
    		private Location lastLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
    		public void run() {
    			if (!arrow.isDead() && !arrow.isOnGround()) {
    				lastLocation = arrow.getLocation();
    				UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.REDSTONE, arrow.getLocation(), 0.0f, 0.0f, 0.0f, 1, 0.0f);
    			} else {
    				cancel();
    				UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.FLAME, lastLocation, 0.1f, 0.1f, 0.1f, 3, 0.05f);
    			}
    		}
    	}.runTaskTimer(getPlugin(), 0, 0);
    }

}
