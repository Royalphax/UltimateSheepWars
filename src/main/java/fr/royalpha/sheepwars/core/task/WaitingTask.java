package fr.royalpha.sheepwars.core.task;

import java.io.IOException;

import fr.royalpha.sheepwars.api.GameState;
import fr.royalpha.sheepwars.api.SheepWarsTeam;
import fr.royalpha.sheepwars.core.handler.PlayableMap;
import fr.royalpha.sheepwars.core.handler.Sounds;
import fr.royalpha.sheepwars.core.manager.ConfigManager;
import fr.royalpha.sheepwars.core.manager.ExceptionManager;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.api.PlayerData;

public class WaitingTask extends BukkitRunnable {
	private boolean started = false;
	private boolean forced = false;
	private int timeUntilStart;
	private final SheepWarsPlugin plugin;

	public WaitingTask(final SheepWarsPlugin plugin) {
		this.plugin = plugin;
		this.timeUntilStart = ConfigManager.getInt(ConfigManager.Field.COUNTDOWN);
		plugin.setWaitingTask(this);
		start();
		this.runTaskTimer(plugin, 0L, 20L);
	}

	public void run() {
		if (this.timeUntilStart == 0) {
			this.cancel();
			if (Bukkit.getOnlinePlayers().size() >= (forced ? 2 : ConfigManager.getInt(ConfigManager.Field.MIN_PLAYERS))) {
				GameState.setCurrentStep(GameState.INGAME);
				/** On check les teams **/
				boolean shakeup = SheepWarsTeam.checkTeams();
				for (Player player : Bukkit.getOnlinePlayers()) {
					/** On reeassigne le joueur dans une team si besoin **/
					final PlayerData data = PlayerData.getPlayerData(player);
					if (!data.hasTeam() && !shakeup)
						data.setTeam(SheepWarsTeam.getRandomTeam());
					/** On get sa team finale et on le TP en fonction **/
					final SheepWarsTeam team = data.getTeam();
					player.setFallDistance(0.0f);
					final PlayerInventory inv = player.getInventory();
					for (ItemStack item : inv.getContents()) {
						if (item != null && item.getType() != ConfigManager.getItemStack(ConfigManager.Field.KIT_ITEM).getType()) {
							inv.remove(item);
						}
					}
					player.teleport(team.getNextSpawn());
					data.disableMovements(true);
					SheepWarsPlugin.getVersionManager().getTitleUtils().titlePacket(player, 2, 80, 20, data.getLanguage().getMessage(Message.Messages.GAME_PRE_START_TITLE), data.getLanguage().getMessage(Message.Messages.GAME_PRE_START_SUBTITLE));
				}
				new PreGameTask(this.plugin);
			} else {
				Message.broadcast(Message.Messages.PLAYERS_DEFICIT);
				this.timeUntilStart = ConfigManager.getInt(ConfigManager.Field.COUNTDOWN);
				started = false;
				if (SheepWarsPlugin.getWorldManager().isVoteModeEnable()) {
					/** En th??orie le monde a ??t?? load juste avant **/
					SheepWarsPlugin.getWorldManager().unloadVotedMap();
				}
			}
			return;
		}
		int remainingMins = this.timeUntilStart / 60 % 60;
		int remainingSecs = this.timeUntilStart % 60;
		float progress = (float) this.timeUntilStart / (float) ConfigManager.getInt(ConfigManager.Field.COUNTDOWN);
		for (Player online : Bukkit.getOnlinePlayers())
			online.setExp(progress);
		if (this.timeUntilStart == 10 && SheepWarsPlugin.getWorldManager().isVoteModeEnable()) {
			final PlayableMap map = SheepWarsPlugin.getWorldManager().getVoteResult();
			Message.broadcast(Message.Messages.VOTE_END, "%MAP_NAME%", map.getDisplayName());
			try {
				map.loadWorld();
			} catch (IOException e) {
				ExceptionManager.register(e, true);
			}
		}
		if (this.timeUntilStart % 30 == 0 || (remainingMins == 0 && (remainingSecs % 10 == 0 || remainingSecs < 10))) {
			for (Player online : Bukkit.getOnlinePlayers()) {
				SheepWarsPlugin.getVersionManager().getTitleUtils().actionBarPacket(online, Message.getMessage(online, Message.Messages.STARTING_GAME).replaceAll("%TIME%", (remainingMins > 0 ? (remainingMins > 1 ? remainingMins + " " + Message.getMessage(online, Message.Messages.MINUTES) : remainingMins + " " + Message.getMessage(online, Message.Messages.MINUTE)) : (remainingSecs > 1 ? remainingSecs + " " + Message.getMessage(online, Message.Messages.SECONDS) : remainingSecs + " " + Message.getMessage(online, Message.Messages.SECOND)))));
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
