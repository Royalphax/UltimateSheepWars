package fr.asynchronous.sheepwars.core.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.gui.GuiContributor;
import fr.asynchronous.sheepwars.core.gui.manager.GuiManager;
import fr.asynchronous.sheepwars.core.handler.Contributor;

public class ContributorCommand implements CommandExecutor {

	public UltimateSheepWarsPlugin plugin;

	public ContributorCommand(UltimateSheepWarsPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Please connect on the server, then do this command again.");
            return true;
        }
        final Player player = (Player)sender;
        if (Contributor.isContributor(player))
    		GuiManager.openGui(this.plugin, new GuiContributor(this.plugin, player));
		return false;
	}
}
