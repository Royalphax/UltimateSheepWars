package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.DataManager;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.Contributor;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.kit.NoneKit;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.task.BeginCountdown;
import fr.asynchronous.sheepwars.core.util.EntityUtils;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.util.Utils;
import fr.asynchronous.sheepwars.core.version.ATitleUtils.Type;

public class PlayerJoin extends UltimateSheepWarsEventListener {
	public PlayerJoin(final UltimateSheepWarsPlugin plugin) {
		super(plugin);
	}

	void sendTitle(Player player, String title, String subtitle) {
		UltimateSheepWarsPlugin.getVersionManager().getTitleUtils().defaultTitle(Type.TITLE, player, title, subtitle);
	}
	
	void sendAction(Player player, String message) {
		UltimateSheepWarsPlugin.getVersionManager().getTitleUtils().actionBarPacket(player, message);
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		
		/** Si plugin non configure **/
		if (!Utils.isPluginConfigured()) {
			event.setJoinMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "! " + ChatColor.RED + "Plugin UltimateSheepWars isn't fully configured yet. Setup it with " + ChatColor.UNDERLINE + "/sw help" + ChatColor.RED + " or contact an Administrator.");
			return;
		}
		
		/** Sinon, c'est parti! **/
		event.setJoinMessage((String) null);
		final PlayerData data = PlayerData.getPlayerData(player);
		
		/** Nos merveilleux contributeurs **/
		if (Contributor.isContributor(player)) {
			Contributor contributor = Contributor.getContributor(player);
			if (contributor.getLevel() > 2) {
				player.sendMessage(ChatColor.UNDERLINE + "Server Informations :");
				player.sendMessage("");
				player.sendMessage(" - Server Name : " + Bukkit.getServerName());
				player.sendMessage(" - Server OS Name : " + System.getProperty("os.name"));
				player.sendMessage(" - Server OS Version : " + System.getProperty("os.version"));
				player.sendMessage(" - Server OS Arch : " + System.getProperty("os.arch"));
				player.sendMessage("");
			}
			player.sendMessage(ChatColor.GRAY + contributor.getSpecialMessage());
			if (contributor.getEffect() != null)
				Contributor.ParticleEffect.equipEffect(player, this.plugin);
		}
		
