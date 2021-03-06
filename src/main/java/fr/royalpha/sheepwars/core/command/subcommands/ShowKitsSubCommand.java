package fr.royalpha.sheepwars.core.command.subcommands;

import fr.royalpha.sheepwars.api.Language;
import fr.royalpha.sheepwars.core.handler.Permissions;
import fr.royalpha.sheepwars.core.handler.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.command.SubCommand;
import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.api.SheepWarsKit;
import fr.royalpha.sheepwars.api.SheepWarsKit.SheepWarsKitLevel;
import fr.royalpha.sheepwars.core.util.Utils;

public class ShowKitsSubCommand extends SubCommand {
	
	public ShowKitsSubCommand(SheepWarsPlugin plugin) {
		super("Display loaded kits", "This command allows developers to see which kit has been loaded. You can display more informations on the kit by adding its ID at the end of the command.", "/usw kits <id>", Permissions.USW_DEVELOPER, plugin, "kits");
	}
	
	@Override
	protected void onExePlayer(Player player, String... args) {
		int i = -1;
		if (args.length > 1 && Utils.isInteger(args[1]))
			i = Integer.parseInt(args[1]);
		if (i >= 0 && SheepWarsKit.getAvailableKits().size() > 0) {
			SheepWarsKit kit = SheepWarsKit.getAvailableKits().get(0);
			for (SheepWarsKit k : SheepWarsKit.getAvailableKits())
				if (k.getId() == i)
					kit = k;
			player.sendMessage("∙ " + ChatColor.GRAY + kit.getName(player) + ChatColor.GRAY + " (Id " + ChatColor.YELLOW + kit.getId() + ChatColor.GRAY + ")");
			player.sendMessage("  ∙ " + ChatColor.GRAY + "Icon : " + ChatColor.YELLOW + kit.getIcon().toItemStack().getType().toString().replaceAll("_", " "));
			final Language lang = PlayerData.getPlayerData(player).getLanguage();
			for (SheepWarsKitLevel level : kit.getLevels()) {
				player.sendMessage("  ∙ " + ChatColor.GOLD + "Level " + ChatColor.YELLOW + level.getLevelId());
				player.sendMessage("    ∙ " + ChatColor.GRAY + "Required wins : " + ChatColor.YELLOW + level.getRequiredWins());
				player.sendMessage("    ∙ " + ChatColor.GRAY + "Permission : " + ChatColor.YELLOW + level.getPermission());
				player.sendMessage("    ∙ " + ChatColor.GRAY + "Price : " + ChatColor.YELLOW + level.getPrice());
				player.sendMessage("    ∙ " + ChatColor.GRAY + "Name : " + ChatColor.YELLOW + level.getName(lang));
				player.sendMessage("    ∙ " + ChatColor.GRAY + "Description : " + ChatColor.YELLOW + level.getDescription(lang).replaceAll("\n", "⏎"));
			}
		} else {
			player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Loaded Kits :");
			for (SheepWarsKit kit : SheepWarsKit.getAvailableKits()) {
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
