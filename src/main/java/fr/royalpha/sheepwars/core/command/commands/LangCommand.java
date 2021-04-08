package fr.royalpha.sheepwars.core.command.commands;

import fr.royalpha.sheepwars.api.Language;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.api.PlayerData;

public class LangCommand implements CommandExecutor {

	public SheepWarsPlugin plugin;
	
	public LangCommand(SheepWarsPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command is not allowed from console.");
            return true;
        }
        if (!plugin.isConfigured()) {
    		sender.sendMessage(ChatColor.RED + "You can't use this command until the plugin isn't fully configured.");
    		return true;
    	}
    	final Player player = (Player)sender;
    	final PlayerData data = PlayerData.getPlayerData(player);
    	if (args.length != 0) {
            final String sub = args[0];
            if (Language.getLanguage(sub) != null)
            {
            	data.setLanguage(Language.getLanguage(sub));
            } else {
            	player.sendMessage(ChatColor.RED + "This language doesn't exist.");
            }
    	} else {
    		Language.listAvailableLanguages(player);
    		return true;
        }
		return false;
	}
}
