package fr.asynchronous.sheepwars.core.task;

import java.util.Arrays;

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

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.kit.MoreHealthKit;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.EntityUtils;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.version.ATitleUtils.Type;

public class BeginCountdown extends BukkitRunnable {
	private boolean started = false;
	private boolean forced = false;
	private int timeUntilStart;
	private final UltimateSheepWarsPlugin plugin;

	public BeginCountdown(final UltimateSheepWarsPlugin plugin) {
		this.plugin = plugin;
		this.timeUntilStart = ConfigManager.getInt(Field.COUNTDOWN);
		plugin.setPreGameTask(this);
		this.runTaskTimer(plugin, 0L, 20L);
		start();
	}

	public void run() {
		if (this.timeUntilStart == 0) {
			this.cancel();
			boolean begin = false;
			begin = Bukkit.getOnlinePlayers().size() >= (forced ? 2 : ConfigManager.getInt(Field.MIN_PLAYERS));
			if (!begin) {
				Message.broadcast(MsgEnum.PLAYERS_DEFICIT);
				this.timeUntilStart = ConfigManager.getInt(Field.COUNTDOWN);
				started = false;
			} else {
				GameState.setCurrentStep(GameState.INGAME);
				for (TeamManager team : Arrays.asList(TeamManager.RED, TeamManager.BLUE))
					team.inGameRules();
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
				}
				Boolean shakeUp = (TeamManager.BLUE.getOnlinePlayers().isEmpty() || TeamManager.RED.getOnlinePlayers().isEmpty());
				for (Player player : Bukkit.getOnlinePlayers()) {
					PlayerData data = PlayerData.getPlayerData(player);
					if (!data.hasTeam() || shakeUp) {
						TeamManager team = TeamManager.getRandomTeam();
						team.addPlayer(player);
					}
					EntityUtils.resetPlayer(player, GameMode.SURVIVAL);
					final TeamManager team = data.getTeam();
					final Color color = team.getLeatherColor();
					final KitManager kit = data.getKit();
					boolean equipBasics = kit.onEquip(player);
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
					for (Language lang : Language.getLanguages())
						lang.getScoreboardWrapper().getScoreboard().getObjective("health").getScore(player.getName()).setScore((kit == new MoreHealthKit() ? 24 : 20));
					data.increaseGames(1);
					player.setFallDistance(0.0f);
					player.teleport(team.getNextSpawn());
					UltimateSheepWarsPlugin.getVersionManager().getTitleUtils().defaultTitle(Type.TITLE, player, data.getLanguage().getMessage(MsgEnum.GAME_START_TITLE), data.getLanguage().getMessage(MsgEnum.GAME_START_SUBTITLE).replace("%TIME%", ConfigManager.getInt(Field.GIVE_SHEEP_INTERVAL).toString()));
				}
				new GameTask(this.plugin);
			}
			return;
		}
		int remainingMins = this.timeUntilStart / 60 % 60;
		int remainingSecs = this.timeUntilStart % 60;
		for (Player online : Bukkit.getOnlinePlayers())
			online.setLevel(this.timeUntilStart);
		if (this.timeUntilStart % 30 == 0 || (remainingMins == 0 && (remainingSecs % 10 == 0 || remainingSecs < 10))) {
			for (Player online : Bukkit.getOnlinePlayers())
				UltimateSheepWarsPlugin.getVersionManager().getTitleUtils().actionBarPacket(online, Message.getMessage(online, MsgEnum.STARTING_GAME).replaceAll("%TIME%", (remainingMins > 0 ? (remainingMins > 1 ? remainingMins + " " + Message.getMessage(online, MsgEnum.MINUTES) : remainingMins + " " + Message.getMessage(online, MsgEnum.MINUTE)) : (remainingSecs > 1 ? remainingSecs + " " + Message.getMessage(online, MsgEnum.SECONDS) : remainingSecs + " " + Message.getMessage(online, MsgEnum.SECOND)))));
			if (remainingMins == 0 && remainingSecs <= 10) {
				if (remainingSecs < 4) {
					switch (remainingSecs) {
						case 3 :
							Sounds.playSoundAll(null, Sounds.ORB_PICKUP, 1f, 0f);
							break;
						case 2 :
							Sounds.playSoundAll(null, Sounds.ORB_PICKUP, 1f, 1f);
							break;
						case 1 :
							Sounds.playSoundAll(null, Sounds.ORB_PICKUP, 1f, 2f);
							break;
					}
					for (Player online : Bukkit.getOnlinePlayers())
						UltimateSheepWarsPlugin.getVersionManager().getTitleUtils().titlePacket(online, 2, 25, 4, ChatColor.YELLOW + "" + remainingSecs, Message.getMessage(online, ChatColor.AQUA.toString(), MsgEnum.PRE_START_SUBTITLE, ""));
				} else {
					Sounds.playSoundAll(null, Sounds.NOTE_STICKS, 1f, 1.5f);
				}
			}
		}
		--this.timeUntilStart;
	}

	public void shortenCountdown() {
		forceStarting();
		if (this.timeUntilStart > 10)
			this.timeUntilStart = 10;
	}

	public void start() {
		started = true;
	}

	public void forceStarting() {
		forced = true;
	}

	public boolean hasStarted() {
		return started;
	}

	public boolean wasForced() {
		return forced;
	}
}
