package fr.asynchronous.sheepwars.v1_9_R2;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.version.IEventHelper;
import fr.asynchronous.sheepwars.v1_9_R2.entity.CustomSheep;
import fr.asynchronous.sheepwars.v1_9_R2.util.SpecialMessage;
import net.minecraft.server.v1_9_R2.ChatHoverable.EnumHoverAction;

public class EventHelper implements IEventHelper {

	@Override
	public Boolean onEntityTargerEvent(Entity entity, Entity target) {
		net.minecraft.server.v1_9_R2.Entity entityHandler = ((CraftEntity) entity).getHandle();
		if ((entityHandler instanceof CustomSheep)) {
			CustomSheep sheep = (CustomSheep) entityHandler;
			TeamManager team = TeamManager.getPlayerTeam((Player) target);
			if (((sheep.getColor().ordinal() != DyeColor.LIME.ordinal()) || (team == TeamManager.SPEC)
					|| (team == TeamManager.getPlayerTeam(sheep.getPlayer())))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onAsyncPlayerChat(String prefix, String suffix, Player online, AsyncPlayerChatEvent event, String hover, Boolean spec) {
		SpecialMessage msg = new SpecialMessage("");
		Player player = event.getPlayer();
		TeamManager playerTeam = TeamManager.getPlayerTeam(player);
		if (spec)
		{
			msg.setHover(prefix + ChatColor.WHITE.toString()+ChatColor.BOLD+player.getName() + suffix, EnumHoverAction.SHOW_TEXT, hover);
	    	msg.append(": " + event.getMessage());
			msg.sendToPlayer(online);
		} else {
    		msg.setHover(prefix + ((playerTeam != null) ? playerTeam.getColor() : ChatColor.GRAY) + "" + player.getName() + suffix, EnumHoverAction.SHOW_TEXT, hover);
        	msg.append(": " + event.getMessage());
    		msg.sendToPlayer(online);
		}
	}
}
