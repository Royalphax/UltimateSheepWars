package fr.asynchronous.sheepwars.core.version;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public interface IEventHelper {

	public Boolean onEntityTargetEvent(final Entity entity, final Entity target);
	
	public void onAsyncPlayerChat(final String prefix, final String suffix, final Player player, final AsyncPlayerChatEvent event, final String hover, final Boolean spec);
}
