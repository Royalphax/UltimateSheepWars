package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;

public class PlayerCommandPreprocess extends UltimateSheepWarsEventListener {
	public PlayerCommandPreprocess(final UltimateSheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onCommandPreprocess(final PlayerCommandPreprocessEvent event) {
		final Player player = event.getPlayer();
		if (player.isOp() && event.getMessage().split(" ")[0].contains("reload")
				|| event.getMessage().split(" ")[0].contains("rl")) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "This command is forbidden. You may use /restart.");
		}
		if (!player.isOp() && event.getMessage().split(" ")[0].contains("pl")
				|| event.getMessage().split(" ")[0].contains("plugins")
				|| event.getMessage().split(" ")[0].contains("?") || event.getMessage().split(" ")[0].contains("help")
				|| event.getMessage().split(" ")[0].contains("bukkit")
				|| event.getMessage().split(" ")[0].contains("ver")
				|| event.getMessage().split(" ")[0].contains("version")
				|| event.getMessage().split(" ")[0].contains("bukkit:")
				|| event.getMessage().split(" ")[0].contains("about")
				|| event.getMessage().split(" ")[0].contains("icanhasbukkit")) {
			event.setCancelled(true);
		}
	}
}
