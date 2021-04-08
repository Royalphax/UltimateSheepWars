package fr.royalpha.sheepwars.core.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.royalpha.sheepwars.core.legacy.LegacyMaterial;
import fr.royalpha.sheepwars.api.Language;
import fr.royalpha.sheepwars.api.SheepWarsTeam;
import fr.royalpha.sheepwars.api.util.ItemBuilder;
import fr.royalpha.sheepwars.core.handler.Sounds;
import fr.royalpha.sheepwars.core.manager.ConfigManager;
import fr.royalpha.sheepwars.core.message.Message;
import fr.royalpha.sheepwars.core.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.api.SheepWarsKit;
import fr.royalpha.sheepwars.api.SheepWarsKit.SheepWarsKitLevel;

public class PreGameTask extends BukkitRunnable {
	private final SheepWarsPlugin plugin;
	private int maxSeconds = ConfigManager.getInt(ConfigManager.Field.PREGAME_TASK_COUNTDOWN);
	private int seconds = maxSeconds;

	public PreGameTask(final SheepWarsPlugin plugin) {
		this.plugin = plugin;
		this.runTaskTimer(plugin, 0L, 20L);
	}

	public void run() {
		if (seconds < 6) {
			switch (seconds) {
				case 5 :
				case 4 :
					Sounds.playSoundAll(null, Sounds.NOTE_STICKS, 1f, 1f);
					break;
				case 3 :
					Sounds.playSoundAll(null, Sounds.ORB_PICKUP, 1f, 0.5f);
					break;
				case 2 :
					Sounds.playSoundAll(null, Sounds.ORB_PICKUP, 1f, 1f);
					break;
				case 1 :
					Sounds.playSoundAll(null, Sounds.ORB_PICKUP, 1f, 1.5f);
					break;
				case 0 :
					Sounds.playSoundAll(null, Sounds.EXPLODE, 1f, 1.0f);
					for (Player online : Bukkit.getOnlinePlayers())
						online.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5, 1, false, false));
					break;
			}
		}
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (seconds == 5) {
				SheepWarsPlugin.getVersionManager().getTitleUtils().titlePacket(online, 4, 25, 0, ChatColor.AQUA + "" + seconds, Message.getMessage(online, ChatColor.GOLD.toString(), Message.Messages.PRE_START_COUNTDOWN_SUBTITLE, ""));
			} else if (seconds < 5) {
				SheepWarsPlugin.getVersionManager().getTitleUtils().titlePacket(online, 0, 25, 0, ChatColor.AQUA + "" + seconds, Message.getMessage(online, ChatColor.GOLD.toString(), Message.Messages.PRE_START_COUNTDOWN_SUBTITLE, ""));
			}
			else {
				if (seconds < maxSeconds - 5) {
					SheepWarsPlugin.getVersionManager().getTitleUtils().titlePacket(online, 4, 12, 4, ChatColor.AQUA + "" + seconds, "");
				} else {
					break;
				}
			}
		}
		if (this.seconds == 0) {
			this.cancel();
			/** Permet de stocker les listeners de kits déjà enregistrés **/
			final List<SheepWarsKitLevel> alreadyRegistred = new ArrayList<>();
			for (Player player : Bukkit.getOnlinePlayers()) {
				/** On equipe le kit **/
				EntityUtils.resetPlayer(player, GameMode.SURVIVAL);
				final PlayerData data = PlayerData.getPlayerData(player);
				final SheepWarsTeam team = data.getTeam();
				final Color color = team.getLeatherColor();
				final SheepWarsKit kit = data.getKit();
				final SheepWarsKitLevel level = kit.getLevel(data.getKitLevel());
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
					player.getInventory().addItem(new ItemBuilder(LegacyMaterial.WOODEN_SWORD.getMaterial()).setUnbreakable().toItemStack());
					player.getInventory().setItem(9, new ItemStack(Material.ARROW));
				}
				/** Une partie de plus que le joueur joue **/
				data.increaseGames(1);
				/** On unfreeze le joueur **/
				data.disableMovements(false);
				SheepWarsPlugin.getVersionManager().getTitleUtils().titlePacket(player, 5, 50, 20, data.getLanguage().getMessage(Message.Messages.GAME_START_TITLE), data.getLanguage().getMessage(Message.Messages.GAME_START_SUBTITLE).replace("%TIME%", ConfigManager.getInt(ConfigManager.Field.GIVE_SHEEP_INTERVAL).toString()));
			}
			/** Update le score board **/
			updateScoreboard();
			new GameTask(this.plugin);
		}
		this.seconds--;
	}

	private void updateScoreboard() {
		for (SheepWarsTeam team : Arrays.asList(SheepWarsTeam.RED, SheepWarsTeam.BLUE))
			team.inGameRules();
		for (Language lang : Language.getLanguages()) {
			lang.getScoreboardWrapper().setLine(8, ChatColor.RED + "", true);
			final Scoreboard board = lang.getScoreboardWrapper().getScoreboard();
			Objective obj = board.getObjective("health");
			if (obj != null) {
				obj.unregister();
			}
			obj = board.registerNewObjective("health", "health");
			obj.setDisplayName(ChatColor.RED + "\u2764");
			obj.setDisplaySlot(DisplaySlot.BELOW_NAME);

			final Objective kills = board.getObjective("kills");
			if (kills != null) {
				kills.unregister();
			}
			board.registerNewObjective("kills", "playerKillCount").setDisplaySlot(DisplaySlot.PLAYER_LIST);
			for (Player player : Bukkit.getOnlinePlayers())
				lang.getScoreboardWrapper().getScoreboard().getObjective("health").getScore(player.getName()).setScore((int) Math.round(player.getHealth()));
		}
	}
}