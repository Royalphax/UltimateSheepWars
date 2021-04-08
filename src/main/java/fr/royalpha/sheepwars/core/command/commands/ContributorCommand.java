package fr.royalpha.sheepwars.core.command.commands;

import fr.royalpha.sheepwars.core.handler.Contributor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.gui.GuiManager;
import fr.royalpha.sheepwars.core.gui.guis.ContributorsInventory;

public class ContributorCommand implements CommandExecutor {

	public SheepWarsPlugin plugin;

	public ContributorCommand(SheepWarsPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command is not allowed from console.");
            return true;
        }
        final Player player = (Player)sender;
        if (Contributor.isContributor(player))
    		GuiManager.openGui(this.plugin, player, "Contributor's GUI", new ContributorsInventory());
		return false;
	}
}
