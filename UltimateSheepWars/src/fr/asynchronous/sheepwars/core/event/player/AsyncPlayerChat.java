package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.util.Utils;

public class AsyncPlayerChat extends UltimateSheepWarsEventListener {
	public AsyncPlayerChat(final UltimateSheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
		if (event.isCancelled())
			return;
		event.setCancelled(true);
		final Player player = event.getPlayer();
		final TeamManager playerTeam = TeamManager.getPlayerTeam(player);
		final PlayerData data = PlayerData.getPlayerData(player);
		String prefix = "";
		String suffix = "";
		if (this.plugin.CHAT_PROVIDER_INSTALLED && this.plugin.CHAT_PROVIDER != null) {
			// String groupPrefix = this.plugin.CHAT_PROVIDER.getGroupPrefix(player.getWorld(), this.plugin.PERMISSION_PROVIDER.getPrimaryGroup(player));
			// String groupSuffix = this.plugin.CHAT_PROVIDER.getGroupSuffix(player.getWorld(), this.plugin.PERMISSION_PROVIDER.getPrimaryGroup(player));
			String playerPrefix = this.plugin.CHAT_PROVIDER.getPlayerPrefix(player.getWorld().getName(), player);
			String playerSuffix = this.plugin.CHAT_PROVIDER.getPlayerSuffix(player.getWorld().getName(), player);
			prefix = ChatColor.translateAlternateColorCodes('&', /* groupPrefix + */playerPrefix);
			suffix = ChatColor.translateAlternateColorCodes('&', playerSuffix/* + groupSuffix */);
		}
		if (GameState.isStep(GameState.IN_GAME) && playerTeam == TeamManager.SPEC) {
			for (Player online : Bukkit.getOnlinePlayers()) {
				if (TeamManager.getPlayerTeam(online) == TeamManager.SPEC) {
					String locale = PlayerData.getPlayerData(online).getLocale();
					String hover = playerTeam.getColor() + Language.getMessageByLanguage(locale, Message.RECORDS).replaceAll("%PLAYER%", playerTeam.getColor() + "" + ChatColor.BOLD + player.getName()) + "\n\n" + ChatColor.GRAY + Language.getMessageByLanguage(locale, Message.DATABASE_NOT_CONNECTED);
					if (this.plugin.MySQL_ENABLE)
						hover = Utils.assignArrayToString(Utils.getPlayerRatio(online, data, false, this.plugin));
					UltimateSheepWarsPlugin.getVersionManager().getEventHelper().onAsyncPlayerChat(prefix, suffix, online, event, hover, true);
				}
			}
		} else {
			for (Player online : Bukkit.getOnlinePlayers()) {
				String locale = PlayerData.getPlayerData(online).getLocale();
				String hover = playerTeam.getColor() + Language.getMessageByLanguage(locale, Message.RECORDS).replaceAll("%PLAYER%", playerTeam.getColor() + "" + ChatColor.BOLD + player.getName()) + "\n\n" + ChatColor.GRAY + Language.getMessageByLanguage(locale, Message.DATABASE_NOT_CONNECTED);
				if (this.plugin.MySQL_ENABLE)
					hover = Utils.assignArrayToString(Utils.getPlayerRatio(online, data, false, this.plugin));
				UltimateSheepWarsPlugin.getVersionManager().getEventHelper().onAsyncPlayerChat(prefix, suffix, online, event, hover, false);
			}
		}
	}
}
