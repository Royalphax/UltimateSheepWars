package fr.asynchronous.sheepwars.core.event.projectile;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.material.Wool;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.BoosterManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.task.BoosterWoolTask;
import fr.asynchronous.sheepwars.core.util.BlockUtils;

public class ProjectileHit extends UltimateSheepWarsEventListener {
	public ProjectileHit(final UltimateSheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onProjectileHit(final ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {
			final Arrow arrow = (Arrow) event.getEntity();
			final Player player = (Player) arrow.getShooter();
			Block sourceBlock = arrow.getLocation().getBlock();
			ArrayList<Block> arrayList = BlockUtils.getSurrounding(sourceBlock, true, true);
			Block block = null;
			for (Block blc : arrayList)
				if (blc.getType() == Material.WOOL && blc.hasMetadata(BoosterWoolTask.BOOSTER_METADATA)) {
					block = blc;
					break;
				}
			if (block != null && block.getType() == Material.WOOL) {
				final PlayerData data = PlayerData.getPlayerData(player);
				final Wool wool = (Wool) block.getState().getData();
				if (data.hasTeam()) {
					block.setType(Material.AIR);
					Sounds.playSoundAll(block.getLocation(), Sounds.CHICKEN_EGG_POP, 5f, 2f);
					final BoosterManager booster = BoosterManager.activateBooster(player, wool.getColor(), this.plugin);
					for (Player online : Bukkit.getOnlinePlayers()) {
						PlayerData onlineData = PlayerData.getPlayerData(online);
						online.sendMessage(onlineData.getLanguage().getMessage(MsgEnum.BOOSTER_ACTION).replaceAll("%PLAYER%", data.getTeam().getColor() + player.getName()).replaceAll("%BOOSTER%", onlineData.getLanguage().getMessage(booster.getName())));
					}
				}
			}
			// DEBUG
			sourceBlock.setType(Material.REDSTONE_BLOCK);
			for (Block b : BlockUtils.getSurrounding(sourceBlock, false, true))
				b.setType(Material.GLASS);
			// DEBUG
			arrow.remove();
		}
	}
}
