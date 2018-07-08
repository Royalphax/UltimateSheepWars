package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.message.Language;

public class PlayerLogin extends UltimateSheepWarsEventListener {
	public PlayerLogin(final UltimateSheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerLogin(final PlayerLoginEvent event) {
		final Player player = event.getPlayer();
		final PlayerData data = PlayerData.getPlayerData(player);
		
		/** On regarde si on le kick ou pas **/
		if (GameState.isStep(GameState.WAITING)) {
			if (event.getResult() == PlayerLoginEvent.Result.KICK_FULL && player.hasPermission("sheepwars.vip")) {
				event.allow();
			}
		} else {
			if (ConfigManager.getBoolean(Field.ENABLE_JOIN_FOR_SPECTATORS)) {
				event.allow();
			} else {
				event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
				event.setKickMessage(GameState.getMOTD());
			}
		}
		
		/** On actualise son langage **/
		new BukkitRunnable()
		{
			public void run()
			{
				String locale = event.getPlayer().spigot().getLocale();
				if (ConfigManager.getBoolean(Field.AUTO_GENERATE_LANGUAGES))
					data.setLanguage(Language.getLanguage(locale));
				player.sendMessage(ChatColor.GRAY + data.getLanguage().getIntro());
			}
		}.runTaskLater(this.plugin, (20 * 5));
	}
}
