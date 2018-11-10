package fr.asynchronous.sheepwars.core.calendar.event;

import java.util.Calendar;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import fr.asynchronous.sheepwars.core.calendar.CalendarEvent;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import net.md_5.bungee.api.ChatColor;

public class HalloweenDaysEvent extends CalendarEvent {

	public HalloweenDaysEvent() {
		super(3, "Halloween", Type.TIME_PERIOD);
	}

	@Override
	public Calendar getEndDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.NOVEMBER);
		cal.set(Calendar.DAY_OF_MONTH, 3);
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 0);
		return cal;
	}

	@Override
	public Calendar getStartDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.OCTOBER);
		cal.set(Calendar.DAY_OF_MONTH, 25);
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 0);
		return cal;
	}

	@Override
	public void activate(Plugin activatingPlugin) {
		ConfigManager.setItemStack(Field.KIT_ITEM, new ItemStack(Material.PUMPKIN));
	}

	@Override
	public void deactivate(Plugin deactivatingPlugin) {
		// Do nothing
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Happy Halloween " + ChatColor.YELLOW + ChatColor.BOLD + player.getName() + ChatColor.GOLD + ChatColor.BOLD + " !");
		Sounds.playSound(player, null, Sounds.AMBIENCE_CAVE, 1.0f, 0.0f);
	}
}
