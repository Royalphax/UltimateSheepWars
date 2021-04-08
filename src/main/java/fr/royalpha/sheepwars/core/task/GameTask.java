package fr.royalpha.sheepwars.core.task;

import fr.royalpha.sheepwars.api.GameState;
import fr.royalpha.sheepwars.api.Language;
import fr.royalpha.sheepwars.api.SheepWarsSheep;
import fr.royalpha.sheepwars.api.SheepWarsTeam;
import fr.royalpha.sheepwars.api.event.GameStartEvent;
import fr.royalpha.sheepwars.core.manager.ConfigManager;
import fr.royalpha.sheepwars.core.manager.ExceptionManager;
import fr.royalpha.sheepwars.core.message.Message;
import fr.royalpha.sheepwars.core.sheep.BoardingSheep;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.api.PlayerData;

public class GameTask extends BukkitRunnable {
	public final SheepWarsPlugin plugin;
	public final int gameTime;
	public final int boosterInterval;
	public final int boosterLifeTime;
	public final int boardingTime;
	
	private int remainingDurationInSecs;
	private int boosterCountdown;
	private int giveSheepCountdown;

	public GameTask(final SheepWarsPlugin plugin) {
		this.plugin = plugin;
		this.gameTime = ConfigManager.getInt(ConfigManager.Field.GAME_TIME);
		this.boosterInterval = ConfigManager.getInt(ConfigManager.Field.BOOSTER_INTERVAL);
		this.boosterLifeTime = ConfigManager.getInt(ConfigManager.Field.BOOSTER_LIFE_TIME);
		this.boardingTime = ConfigManager.getInt(ConfigManager.Field.BOARDING_TIME);
		this.remainingDurationInSecs = (this.gameTime * 60);
		this.boosterCountdown = this.boosterInterval;
		this.giveSheepCountdown = 0;
		plugin.setGameTask(this);
		GameStartEvent event = new GameStartEvent(plugin);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCloudNetSupportEnable()) {
			try {
				de.dytanic.cloudnet.bridge.CloudServer.getInstance().setServerState(de.dytanic.cloudnet.lib.server.ServerState.INGAME);
				de.dytanic.cloudnet.bridge.CloudServer.getInstance().changeToIngame();
				de.dytanic.cloudnet.bridge.CloudServer.getInstance().update();
			} catch (NoClassDefFoundError ex) {
				// Do nothing
			}
		}
		new BoosterWoolTask(this).runTaskTimer(this.plugin, 0, 20);
		this.runTaskTimer(plugin, 0, 20);
	}

	public void run() {
		try {
			for (Player online : Bukkit.getOnlinePlayers()) {
				final PlayerData data = PlayerData.getPlayerData(online);
				if (data.getTeam() != SheepWarsTeam.SPEC) {
					data.increaseTotalTime(1);
					SheepWarsPlugin.getVersionManager().getTitleUtils().actionBarPacket(online, data.getLanguage().getMessage(Message.Messages.ACTION_KILLS_STATS).replace("%KILLS%", data.getActualKills() + ""));
				}
			}
			/** Fin du jeu ou arret brutal (se traduit par changement d'etape du jeu) **/
			if (remainingDurationInSecs <= 0 || !GameState.isStep(GameState.INGAME)) {
				this.cancel();
				if (GameState.isStep(GameState.INGAME)) {
					for (Player online : Bukkit.getOnlinePlayers()) {
						final Language lang = PlayerData.getPlayerData(online).getLanguage();
						SheepWarsPlugin.getVersionManager().getTitleUtils().titlePacket(online, 5, 10 * 20, 20, lang.getMessage(Message.Messages.FINISH_EQUALITY), lang.getMessage(Message.Messages.GAME_END_EQUALITY_DESCRIPTION));
					}
					this.plugin.stopGame(null);
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
					lang.getScoreboardWrapper().setTitle(lang.getMessage(Message.Messages.SCOREBOARD_INGAME_TITLE).replaceAll("%MINUTES%", remainingMinsDisplay).replaceAll("%SECONDS%", remainingSecsDisplay));
				} catch (IllegalArgumentException ex) {
					ExceptionManager.register(ex, true);
				}
				lang.refreshSheepCountdown(giveSheepCountdown);
				lang.refreshBoosterCountdown(boosterCountdown);
			}
			if (remainingDurationInSecs == (this.boardingTime * 60)) {
				Message.broadcastTitle(Message.Messages.BOARDING_TITLE, Message.Messages.BOARDING_SUBTITLE);
				new GiveSheepTask(new BoardingSheep()).runTaskTimer(this.plugin, 0, (20*60));
				
			}
			--remainingDurationInSecs;
		} catch (Exception ex) {
			ExceptionManager.register(ex, true);
		}
		giveSheepCountdown--;
		if (giveSheepCountdown <= 0) {
			for (Player player : Bukkit.getOnlinePlayers())
				SheepWarsSheep.giveRandomSheep(player);
			giveSheepCountdown = ConfigManager.getInt(ConfigManager.Field.GIVE_SHEEP_INTERVAL);
		}
	}
	
	public void setBoosterCountdown(int i) {
		this.boosterCountdown = i;
	}
}
