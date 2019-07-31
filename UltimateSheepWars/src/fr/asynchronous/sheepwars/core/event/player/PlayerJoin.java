package fr.asynchronous.sheepwars.core.event.player;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.command.SubCommand;
import fr.asynchronous.sheepwars.core.data.DataManager;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.Contributor;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.Particles.ParticleEffect;
import fr.asynchronous.sheepwars.core.handler.Permissions;
import fr.asynchronous.sheepwars.core.handler.SheepWarsTeam;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.URLManager;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.Messages;
import fr.asynchronous.sheepwars.core.task.WaitingTask;
import fr.asynchronous.sheepwars.core.util.EntityUtils;
import fr.asynchronous.sheepwars.core.version.ATitleUtils.Type;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class PlayerJoin extends UltimateSheepWarsEventListener {
	public PlayerJoin(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	void sendTitle(Player player, String title, String subtitle) {
		SheepWarsPlugin.getVersionManager().getTitleUtils().defaultTitle(Type.TITLE, player, title, subtitle);
	}
	
	void sendAction(Player player, String message) {
		SheepWarsPlugin.getVersionManager().getTitleUtils().actionBarPacket(player, message);
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		
		/** Si plugin non configure **/
		if (!plugin.isConfigured()) {
			event.setJoinMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "! " + ChatColor.RED + "Plugin UltimateSheepWars isn't fully configured yet. Setup it with " + ChatColor.UNDERLINE + "/sw help" + ChatColor.RED + " or contact an Administrator.");
			return;
		}
		
		if ((Permissions.USW_DEVELOPER.hasPermission(player)) && !URLManager.isUpToDate()) {
			List<String> news = URLManager.getInfoVersion();
			if (!news.isEmpty()) {
				player.sendMessage(SubCommand.PREFIX + ChatColor.RED + "A new version of the plugin is available. " + ChatColor.UNDERLINE + "Changelog :");
				for (int i = 0; i < news.size(); i++) {
					String newsLine = news.get(i);
					player.sendMessage(SubCommand.PREFIX + ChatColor.GREEN + (newsLine.startsWith("#") ? "✚ " : "") + ChatColor.RESET + (newsLine.startsWith("#") ? ChatColor.YELLOW + "" + ChatColor.BOLD + newsLine.replaceFirst("#", "") : ChatColor.GRAY + "  " + newsLine));
				}
			}
			BaseComponent[] compo = new ComponentBuilder(SubCommand.PREFIX).append("Please stay updated : ").color(net.md_5.bungee.api.ChatColor.RED).append("(click)").color(net.md_5.bungee.api.ChatColor.GREEN).event(new ClickEvent(Action.OPEN_URL, "https://www.spigotmc.org/resources/17393/")).create();
			player.spigot().sendMessage(compo);
		}
		
		/** Sinon, c'est parti! **/
		event.setJoinMessage((String) null);
		final PlayerData data = PlayerData.getPlayerData(player);
		
		/** Nos merveilleux contributeurs **/
		if (Contributor.isContributor(player)) {
			Contributor contributor = Contributor.getContributor(player);
			player.sendMessage(ChatColor.GRAY + contributor.getSpecialMessage());
			if (contributor.getEffect() != null)
				ParticleEffect.equipEffect(player, this.plugin);
		}
		
		/** On fonctionne par gamestate **/
		if (!GameState.isStep(GameState.WAITING)) {
			/** On le met en spec **/
			this.plugin.setSpectator(player, false);
			EntityUtils.resetPlayer(player, GameMode.SPECTATOR);
			
			/** On le tp + fly **/ 
			final Location spawn = SheepWarsTeam.SPEC.getNextSpawn();
			player.teleport((spawn == null) ? ConfigManager.getLocation(Field.LOBBY).toBukkitLocation() : spawn);
			player.setFlying(true);
			
			new BukkitRunnable() {
				public void run() {
					SheepWarsPlugin.getVersionManager().getTitleUtils().actionBarPacket(player, data.getLanguage().getMessage(Message.getMessage(Messages.USER_DATA_LOADING)));
					if (data.isLoaded()) {
						cancel();
						if (DataManager.isConnected()) {
							sendAction(player, data.getLanguage().getMessage(Messages.USER_DATA_LOADED));
						} else {
							sendAction(player, data.getLanguage().getMessage(Messages.DATABASE_NOT_CONNECTED));
						}
						sendTitle(player, data.getLanguage().getMessage(Messages.JOIN_TITLE).replace("%ONLINE_PLAYERS%", Bukkit.getOnlinePlayers().size() + "").replace("%MAX_PLAYERS%", Bukkit.getMaxPlayers() + ""), data.getLanguage().getMessage(Messages.GHOST_MESSAGE));
						player.setScoreboard(data.getLanguage().getScoreboardWrapper().getScoreboard());
					}
				}
			}.runTaskTimer(plugin, 0, 0);
			
		} else if (GameState.isStep(GameState.WAITING)) {
			/** On le reset puis teleporte **/
			EntityUtils.resetPlayer(player, GameMode.ADVENTURE);
			player.teleport(ConfigManager.getLocation(Field.LOBBY).toBukkitLocation());
			
			/** On s'assure que tout le monde le voit, on affiche le message de bienvenue **/
			for (Player online : Bukkit.getOnlinePlayers()) {
				online.showPlayer(player);
				online.sendMessage(ChatColor.YELLOW + data.getLanguage().getMessage(Messages.PLAYER_JOIN_MESSAGE).replaceAll("%PLAYER%", Contributor.getPrefix(player) + player.getName()) + ChatColor.GRAY + " (" + ChatColor.YELLOW + Bukkit.getOnlinePlayers().size() + ChatColor.GRAY + "/" + ChatColor.YELLOW + Bukkit.getMaxPlayers() + ChatColor.GRAY + ")");
			}
			
			/** S'il y a assez de joueurs, on lance le countdown **/
			if ((Bukkit.getOnlinePlayers().size() >= ConfigManager.getInt(Field.MIN_PLAYERS)) && (!this.plugin.hasWaitingTaskStarted())) {
				new WaitingTask(this.plugin);
			}
			
			new BukkitRunnable() {
				public void run() {
					sendAction(player, data.getLanguage().getMessage(Messages.USER_DATA_LOADING));
					if (data.isLoaded()) {
						cancel();
						if (DataManager.isConnected()) {
							sendAction(player, data.getLanguage().getMessage(Messages.USER_DATA_LOADED));
						} else {
							sendAction(player, data.getLanguage().getMessage(Messages.DATABASE_NOT_CONNECTED));
						}
						String locale = event.getPlayer().spigot().getLocale();
						if (ConfigManager.getBoolean(Field.AUTO_GENERATE_LANGUAGES)) {
							data.setLanguage(Language.getLanguage(locale));
						} else {
							player.sendMessage(ChatColor.GRAY + data.getLanguage().getIntro());
						}
						if (plugin.isConfigured())
							data.getLanguage().equipPlayer(player);
						sendTitle(player, data.getLanguage().getMessage(Messages.JOIN_TITLE).replace("%ONLINE_PLAYERS%", Bukkit.getOnlinePlayers().size() + "").replace("%MAX_PLAYERS%", Bukkit.getMaxPlayers() + ""), data.getLanguage().getMessage(Messages.JOIN_SUBTITLE));
					}
				}
			}.runTaskTimer(plugin, 0, 0);
		}
	}
}
