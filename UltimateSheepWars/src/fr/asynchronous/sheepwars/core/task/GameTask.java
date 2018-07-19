package fr.asynchronous.sheepwars.core.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.DataManager;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.manager.RewardsManager.Events;
import fr.asynchronous.sheepwars.core.manager.SheepManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.sheep.BoardingSheep;
import fr.asynchronous.sheepwars.core.util.EntityUtils;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public class GameTask extends BukkitRunnable {
	public final UltimateSheepWarsPlugin plugin;
	public final int gameTime;
	public final int boosterInterval;
	public final int boosterLifeTime;
	public final int boardingTime;
	
	private int remainingDurationInSecs;
	private int boosterCountdown;
	private int giveSheepCountdown;

	public GameTask(final UltimateSheepWarsPlugin plugin) {
		this.plugin = plugin;
		plugin.setGameTask(this);
		this.gameTime = ConfigManager.getInt(Field.GAME_TIME);
		this.boosterInterval = ConfigManager.getInt(Field.BOOSTER_INTERVAL);
		this.boosterLifeTime = ConfigManager.getInt(Field.BOOSTER_LIFE_TIME);
		this.boardingTime = ConfigManager.getInt(Field.BOARDING_TIME);
		this.remainingDurationInSecs = (this.gameTime * 60);
		this.boosterCountdown = this.boosterInterval;
		this.giveSheepCountdown = 0;
		new BoosterWoolTask(this).runTaskTimer(this.plugin, 0, 20);
		this.runTaskTimer(plugin, 0, 20);
	}

	public void run() {
		try {
			for (Player online : Bukkit.getOnlinePlayers()) {
				final PlayerData data = PlayerData.getPlayerData(online);
				if (data.getTeam() != TeamManager.SPEC) {
					data.increaseTotalTime(1);
					UltimateSheepWarsPlugin.getVersionManager().getTitleUtils().actionBarPacket(online, data.getLanguage().getMessage(MsgEnum.ACTION_KILLS_STATS).replace("%KILLS%", data.getActualKills() + ""));
				}
			}
			/** Fin du jeu ou arret brutal (se traduit par changement d'etape du jeu) **/
			if (remainingDurationInSecs <= 0 || !GameState.isStep(GameState.INGAME)) {
				this.cancel();
				if (GameState.isStep(GameState.INGAME)) {
					for (Player online : Bukkit.getOnlinePlayers()) {
						final Language lang = PlayerData.getPlayerData(online).getLanguage();
						UltimateSheepWarsPlugin.getVersionManager().getTitleUtils().titlePacket(online, 5, 10 * 20, 20, lang.getMessage(MsgEnum.FINISH_EQUALITY), lang.getMessage(MsgEnum.GAME_END_EQUALITY_DESCRIPTION));
					}
					this.stopGame(null);
					return;
				}
			}
			/** Si c'est pas la fin du jeu, on fait les trucs habituels **/
			final int remainingMins = remainingDurationInSecs / 60 % 60;
			final int remainingSecs = remainingDurationInSecs % 60;
			final String remainingMinsDisplay = ((remainingMins < 10) ? "0" : "") + remainingMins;
			final String remainingSecsDisplay = ((remainingSecs < 10) ? "0" : "") + remainingSecs;
			for (Language lang : Language.getLanguages()) {
				try {
					lang.getScoreboardWrapper().setTitle(ChatColor.DARK_GRAY + "- " + ChatColor.YELLOW + lang.getMessage(MsgEnum.GAME_DISPLAY_NAME) + " " + ChatColor.GREEN + remainingMinsDisplay + ":" + remainingSecsDisplay + ChatColor.DARK_GRAY + " -");
				} catch (IllegalArgumentException ex) {
					new ExceptionManager(ex).register(true);
				}
				lang.refreshSheepCountdown(giveSheepCountdown);
				lang.refreshBoosterCountdown(boosterCountdown);
			}
			if (remainingDurationInSecs == (this.boardingTime * 60)) {
				Message.broadcastTitle(MsgEnum.BOARDING_TITLE, MsgEnum.BOARDING_SUBTITLE);
				new GiveSheepTask(new BoardingSheep()).runTaskTimer(this.plugin, 0, (20*60));
				
			}
			--remainingDurationInSecs;
		} catch (Exception ex) {
			new ExceptionManager(ex).register(true);
		}
		giveSheepCountdown--;
		if (giveSheepCountdown <= 0) {
			for (Player player : Bukkit.getOnlinePlayers())
				SheepManager.giveRandomSheep(player);
			giveSheepCountdown = ConfigManager.getInt(Field.GIVE_SHEEP_INTERVAL);
		}
	}
	
	public void setBoosterCountdown(int i) {
		this.boosterCountdown = i;
	}

	public void setSpectator(final Player player, final boolean lose) {
		final PlayerData data = PlayerData.getPlayerData(player);
		if (!data.isSpectator()) {
			if (lose) {
				this.removePlayer(player);
				data.increaseDeaths(1);
			}
			data.setTeam(TeamManager.SPEC);
		}
		EntityUtils.resetPlayer(player, GameMode.SPECTATOR);
	}

	public void removePlayer(final Player player) {
		final PlayerData data = PlayerData.getPlayerData(player);
		final TeamManager team = data.getTeam();
		if (!data.isSpectator()) {
			data.setTeam(TeamManager.NULL);
			if (GameState.isStep(GameState.WAITING)) {
				PlayerData.getPlayers().remove(player);
			} else if (GameState.isStep(GameState.INGAME) && team.getOnlinePlayers().isEmpty()) {
				TeamManager winnerTeam = team == TeamManager.BLUE ? TeamManager.RED : TeamManager.BLUE;
				stopGame(winnerTeam);
				new BukkitRunnable() {
					private int ticks = 30;
					public void run() {
						if (this.ticks == 0) {
							this.cancel();
							return;
						}
						Location location = ConfigManager.getRdmLocationFromList(Field.BOOSTERS);
						ArrayList<Player> onlines = new ArrayList<>();
						for (Player online : Bukkit.getOnlinePlayers())
							onlines.add(online);
						Random rdm = new Random();
						FireworkEffect effect = FireworkEffect.builder().flicker(rdm.nextBoolean()).withColor(RandomUtils.getRandomColor()).withFade(RandomUtils.getRandomColor()).with(Type.BALL_LARGE).build();
						UltimateSheepWarsPlugin.getVersionManager().getCustomEntities().spawnInstantExplodingFirework(location, effect, onlines);
						this.ticks--;
					}
				}.runTaskTimer(plugin, 0, 10);
			}
		}
	}

	public void stopGame(final TeamManager winnerTeam) {
		new TerminatedGameTask(this.plugin);
		for (Entry<OfflinePlayer, PlayerData> entry : PlayerData.getData()) {
			final OfflinePlayer offPlayer = entry.getKey();
			final PlayerData data = entry.getValue();
			if (winnerTeam != null && winnerTeam != TeamManager.SPEC && offPlayer.isOnline()) {
				Player player = (Player) offPlayer;
				if (data.getTeam() == winnerTeam) {
					data.increaseWins(1);
					this.plugin.getRewardsManager().rewardPlayer(Events.ON_WIN, data.getPlayer());
				} else {
					this.plugin.getRewardsManager().rewardPlayer(Events.ON_LOOSE, data.getPlayer());
				}
				player.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + data.getLanguage().getMessage(MsgEnum.VICTORY).replaceAll("%WINNER%", winnerTeam.getColor() + winnerTeam.getDisplayName(player)) + " " + Message.getDecoration() + ChatColor.AQUA + " " + ChatColor.BOLD + data.getLanguage().getMessage(MsgEnum.CONGRATULATIONS) + " " + Message.getDecoration());
				UltimateSheepWarsPlugin.getVersionManager().getTitleUtils().titlePacket(player, 5, 10 * 20, 20, ChatColor.YELLOW + "" + data.getLanguage().getMessage(MsgEnum.GAME_END_TITLE), Message.getDecoration() + "" + ChatColor.GOLD + " " + ChatColor.BOLD + data.getLanguage().getMessage(MsgEnum.VICTORY).replaceAll("%WINNER%", winnerTeam.getColor() + winnerTeam.getDisplayName(player)) + " " + Message.getDecoration());
			}
			if (DataManager.isConnected())
				data.uploadData(offPlayer);
		}
	}
	
	public boolean isBooster(Location loc) {
		List<Location> boostersLoc = ConfigManager.getLocations(Field.BOOSTERS);
		List<Location> boostersBlock = new ArrayList<>();
		for (Location location : boostersLoc)
			boostersBlock.add(location.getBlock().getLocation());
		return (boostersBlock.contains(loc.getBlock().getLocation()));
	}
}
