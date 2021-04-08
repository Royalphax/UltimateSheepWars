package fr.royalpha.sheepwars.core.command.subcommands;

import java.util.ArrayList;
import java.util.List;

import fr.royalpha.sheepwars.api.GameState;
import fr.royalpha.sheepwars.core.handler.Permissions;
import fr.royalpha.sheepwars.core.handler.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.command.SubCommand;
import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.api.SheepWarsSheep;

public class GiveSheepSubCommand extends SubCommand {

	public GiveSheepSubCommand(SheepWarsPlugin plugin) {
		super("Give extra sheeps", "This command allows you to give some extra sheeps to a player. The first argument allows you to target one player (just put his name) or all the players (put '*'). Then, the second argument allows you to select which sheep you will give (use the sheep IDs '/usw sheeps'). Finally, the last argument allows you to specify how many sheeps will be given (use ',' to specify different amounts for each sheep).", "/usw give <player/*> <Id,...> <amount,...>", makeList(Permissions.USW_GIVE_ALL, Permissions.USW_GIVE_OTHER, Permissions.USW_GIVE_SELF, Permissions.USW_GIVE_X), plugin, "give", "g");
	}

	@Override
	protected void onExePlayer(Player player, String... args) {
		if (GameState.isStep(GameState.INGAME)) {
			List<SheepWarsSheep> sheeps = new ArrayList<>(SheepWarsSheep.getAvailableSheeps());
			List<Integer> amounts = new ArrayList<>();
			List<Player> players = new ArrayList<>();
			players.add(player);

			if (args.length > 1) { // A été spécifié le/les joueurs.

				if (args[1].equalsIgnoreCase("*")) {
					if (!Permissions.USW_GIVE_ALL.hasPermission(player, true))
						return;
					players.clear();
					for (Player online : player.getWorld().getPlayers())
						if (!PlayerData.getPlayerData(online).isSpectator() && !players.contains(online))
							players.add(online);
				} else if (Bukkit.getPlayer(args[1]) != null) {
					if (!Permissions.USW_GIVE_OTHER.hasPermission(player, true))
						return;
					players.clear();
					players.add(Bukkit.getPlayer(args[1]));
				} else {
					if (!Permissions.USW_GIVE_OTHER.hasPermission(player, true))
						return;
					player.sendMessage(ChatColor.RED + args[1] + " is not connected.");
					return;
				}

				if (args.length > 2) { // A été spécifié les moutons.
					sheeps.clear();

					List<Integer> sheepsId = new ArrayList<>();
					for (String str : args[2].split(",")) {
						try {
							sheepsId.add(Integer.parseInt(str));
						} catch (NumberFormatException ex) {
							continue;
						}
					}

					List<SheepWarsSheep> availableSheeps = SheepWarsSheep.getAvailableSheeps();
					for (int i : sheepsId)
						sheeps.add(availableSheeps.get(i));

					if (args.length > 3) { // A été spécifié les amounts.
						for (String str : args[3].split(",")) {
							try {
								amounts.add(Integer.parseInt(str));
							} catch (NumberFormatException ex) {
								continue;
							}
						}
					}
				}
			}

			if (players.size() == 1 && players.contains(player) && !Permissions.USW_GIVE_SELF.hasPermission(player, true))
				return;

			if (sheeps.isEmpty()) {
				player.sendMessage(ChatColor.RED + "No sheep has been given (check the syntax of your command).");
				return;
			}

			for (Player people : players) {
				int amount = 64;
				for (int i = 0; i < sheeps.size(); i++) {
					if (i < amounts.size())
						amount = amounts.get(i);
					SheepWarsSheep.giveSheep(people, sheeps.get(i), amount);
				}
				Sounds.playSound(people, people.getLocation(), Sounds.VILLAGER_YES, 1f, 1f);
				if (people != player) {
					people.sendMessage(ChatColor.GREEN + "✚ " + player.getName() + " gave you some sheep.");
				}
			}
		} else {
			player.sendMessage(ChatColor.RED + "You can't try to give you the sheeps while the game hasn't started.");
			Sounds.playSound(player, player.getLocation(), Sounds.VILLAGER_NO, 1f, 1f);
		}
	}

	@Override
	protected void onExeConsole(ConsoleCommandSender sender, String... args) {
		notAllowed(sender);
	}
}
