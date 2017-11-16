package fr.asynchronous.sheepwars.core.version;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.util.Utils;

public abstract class ATitleUtils {

	public abstract void titlePacket(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle);

	public abstract void tabPacket(Player player, String footer, String header);
	
	public abstract void actionBarPacket(Player player, String message);
	
	public void defaultTitle(Type type, Player player, Object... obj) {
		try {
			switch (type)
			{
			case ACTION:
				actionBarPacket(player, (String) obj[0]);
				break;  
			case TAB:
				tabPacket(player, (String) obj[0], (String) obj[1]);
				break;
			case TITLE:
				titlePacket(player, 10, 40, 10, (String) obj[0], (String) obj[1]);
				break;
			}
		} catch (Exception ex) {
			new ExceptionManager(ex).register(true);
			return;
		}
	}

	public void broadcastDefaultTitle(Type type, Object... obj) {
		for (Player online : Bukkit.getOnlinePlayers())
			defaultTitle(type, online, obj);
	}
	
	public static enum Type {
		TITLE(0),
		TAB(1),
		ACTION(2);
		
		private int id;
		private Type(int id)
		{
			this.id = id;
		}
		
		public int getId() {
			return this.id;
		}
	}
}
