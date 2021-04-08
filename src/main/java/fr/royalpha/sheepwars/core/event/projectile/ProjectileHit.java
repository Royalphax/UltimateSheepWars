package fr.royalpha.sheepwars.core.event.projectile;

import java.util.ArrayList;

import fr.royalpha.sheepwars.api.SheepWarsBooster;
import fr.royalpha.sheepwars.core.handler.Sounds;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.material.Wool;
import org.bukkit.scheduler.BukkitRunnable;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.royalpha.sheepwars.core.task.BoosterWoolTask;
import fr.royalpha.sheepwars.core.util.BlockUtils;

public class ProjectileHit extends UltimateSheepWarsEventListener {
	public ProjectileHit(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onProjectileHit(final ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {
			final Arrow arrow = (Arrow) event.getEntity();
			final Player player = (Player) arrow.getShooter();
			new BukkitRunnable() {
				public void run() {
					final Block sourceBlock = arrow.getLocation().getBlock();
					ArrayList<Block> arrayList = BlockUtils.getSurrounding(sourceBlock, true, true);
					Block block = null;
					for (Block blc : arrayList)
						if (blc.getType().toString().contains("WOOL") && blc.hasMetadata(BoosterWoolTask.BOOSTER_METADATA)) {
							block = blc;
							break;
						}
					if (block != null && block.getType().toString().contains("WOOL")) {
						final PlayerData data = PlayerData.getPlayerData(player);
						final Wool wool = (Wool) block.getState().getData();
						if (data.hasTeam()) {
							block.setType(Material.AIR);
							Sounds.playSoundAll(block.getLocation(), Sounds.CHICKEN_EGG_POP, 5f, 2f);
							final SheepWarsBooster booster = SheepWarsBooster.activateBooster(player, wool.getColor(), plugin);
							for (Player online : Bukkit.getOnlinePlayers()) {
								PlayerData onlineData = PlayerData.getPlayerData(online);
								online.sendMessage(onlineData.getLanguage().getMessage(Message.Messages.BOOSTER_ACTION).replaceAll("%PLAYER%", data.getTeam().getColor() + player.getName()).replaceAll("%BOOSTER%", onlineData.getLanguage().getMessage(booster.getName())));
							}
						}
					}
					arrow.remove();
				}
			}.runTaskLater(this.plugin, 5);
		}
	}
}
