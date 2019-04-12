package fr.asynchronous.sheepwars.core.task;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.ItemBuilder;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.kit.SheepWarsKit;
import fr.asynchronous.sheepwars.core.kit.SheepWarsKit.KitLevel;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.EntityUtils;
import fr.asynchronous.sheepwars.core.version.ATitleUtils.Type;

public class PreGameTask extends BukkitRunnable {
	private final SheepWarsPlugin plugin;
	private int seconds = 10;

	public PreGameTask(final SheepWarsPlugin plugin) {
		this.plugin = plugin;
		this.runTaskTimer(plugin, 0L, 20L);
	}

	public void run() {
		if (seconds < 6) {
			switch (seconds) {
				case 5 :
					Sounds.playSoundAll(null, Sounds.ORB_PICKUP, 1f, 0.4f);
					break;
				case 4 :
					Sounds.playSoundAll(null, Sounds.ORB_PICKUP, 1f, 0.8f);
					break;
				case 3 :
					Sounds.playSoundAll(null, Sounds.ORB_PICKUP, 1f, 1.2f);
					break;
				case 2 :
					Sounds.playSoundAll(null, Sounds.ORB_PICKUP, 1f, 1.6f);
					break;
				case 1 :
					Sounds.playSoundAll(null, Sounds.ORB_PICKUP, 1f, 2f);
					break;
			}
			for (Player online : Bukkit.getOnlinePlayers())
				SheepWarsPlugin.getVersionManager().getTitleUtils().titlePacket(online, 2, 25, 4, ChatColor.YELLOW + "" + seconds, Message.getMessage(online, ChatColor.AQUA.toString(), MsgEnum.PRE_START_SUBTITLE, ""));
		}
		this.seconds--;
		if (this.seconds == 0) {
			this.cancel();
			/** Permet de stocker les listeners de kits déjà enregistrés **/
			final List<KitLevel> alreadyRegistred = new ArrayList<>();
			for (Player player : Bukkit.getOnlinePlayers()) {
				/** On equipe le kit **/
				EntityUtils.resetPlayer(player, GameMode.SURVIVAL);
				final PlayerData data = PlayerData.getPlayerData(player);
				final TeamManager team = data.getTeam();
				final Color color = team.getLeatherColor();
				final SheepWarsKit kit = data.getKit();
				final KitLevel level = kit.getLevel(data.getKitLevel());
				if (!alreadyRegistred.contains(level)) {
					alreadyRegistred.add(level);
					Bukkit.getPluginManager().registerEvents(level, this.plugin);
				}
				boolean equipBasics = level.onEquip(player);
				if (equipBasics) {
					player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setLeatherArmorColor(color).addEnchant(Enchantment.PROTECTION_PROJECTILE, 2).setUnbreakable().toItemStack());
					player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherArmorColor(color).addEnchant(Enchantment.PROTECTION_PROJECTILE, 2).setUnbreakable().toItemStack());
					player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).setLeatherArmorColor(color).addEnchant(Enchantment.PROTECTION_PROJECTILE, 2).setUnbreakable().toItemStack());
					player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setLeatherArmorColor(color).addEnchant(Enchantment.PROTECTION_PROJECTILE, 2).setUnbreakable().toItemStack());
					player.getInventory().setItem(8, new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherArmorColor(color).setName(team.getColor() + "" + ChatColor.BOLD + team.getDisplayName(player)).setUnbreakable().toItemStack());
					player.getInventory().addItem(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_INFINITE, 1).setUnbreakable().toItemStack());
					player.getInventory().addItem(new ItemBuilder(Material.WOOD_SWORD).setUnbreakable().toItemStack());
					player.getInventory().setItem(9, new ItemStack(Material.ARROW));
				}
			}
			/** Update le SB **/
			updateScoreboard();
			for (Player player : Bukkit.getOnlinePlayers()) {
				/** On unfreeze le joueur **/
				SheepWarsPlugin.getVersionManager().getNMSUtils().cancelMove(player, false);
				/** On affiche le title **/
				final PlayerData data = PlayerData.getPlayerData(player);
				SheepWarsPlugin.getVersionManager().getTitleUtils().defaultTitle(Type.TITLE, player, data.getLanguage().getMessage(MsgEnum.GAME_START_TITLE), data.getLanguage().getMessage(MsgEnum.GAME_START_SUBTITLE).replace("%TIME%", ConfigManager.getInt(Field.GIVE_SHEEP_INTERVAL).toString()));
			}
			new GameTask(this.plugin);
		}
	}
	
	private void updateScoreboard() {
		for (Language lang : Language.getLanguages()) {
			lang.getScoreboardWrapper().setLine(8, ChatColor.RED + "", true);
			final Scoreboard board = lang.getScoreboardWrapper().getScoreboard();
			Objective obj = board.getObjective("health");
			if (obj != null) {
				obj.unregister();
			}
			obj = board.registerNewObjective("health", "health");
			obj.setDisplayName(ChatColor.RED + "❤");
			obj.setDisplaySlot(DisplaySlot.BELOW_NAME);

			final Objective kills = board.getObjective("kills");
			if (kills != null) {
				kills.unregister();
			}
			board.registerNewObjective("kills", "playerKillCount").setDisplaySlot(DisplaySlot.PLAYER_LIST);
			//for (Player player : Bukkit.getOnlinePlayers())
			//	lang.getScoreboardWrapper().getScoreboard().getObjective("health").getScore(player.getName()).setScore((int) Math.round(player.getHealth()));
		}
	}
}