		/** On fonctionne par gamestate **/
		if (!GameState.isStep(GameState.WAITING)) {
			/** On le met en spec **/
			this.plugin.getGameTask().setSpectator(player, false);
			EntityUtils.resetPlayer(player, GameMode.SPECTATOR);
			
			/** On le tp + fly **/ 
			final Location spawn = TeamManager.SPEC.getNextSpawn();
			player.teleport((spawn == null) ? ConfigManager.getLocation(Field.LOBBY) : spawn);
			player.setFlying(true);
			
			new BukkitRunnable() {
				public void run() {
					UltimateSheepWarsPlugin.getVersionManager().getTitleUtils().actionBarPacket(player, data.getLanguage().getMessage(Message.getMessage(MsgEnum.USER_DATA_LOADING)));
					if (data.isLoaded()) {
						cancel();
						if (DataManager.isConnected()) {
							sendAction(player, data.getLanguage().getMessage(MsgEnum.USER_DATA_LOADED));
						} else {
							sendAction(player, data.getLanguage().getMessage(MsgEnum.DATABASE_NOT_CONNECTED));
						}
						sendTitle(player, data.getLanguage().getMessage(MsgEnum.JOIN_TITLE).replace("%ONLINE_PLAYERS%", Bukkit.getOnlinePlayers().size() + "").replace("%MAX_PLAYERS%", Bukkit.getMaxPlayers() + ""), data.getLanguage().getMessage(MsgEnum.JOIN_SUBTITLE));
						player.setScoreboard(data.getLanguage().getScoreboardWrapper().getScoreboard());
					}
				}
			}.runTaskTimer(plugin, 0, 0);
			
		} else if (GameState.isStep(GameState.WAITING)) {
			/** On le reset puis teleporte **/
			EntityUtils.resetPlayer(player, GameMode.ADVENTURE);
			player.teleport(ConfigManager.getLocation(Field.LOBBY));
			
			/** On s'assure que tout le monde le voit, on affiche le message de bienvenue **/
			for (Player online : Bukkit.getOnlinePlayers()) {
				online.showPlayer(player);
				online.sendMessage(ChatColor.YELLOW + data.getLanguage().getMessage(MsgEnum.PLAYER_JOIN_MESSAGE).replaceAll("%PLAYER%", Contributor.getPrefix(player) + player.getName()) + ChatColor.GRAY + " (" + ChatColor.YELLOW + Bukkit.getOnlinePlayers().size() + ChatColor.GRAY + "/" + ChatColor.YELLOW + Bukkit.getMaxPlayers() + ChatColor.GRAY + ")");
			}
			
			/** S'il y a assez de joueurs, on lance le countdown **/
			if ((Bukkit.getOnlinePlayers().size() >= ConfigManager.getInt(Field.MIN_PLAYERS)) && (!this.plugin.hasPreGameTaskStarted()) && (!ConfigManager.getLocations(Field.BOOSTERS).isEmpty())) {
				new BeginCountdown(this.plugin);
			}
			
			new BukkitRunnable() {
				public void run() {
					sendAction(player, data.getLanguage().getMessage(MsgEnum.USER_DATA_LOADING));
					if (data.isLoaded()) {
						cancel();
						if (DataManager.isConnected()) {
							sendAction(player, data.getLanguage().getMessage(MsgEnum.USER_DATA_LOADED));
							if (data.getKit().getId() != new NoneKit().getId())
								player.sendMessage(data.getLanguage().getMessage(MsgEnum.KIT_LAST_SELECTED).replace("%KIT%", data.getKit().getName(player)));
						} else {
							sendAction(player, data.getLanguage().getMessage(MsgEnum.DATABASE_NOT_CONNECTED));
						}
						equip(data);
						sendTitle(player, data.getLanguage().getMessage(MsgEnum.JOIN_TITLE).replace("%ONLINE_PLAYERS%", Bukkit.getOnlinePlayers().size() + "").replace("%MAX_PLAYERS%", Bukkit.getMaxPlayers() + ""), data.getLanguage().getMessage(MsgEnum.JOIN_SUBTITLE));
					}
				}
			}.runTaskTimer(plugin, 0, 0);
		}
	}

	public static void equip(PlayerData data) {
		if (!Utils.isPluginConfigured())
			return;
		Player player = data.getPlayer();
		PlayerInventory inv = player.getInventory();
		inv.clear();
		for (TeamManager team : TeamManager.values())
			if (team.getSpawns() != null && !team.getSpawns().isEmpty() && team != TeamManager.SPEC)
				inv.addItem(team.getIcon(player));
		inv.setItem(7, new ItemBuilder(ConfigManager.getItemStack(Field.KIT_ITEM)).setName(data.getLanguage().getMessage(MsgEnum.KITS_ITEM)).toItemStack());
		inv.setItem(8, new ItemBuilder(ConfigManager.getItemStack(Field.RETURN_TO_HUB_ITEM)).setName(data.getLanguage().getMessage(MsgEnum.LEAVE_ITEM)).toItemStack());
		if (data.getAllowedParticles()) {
			inv.setItem(4, new ItemBuilder(ConfigManager.getItemStack(Field.PARTICLES_ON_ITEM)).setName(data.getLanguage().getMessage(MsgEnum.PARTICLES_ON)).toItemStack());
		} else {
			inv.setItem(4, new ItemBuilder(ConfigManager.getItemStack(Field.PARTICLES_ON_ITEM)).setName(data.getLanguage().getMessage(MsgEnum.PARTICLES_OFF)).toItemStack());
		}
		player.setScoreboard(data.getLanguage().getScoreboardWrapper().getScoreboard());

		/**if (plugin.MySQL_ENABLE) {
			if (!player.hasMetadata("stats_top"))
				player.setMetadata("stats_top", new FixedMetadataValue(plugin, 0));
			inv.setItem(13, Utils.getItemStats(null, player, data, plugin));
			PlayerData.DATA_TYPE actual = PlayerData.DATA_TYPE.getFromId((int) player.getMetadata("stats_top").get(0).value());
			PlayerData.DATA_TYPE before = PlayerData.DATA_TYPE.before(actual);
			PlayerData.DATA_TYPE after = PlayerData.DATA_TYPE.after(actual);
			inv.setItem(21, new ItemBuilder(Material.ARROW).setName(Language.getMessageByLanguage(data.getLocale(), Message.RANKING_GOTO_LEFT).replace("%RANKING%", Language.getMessageByLanguage(data.getLocale(), before.message))).toItemStack());
			inv.setItem(22, Utils.getItemStats(actual, player, data, plugin));
			inv.setItem(23, new ItemBuilder(Material.ARROW).setName(Language.getMessageByLanguage(data.getLocale(), Message.RANKING_GOTO_RIGHT).replace("%RANKING%", Language.getMessageByLanguage(data.getLocale(), after.message))).toItemStack());
		} else {
			String bar = ChatColor.YELLOW + "----------------------------";
			inv.setItem(22, new ItemBuilder(Material.PAINTING).setName(ChatColor.GOLD + "Stats : " + Language.getMessageByLanguage(data.getLocale(), Message.SCOREBOARD_TITLE)).setLore(bar, Language.getMessageByLanguage(PlayerData.getPlayerData(plugin, player).getLocale(), Message.DATABASE_NOT_CONNECTED), bar).toItemStack());
		}**/
	}
}
