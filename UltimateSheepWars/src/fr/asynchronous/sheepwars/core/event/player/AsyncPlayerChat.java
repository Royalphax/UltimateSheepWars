package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.DataManager;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.DisplayStyle;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.Utils;

public class AsyncPlayerChat extends UltimateSheepWarsEventListener {
	public AsyncPlayerChat(final UltimateSheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority=EventPriority.LOW)
	public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
		if (event.isCancelled())
			return;
		
		event.setCancelled(true);
		final Player player = event.getPlayer();
		final TeamManager playerTeam = PlayerData.getPlayerData(player).getTeam();
		final PlayerData data = PlayerData.getPlayerData(player);
		
		/** Chat prefix **/
		String prefix = "";
		String suffix = "";
		if (this.plugin.isChatProviderInstalled()) {
			String playerPrefix = this.plugin.getChatProvider().getPlayerPrefix(player.getWorld().getName(), player);
			String playerSuffix = this.plugin.getChatProvider().getPlayerSuffix(player.getWorld().getName(), player);
			prefix = ChatColor.translateAlternateColorCodes('&', playerPrefix);
			suffix = ChatColor.translateAlternateColorCodes('&', playerSuffix);
		}
		
		if (GameState.isStep(GameState.INGAME) && playerTeam == TeamManager.SPEC) {
			for (Player online : Bukkit.getOnlinePlayers()) {
				final PlayerData onlineData = PlayerData.getPlayerData(online);
				if (onlineData.getTeam() == TeamManager.SPEC) {
					String hover = playerTeam.getColor() + onlineData.getLanguage().getMessage(MsgEnum.RECORDS).replaceAll("%PLAYER%", playerTeam.getColor() + "" + ChatColor.BOLD + player.getName()) + "\n\n" + ChatColor.GRAY + onlineData.getLanguage().getMessage(MsgEnum.DATABASE_NOT_CONNECTED);
					if (DataManager.isConnected())
						hover = Utils.assignArrayToString(Utils.getPlayerStats(data, onlineData.getLanguage(), DisplayStyle.HOVER));
					UltimateSheepWarsPlugin.getVersionManager().getEventHelper().onAsyncPlayerChat(prefix, suffix, online, event, hover, true);
				}
			}
		} else {
			for (Player online : Bukkit.getOnlinePlayers()) {
				final PlayerData onlineData = PlayerData.getPlayerData(online);
				String hover = playerTeam.getColor() + onlineData.getLanguage().getMessage(MsgEnum.RECORDS).replaceAll("%PLAYER%", playerTeam.getColor() + "" + ChatColor.BOLD + player.getName()) + "\n\n" + ChatColor.GRAY + onlineData.getLanguage().getMessage(MsgEnum.DATABASE_NOT_CONNECTED);
				if (DataManager.isConnected())
					hover = Utils.assignArrayToString(Utils.getPlayerStats(data, onlineData.getLanguage(), DisplayStyle.HOVER));
				UltimateSheepWarsPlugin.getVersionManager().getEventHelper().onAsyncPlayerChat(prefix, suffix, online, event, hover, false);
			}
		}
	}
}
