package fr.asynchronous.sheepwars.core.task;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.PlayableMap;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class WaitingTask extends BukkitRunnable {
	private boolean started = false;
	private boolean forced = false;
	private int timeUntilStart;
	private final SheepWarsPlugin plugin;

	public WaitingTask(final SheepWarsPlugin plugin) {
		this.plugin = plugin;
		this.timeUntilStart = ConfigManager.getInt(Field.COUNTDOWN);
		plugin.setWaitingTask(this);
		start();
		this.runTaskTimer(plugin, 0L, 20L);
	}

	public void run() {
		if (this.timeUntilStart == 0) {
			this.cancel();
			if (Bukkit.getOnlinePlayers().size() >= (forced ? 2 : ConfigManager.getInt(Field.MIN_PLAYERS))) {
				GameState.setCurrentStep(GameState.INGAME);
				/** On check les teams **/
				boolean shakeup = TeamManager.checkTeams();
				for (Player player : Bukkit.getOnlinePlayers()) {
					/** On reeassigne le joueur dans une team si besoin **/
					final PlayerData data = PlayerData.getPlayerData(player);
					if (!data.hasTeam() && !shakeup)
						data.setTeam(TeamManager.getRandomTeam());
					/** On get sa team finale et on le TP en fonction **/
					final TeamManager team = data.getTeam();
					player.setFallDistance(0.0f);
					player.teleport(team.getNextSpawn());
					final PlayerInventory inv = player.getInventory();
					for (ItemStack item : inv.getContents()) {
						if (item != null && item.getType() != ConfigManager.getItemStack(Field.KIT_ITEM).getType()) {
							inv.remove(item);
						}
					}
					SheepWarsPlugin.getVersionManager().getNMSUtils().cancelMove(player, true);
					SheepWarsPlugin.getVersionManager().getTitleUtils().actionBarPacket(player, "");
					SheepWarsPlugin.getVersionManager().getTitleUtils().titlePacket(player, 2, 70, 20, data.getLanguage().getMessage(MsgEnum.GAME_PRE_START_TITLE), data.getLanguage().getMessage(MsgEnum.GAME_PRE_START_SUBTITLE));
				}
				new PreGameTask(this.plugin);
			} else {
				Message.broadcast(MsgEnum.PLAYERS_DEFICIT);
				this.timeUntilStart = ConfigManager.getInt(Field.COUNTDOWN);
				started = false;
				if (SheepWarsPlugin.getWorldManager().isVoteModeEnable()) {
					/** En théorie le monde a été load juste avant **/
					Bukkit.unloadWorld(SheepWarsPlugin.getWorldManager().getVotedMap().getWorld(), false);
				}
			}
			return;
		}
		int remainingMins = this.timeUntilStart / 60 % 60;
		int remainingSecs = this.timeUntilStart % 60;
		float progress = (float) this.timeUntilStart / (float) ConfigManager.getInt(Field.COUNTDOWN);
		for (Player online : Bukkit.getOnlinePlayers())
			online.setExp(progress);
		if (this.timeUntilStart == 10 && SheepWarsPlugin.getWorldManager().isVoteModeEnable()) {
			PlayableMap map = SheepWarsPlugin.getWorldManager().getVoteResult();
			Message.broadcast(MsgEnum.VOTE_END, "%MAP_NAME%", map.getName());
			try {
				map.loadWorld();
			} catch (IOException e) {
				new ExceptionManager(e).register(true);
			}
		}
		if (this.timeUntilStart % 30 == 0 || (remainingMins == 0 && (remainingSecs % 10 == 0 || remainingSecs < 10))) {
			for (Player online : Bukkit.getOnlinePlayers()) {
				SheepWarsPlugin.getVersionManager().getTitleUtils().actionBarPacket(online, Message.getMessage(online, MsgEnum.STARTING_GAME).replaceAll("%TIME%", (remainingMins > 0 ? (remainingMins > 1 ? remainingMins + " " + Message.getMessage(online, MsgEnum.MINUTES) : remainingMins + " " + Message.getMessage(online, MsgEnum.MINUTE)) : (remainingSecs > 1 ? remainingSecs + " " + Message.getMessage(online, MsgEnum.SECONDS) : remainingSecs + " " + Message.getMessage(online, MsgEnum.SECOND)))));
				Sounds.playSound(online, Sounds.NOTE_STICKS, 1f, 1.5f);
			}
		}
		--this.timeUntilStart;
	}

	public void shortenCountdown() {
		forceStarting();
		if (this.timeUntilStart > 10)
			this.timeUntilStart = 10;
	}
	
	public int getRemainingSeconds() {
		return this.timeUntilStart;
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
