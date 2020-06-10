package fr.asynchronous.sheepwars.core.event.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.DataManager;
import fr.asynchronous.sheepwars.api.PlayerData;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.DisplayStyle;
import fr.asynchronous.sheepwars.api.GameState;
import fr.asynchronous.sheepwars.api.SheepWarsTeam;
import fr.asynchronous.sheepwars.core.message.Message.Messages;
import fr.asynchronous.sheepwars.core.util.Utils;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class AsyncPlayerChat extends UltimateSheepWarsEventListener {
	public AsyncPlayerChat(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority=EventPriority.LOW)
	public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
		if (event.isCancelled())
			return;
		
		event.setCancelled(true);
		final Player player = event.getPlayer();
		final SheepWarsTeam playerTeam = PlayerData.getPlayerData(player).getTeam();
		final PlayerData data = PlayerData.getPlayerData(player);
		TextComponent message = new TextComponent(event.getMessage());
		
		/** Chat prefix **/
		TextComponent prefix = new TextComponent("");
		TextComponent suffix = new TextComponent("");
		if (this.plugin.isChatProviderInstalled()) {
			String playerPrefix = SheepWarsPlugin.getChatProvider().getPlayerPrefix(player.getWorld().getName(), player);
			String playerSuffix = SheepWarsPlugin.getChatProvider().getPlayerSuffix(player.getWorld().getName(), player);
			prefix = new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', playerPrefix)));
			suffix = new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', playerSuffix)));
		}
		
		boolean global = true;
		List<Player> audience = new ArrayList<>(Bukkit.getOnlinePlayers());
		if (GameState.isStep(GameState.INGAME)) {
			if (data.isSpectator()) {
				audience = SheepWarsTeam.SPEC.getOnlinePlayers();
			} else {
				if (!event.getMessage().startsWith("!")) {
					audience = playerTeam.getOnlinePlayers();
					global = false;
				} else {
					message = new TextComponent(event.getMessage().replaceFirst("!", ""));
				}
			}
		}
		
		/*TextComponent name = new TextComponent(player.getName());
		name.setColor(playerTeam.getBungeeColor());
		String hover = playerTeam.getColor() + onlineData.getLanguage().getMessage(MsgEnum.RECORDS).replaceAll("%PLAYER%", playerTeam.getColor() + "" + ChatColor.BOLD + player.getName()) + "\n\n" + ChatColor.GRAY + onlineData.getLanguage().getMessage(MsgEnum.DATABASE_NOT_CONNECTED);
		if (DataManager.isConnected()) {
			hover = Utils.assignArrayToString(Utils.getPlayerStats(data, onlineData.getLanguage(), DisplayStyle.HOVER));
		}*/
		
		
		for (Player listener : audience) {
			final PlayerData listenerData = PlayerData.getPlayerData(listener);
			TextComponent name = new TextComponent(player.getName());
			name.setColor(playerTeam.getBungeeColor());
			String hover = playerTeam.getColor() + listenerData.getLanguage().getMessage(Messages.RECORDS).replaceAll("%PLAYER%", playerTeam.getColor() + "" + ChatColor.BOLD + player.getName()) + "\n\n" + ChatColor.GRAY + listenerData.getLanguage().getMessage(Messages.DATABASE_NOT_CONNECTED);
			if (DataManager.isConnected())
				hover = Utils.assignArrayToString(Utils.getPlayerStats(data, listenerData.getLanguage(), DisplayStyle.HOVER));
			name.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText(hover)));
			
			TextComponent finalMessage = new TextComponent("");
			if (!global)
				finalMessage = getComponent(ChatColor.GRAY + "(" + playerTeam.getDisplayName(listener) + ChatColor.GRAY + ") ");
			finalMessage.addExtra(prefix);
			finalMessage.addExtra(name);
			finalMessage.addExtra(suffix);
			finalMessage.addExtra(getComponent(ChatColor.WHITE + ": "));
			finalMessage.addExtra(message);
			listener.spigot().sendMessage(finalMessage);
		}
		
		
		/*if (GameState.isStep(GameState.INGAME) && data.isSpectator()) {
			for (Player online : Bukkit.getOnlinePlayers()) {
				final PlayerData onlineData = PlayerData.getPlayerData(online);
				if (onlineData.getTeam() == TeamManager.SPEC) {
					String hover = playerTeam.getColor() + onlineData.getLanguage().getMessage(MsgEnum.RECORDS).replaceAll("%PLAYER%", playerTeam.getColor() + "" + ChatColor.BOLD + player.getName()) + "\n\n" + ChatColor.GRAY + onlineData.getLanguage().getMessage(MsgEnum.DATABASE_NOT_CONNECTED);
					if (DataManager.isConnected())
						hover = Utils.assignArrayToString(Utils.getPlayerStats(data, onlineData.getLanguage(), DisplayStyle.HOVER));
					TextComponent name = new TextComponent(player.getName());
					name.setColor(playerTeam.getBungeeColor());
					name.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText(hover)));
				}
			}
		} else {
			for (Player online : Bukkit.getOnlinePlayers()) {
				final PlayerData onlineData = PlayerData.getPlayerData(online);
				String hover = playerTeam.getColor() + onlineData.getLanguage().getMessage(MsgEnum.RECORDS).replaceAll("%PLAYER%", playerTeam.getColor() + "" + ChatColor.BOLD + player.getName()) + "\n\n" + ChatColor.GRAY + onlineData.getLanguage().getMessage(MsgEnum.DATABASE_NOT_CONNECTED);
				if (DataManager.isConnected())
					hover = Utils.assignArrayToString(Utils.getPlayerStats(data, onlineData.getLanguage(), DisplayStyle.HOVER));
				
			}
		}*/
	}
	
	private static TextComponent getComponent(String legacyText) {
		return new TextComponent(TextComponent.fromLegacyText(legacyText));
	}
}
