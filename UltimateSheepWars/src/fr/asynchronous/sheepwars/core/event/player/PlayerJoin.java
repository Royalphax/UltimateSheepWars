package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.Contributor;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.Kit;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.DataManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Language;
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

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		if (!Utils.isPluginConfigured()) {
			event.setJoinMessage(ChatColor.YELLOW + "âš  " + ChatColor.RED + "Ultimate Sheep Wars isn't fully configured yet! Please contact an Administrator or setup it with /sw help.");
			return;
		}
		event.setJoinMessage((String) null);
		final PlayerData data = PlayerData.getPlayerData(player);
		if (Contributor.isContributor(player)) {
			Contributor contributor = Contributor.getContributor(player);
			if (contributor.getLevel() > 2) {
				player.sendMessage("§6-----------------< §aServer §6>-------------------");
				player.sendMessage("§bServer: §a" + Bukkit.getServerName() + " §e- §bOS name: §a" + System.getProperty("os.name"));
				player.sendMessage("§bOS version: §a" + System.getProperty("os.version") + " §e- §bOS arch: §a" + System.getProperty("os.arch"));
				player.sendMessage("§6---------------------------------------------");
			}
			player.sendMessage(ChatColor.GRAY + contributor.getSpecialMessage());
			if (contributor.getEffect() != null)
				Contributor.ParticleEffect.equipEffect(player, this.plugin);
		}
		if (!GameState.isStep(GameState.LOBBY)) {
			this.plugin.getGameTask().setSpectator(player, false);
			EntityUtils.resetPlayer(player, GameMode.SPECTATOR);
			final Location spawn = TeamManager.SPEC.getNextSpawn();
			player.teleport((spawn == null) ? ConfigManager.getLocation(Field.LOBBY) : spawn);
			player.setFlying(true);
			new BukkitRunnable() {
				public void run() {
					UltimateSheepWarsPlugin.getVersionManager().getTitleUtils().actionBarPacket(player, data.getLanguage().getMessage(Message.getMessage(MsgEnum.USER_DATA_LOADING)));
					if (!data.getName().equals("Loading")) {
						cancel();
						if (DataManager.isConnected()) {
							Message.sendAction(plugin, player, "", Message.USER_DATA_LOADED, "");
						} else {
							Message.sendAction(plugin, player, "", Message.DATABASE_NOT_CONNECTED, "");
						}
						sendTitle(player, Language.getMessageByLanguage(data.getLocale(), Message.JOIN_TITLE).replace("%ONLINE_PLAYERS%", Bukkit.getOnlinePlayers().size() + "").replace("%MAX_PLAYERS%", Bukkit.getMaxPlayers() + ""), Language.getMessageByLanguage(data.getLocale(), Message.GHOST_MESSAGE));
						player.sendMessage(ChatColor.GRAY + Language.getLanguage(data.getLocale()).getIntro());
						player.setScoreboard(Language.getLanguage(data.getLocale()).getScoreboardWrapper().getScoreboard());
						GameState.updateTabInfo(player, "", plugin);
					}
				}
			}.runTaskTimer(plugin, 0, 0);
		} else if (GameState.isStep(GameState.LOBBY)) {
			player.teleport(plugin.LOBBY_LOCATION);
			EntityUtils.resetPlayer(player, GameMode.ADVENTURE, this.plugin);
			for (Player online : Bukkit.getOnlinePlayers()) {
				online.showPlayer(player);
				online.sendMessage(ChatColor.YELLOW + Language.getMessageByLanguage(PlayerData.getPlayerData(plugin, online).getLocale(), Message.PLAYER_JOIN_MESSAGE).replaceAll("%PLAYER%", Contributor.getPrefix(player) + player.getName()) + ChatColor.GRAY + " (Â§e" + Bukkit.getOnlinePlayers().size() + "Â§7/Â§e" + Bukkit.getMaxPlayers() + "Â§7)");
			}
			if ((Bukkit.getOnlinePlayers().size() >= this.plugin.MIN_PLAYERS) && (!BeginCountdown.started) && (!this.plugin.BOOSTER_LOCATIONS.isEmpty())) {
				new BeginCountdown(this.plugin);
			}
			new BukkitRunnable() {
				public void run() {
					plugin.versionManager.getTitleUtils().actionBarPacket(player, Language.getMessageByLanguage(data.getLocale(), Message.USER_DATA_LOADING));
					if (data.getName() != "Loading") {
						cancel();
						if (plugin.MySQL_ENABLE) {
							plugin.versionManager.getTitleUtils().actionBarPacket(player, Language.getMessageByLanguage(data.getLocale(), Message.USER_DATA_LOADED));
							if (data.getLastKit() != 0) {
								Kit lastKit = Kit.getFromId(data.getLastKit());
								if ((plugin.KIT_ENABLE_WINS && data.getWins() >= lastKit.getWins()) || (plugin.KIT_ENABLE_PERMISSIONS && lastKit.havePermission(player)) || plugin.KIT_ENABLE_ALL || (Contributor.isImportant(player))) {
									Kit.setPlayerKit(player, lastKit);
									player.sendMessage(plugin.PREFIX + Message.getMessage(player, "", Message.KIT_LAST_SELECTED, "").replace("%KIT%", lastKit.getName(player)));
								}
							}
						} else {
							plugin.versionManager.getTitleUtils().actionBarPacket(player, Language.getMessageByLanguage(data.getLocale(), Message.DATABASE_NOT_CONNECTED));
						}
						if (Kit.getPlayerKit(player) == null) {
							Kit.setPlayerKit(player, Kit.NULL);
							player.sendMessage(plugin.PREFIX + Message.getMessage(player, "", Message.KIT_LAST_SELECTED, "").replace("%KIT%", Kit.NULL.getName(player)));
						}
						player.sendMessage(ChatColor.GRAY + Language.getLanguage(data.getLocale()).getIntro());
						equip(plugin, data);
						sendTitle(player, Language.getMessageByLanguage(data.getLocale(), Message.JOIN_TITLE).replace("%ONLINE_PLAYERS%", Bukkit.getOnlinePlayers().size() + "").replace("%MAX_PLAYERS%", Bukkit.getMaxPlayers() + ""), Language.getMessageByLanguage(data.getLocale(), Message.JOIN_SUBTITLE));
						GameState.updateTabInfo(player, "", plugin);
					}
				}
			}.runTaskTimer(plugin, 0, 0);
		}
	}

	@SuppressWarnings("deprecation")
	public static void equip(UltimateSheepWarsPlugin plugin, PlayerData data) {
		if (!Utils.isPluginConfigured(plugin))
			return;
		Player player = data.getPlayer();
		PlayerInventory inv = player.getInventory();
		inv.clear();
		for (TeamManager team : TeamManager.values())
			if (team.getSpawns() != null && !team.getSpawns().isEmpty() && team != TeamManager.SPEC)
				inv.addItem(team.getIcon(player));
		inv.setItem(7, new ItemBuilder(Material.getMaterial(plugin.ITEM_KIT)).setName(Message.getMessage(player, "", Message.KITS_ITEM, "")).addIllegallyGlow(true).toItemStack());
		inv.setItem(8, new ItemBuilder(Material.getMaterial(plugin.ITEM_RETURN)).setName(Message.getMessage(player, ChatColor.GOLD + "", Message.LEAVE_ITEM, "")).toItemStack());
		if (data.getAllowedParticles()) {
			inv.setItem(4, new ItemBuilder(Material.getMaterial(plugin.ITEM_PARTICLES_ON)).setName(Message.getMessage(player, ChatColor.GOLD + "", Message.PARTICLES_ON, "")).toItemStack());
		} else {
			inv.setItem(4, new ItemBuilder(Material.getMaterial(plugin.ITEM_PARTICLES_OFF)).setName(Message.getMessage(player, ChatColor.GOLD + "", Message.PARTICLES_OFF, "")).toItemStack());
		}
		player.setScoreboard(Language.getLanguage(data.getLocale()).getScoreboardWrapper().getScoreboard());

		if (plugin.MySQL_ENABLE) {
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
		}
	}
}
