package fr.asynchronous.sheepwars.core.command.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.command.SubCommand;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Permissions;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.kit.USWKit;
import fr.asynchronous.sheepwars.core.kit.USWKit.KitLevel;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.util.Utils;

public class ShowKitsSubCommand extends SubCommand {
	
	public ShowKitsSubCommand(UltimateSheepWarsPlugin plugin) {
		super("Display loaded kits", "This command allows developers to see which kit has been loaded. You can display more informations on the kit by adding its ID at the end of the command.", "/usw kits <id>", Permissions.USW_DEVELOPER, plugin, "kits");
	}
	
	@Override
	protected void onExePlayer(Player player, String... args) {
		int i = -1;
		if (args.length > 1 && Utils.isInteger(args[1]))
			i = Integer.parseInt(args[1]);
		if (i >= 0 && USWKit.getAvailableKits().size() > 0) {
			USWKit kit = USWKit.getAvailableKits().get(0);
			for (USWKit k : USWKit.getAvailableKits())
				if (k.getId() == i)
					kit = k;
			player.sendMessage("∙ " + ChatColor.GRAY + kit.getName(player) + ChatColor.GRAY + " (Id " + ChatColor.YELLOW + kit.getId() + ChatColor.GRAY + ")");
			player.sendMessage("  ∙ " + ChatColor.GRAY + "Icon : " + ChatColor.YELLOW + kit.getIcon().toItemStack().getType().toString().replaceAll("_", " "));
			final Language lang = PlayerData.getPlayerData(player).getLanguage();
			for (KitLevel level : kit.getLevels()) {
				player.sendMessage("  ∙ " + ChatColor.GOLD + "Level " + ChatColor.YELLOW + level.getId());
				player.sendMessage("    ∙ " + ChatColor.GRAY + "Required wins : " + ChatColor.YELLOW + level.getRequiredWins());
				player.sendMessage("    ∙ " + ChatColor.GRAY + "Permission : " + ChatColor.YELLOW + level.getPermission());
				player.sendMessage("    ∙ " + ChatColor.GRAY + "Price : " + ChatColor.YELLOW + level.getPrice());
				player.sendMessage("    ∙ " + ChatColor.GRAY + "Name : " + ChatColor.YELLOW + level.getName(lang));
				player.sendMessage("    ∙ " + ChatColor.GRAY + "Description : " + ChatColor.YELLOW + level.getDescription(lang).replaceAll("\n", "⏎"));
			}
		} else {
			player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Loaded Kits :");
			for (USWKit kit : USWKit.getAvailableKits()) {
				player.sendMessage("∙ " + ChatColor.GRAY + kit.getName(player) + ChatColor.GRAY + " (Id " + ChatColor.YELLOW + kit.getId() + ChatColor.GRAY + ")");
			}
		}
		Sounds.playSound(player, player.getLocation(), Sounds.ORB_PICKUP, 1f, 1f);

	}
	
	@Override
	protected void onExeConsole(ConsoleCommandSender sender, String... args) {
		notAllowed(sender);
	}
}
