package fr.asynchronous.sheepwars.core.kit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.api.PlayerData;
import fr.asynchronous.sheepwars.api.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.api.SheepWarsKit;
import fr.asynchronous.sheepwars.core.message.Message.Messages;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public class DestroyerKit extends SheepWarsKit {

	public static final Integer ID = 4;
	public static final Integer TNT_COUNT = 2;
	public static final Integer PERCENTAGE_TO_FIRE = 5;

	public DestroyerKit() {
		super(ID, Messages.KIT_DESTROYER_NAME, new ItemBuilder(Material.TNT), new DestroyerKitLevel());
	}

	public static class DestroyerKitLevel extends SheepWarsKitLevel {

		public DestroyerKitLevel() {
			super(Messages.KIT_DESTROYER_DESCRIPTION, "sheepwars.kit.destroyer", 10, 10);
		}

		@Override
		public boolean onEquip(Player player) {
			player.getInventory().setItem(2, new ItemStack(Material.TNT, TNT_COUNT));
			return true;
		}

		@EventHandler
		public void onProjectileLaunch(final ProjectileLaunchEvent event) {
			if (event.getEntity() instanceof Arrow) {
				final Arrow arrow = (Arrow) event.getEntity();
				if (arrow.getShooter() instanceof Player) {
					final Player player = (Player) arrow.getShooter();
					final PlayerData data = PlayerData.getPlayerData(player);
					if (data.getKit().getId() == this.getKitId() && RandomUtils.getRandomByPercent(PERCENTAGE_TO_FIRE))
						arrow.setFireTicks(Integer.MAX_VALUE);
				}
			}
		}

		@EventHandler
		public void onPlayerInteract(final PlayerInteractEvent event) {
			final Player player = event.getPlayer();
			final PlayerData data = PlayerData.getPlayerData(player);
			if (event.hasItem() && event.getItem().getType() == Material.TNT && data.getKit().getId() == this.getKitId()) {
				ItemStack item = event.getItem();
				ItemStack newItem = item.clone();
				final int amount = item.getAmount() - 1;
				if (amount <= 0) {
					newItem = new ItemStack(Material.AIR);
				} else {
					newItem.setAmount(item.getAmount() - 1);
				}
				SheepWarsPlugin.getVersionManager().getNMSUtils().setItemInHand(newItem, player);
				final org.bukkit.entity.TNTPrimed tnt = player.getWorld().spawn(player.getLocation().add(0, 2.0, 0), TNTPrimed.class);
				final Double velocity = SheepWarsPlugin.getWorldManager().getVotedMap().getSheepVelocity() / 1.5;

				tnt.setMetadata("no-damage-team-" + data.getTeam().getName(), new FixedMetadataValue(getPlugin(), true));
				Sounds.playSound(player, null, Sounds.HORSE_SADDLE, 1f, 1f);
				Sounds.playSound(player, null, Sounds.FUSE, 1f, 1f);
				tnt.setVelocity(new Vector(0.0, 0.1, 0.0).add(player.getLocation().getDirection().multiply((velocity - 0.5 > 0 ? velocity - 0.5 : 0.5))));

				new BukkitRunnable() {
					Location lastLoc = null;
					public void run() {
						if (tnt.isDead()) {
							if (this.lastLoc != null)
								SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.CLOUD, lastLoc, 0f, 0f, 0f, 20, 0.3f);
							this.cancel();
						}
						SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.SMOKE_NORMAL, tnt.getLocation().add(0, 0.5, 0), 0f, 0f, 0f, 3, 0.0f);
						this.lastLoc = tnt.getLocation();
					}
				}.runTaskTimer(getPlugin(), 0, 0);

				player.updateInventory();
			}
		}
	}
}