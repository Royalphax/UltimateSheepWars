package fr.asynchronous.sheepwars.core.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.Hologram;
import fr.asynchronous.sheepwars.core.handler.InteractiveType;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Permissions;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.BoosterManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.manager.SheepManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.manager.URLManager;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.task.BeginCountdown;
import fr.asynchronous.sheepwars.core.util.Utils;

public class MainCommand implements CommandExecutor {

	public static final String PREFIX = ChatColor.AQUA + "" + ChatColor.BOLD + "USW " + ChatColor.WHITE + "➢ " + ChatColor.RESET;

	public UltimateSheepWarsPlugin plugin;
	public boolean initializeBool = false;
	public boolean pluginConfigured = false;

	public MainCommand(UltimateSheepWarsPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Please connect on the server first.");
			return true;
		}
		if (!initializeBool) {
			initializeBool = true;
			pluginConfigured = Utils.isPluginConfigured();
		}
		final Player player = (Player) sender;
		if (args.length != 0) {
			final String sub = args[0];
			if (sub.equalsIgnoreCase("help")) {
				player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "============" + ChatColor.RESET + " " + ChatColor.AQUA + ChatColor.BOLD + "ULTIMATE SHEEP WARS " + ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "=============");
				if (!pluginConfigured) {
					player.sendMessage("");
					final String ok = ChatColor.DARK_GRAY + "❘ " + ChatColor.GREEN + "✔" + ChatColor.DARK_GRAY + " ❘" + ChatColor.GRAY;
					final String no = ChatColor.DARK_GRAY + "❘ " + ChatColor.DARK_RED + "✖" + ChatColor.DARK_GRAY + " ❘" + ChatColor.GRAY;
					player.sendMessage(ChatColor.GRAY + "/usw setLobby " + (ConfigManager.getLocation(Field.LOBBY) == Utils.getDefaultLocation() ? no : ok) + " Set the lobby");
					player.sendMessage(ChatColor.GRAY + "/usw addBooster/clearBoosters " + (ConfigManager.getLocations(Field.BOOSTERS).isEmpty() ? no : ok) + " Add a booster");
					player.sendMessage(ChatColor.GRAY + "/usw addSpawn/clearSpawns [team] " + (ConfigManager.getLocations(Field.RED_SPAWNS).isEmpty() || ConfigManager.getLocations(Field.BLUE_SPAWNS).isEmpty() || ConfigManager.getLocations(Field.SPEC_SPAWNS).isEmpty() ? no : ok) + " " + (!ConfigManager.getLocations(Field.BLUE_SPAWNS).isEmpty() ? ChatColor.GREEN : ChatColor.RED) + "Blue§7, " + (!ConfigManager.getLocations(Field.RED_SPAWNS).isEmpty() ? ChatColor.GREEN : ChatColor.RED) + "red§7 or " + (!ConfigManager.getLocations(Field.SPEC_SPAWNS).isEmpty() ? ChatColor.GREEN : ChatColor.RED) + "spec§7.");
					player.sendMessage("");
					player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "When ingame configuration is over, stop your server, edit");
					player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "the config to suit your needs and restart the server.");
					player.sendMessage("");
				} else {
					player.sendMessage(ChatColor.ITALIC + "Param : <> = optional, [] = needed. Teams : blue, red or spec");
					if (Permissions.USW_ADMIN.hasPermission(player)) {
						player.sendMessage("");
						player.sendMessage("/usw setLobby " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Set the lobby");
						player.sendMessage("/usw addBooster " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Add a booster location");
						player.sendMessage("/usw clearBoosters " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Clear booster locations");
						player.sendMessage("/usw addSpawn [team] " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Add a team spawn location");
						player.sendMessage("/usw clearSpawns [team] " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Clear team spawn locations");
						player.sendMessage("");
						player.sendMessage("/usw start " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Reduce countdown");
						player.sendMessage("/usw give <player/*> <N°,...> <amount,...>" + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Give you all sheeps");
						player.sendMessage("/usw kits <N°> " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "See all loaded kits");
						player.sendMessage("/usw sheeps <N°> <throw>" + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "See all loaded sheeps");
						player.sendMessage("/usw boosters <N°> " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "See all loaded boosters");
					}
					player.sendMessage("");
					player.sendMessage("/stats <player> " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Check player's stats");
					player.sendMessage("/lang " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Change your language");
					player.sendMessage("/hub " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Return to the fallback server");
				}
				player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "=============================================");
			} else if (sub.equalsIgnoreCase("setlobby")) {
				if (!Permissions.USW_ADMIN.hasPermission(player, true))
					return true;
				ConfigManager.setLocation(Field.LOBBY, player.getLocation().add(0, 2, 0));
				player.sendMessage(PREFIX + ChatColor.GREEN + "Lobby set.");
				UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.VILLAGER_HAPPY, player.getLocation().add(0, 1, 0), 0f, 0f, 0f, 1, 0.1f);
				Hologram.runHologramTask(ChatColor.GREEN + "Lobby set!", player.getLocation().add(0, 2, 0), 5, this.plugin);
			} else if (sub.equalsIgnoreCase("clearspawns") && args.length == 2) {
				if (!Permissions.USW_ADMIN.hasPermission(player, true))
					return true;
				if (!args[1].equalsIgnoreCase("red") && !args[1].equalsIgnoreCase("blue") && !args[1].equalsIgnoreCase("spec")) {
					player.sendMessage(PREFIX + ChatColor.RED + "Team " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " doesn't exist (only blue/red/spec).");
				} else {
					final TeamManager team = TeamManager.getTeam(args[1]);
					for (Location loc : team.getSpawns())
						UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.SMOKE_LARGE, loc, 0f, 0f, 0f, 1, 0.1f);
					team.getSpawns().clear();
					player.sendMessage(PREFIX + ChatColor.GREEN + "Team " + team.getColor() + team.getDisplayName(player) + ChatColor.GREEN + " spawns where cleared!");
				}
			} else if (sub.equalsIgnoreCase("addspawn") && args.length == 2) {
				if (!Permissions.USW_ADMIN.hasPermission(player, true))
					return true;
				if (!args[1].equalsIgnoreCase("red") && !args[1].equalsIgnoreCase("blue") && !args[1].equalsIgnoreCase("spec")) {
					player.sendMessage(PREFIX + ChatColor.RED + "Team " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " doesn't exist. (blue/red/spec)");
				} else {
					final TeamManager team = TeamManager.getTeam(args[1]);
					team.getSpawns().add(player.getLocation());
					player.sendMessage(PREFIX + ChatColor.GREEN + "You have added a spawn for " + team.getColor() + team.getDisplayName(player) + ChatColor.GREEN + " team.");
					UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.VILLAGER_HAPPY, player.getLocation().add(0, 1, 0), 0f, 0f, 0f, 1, 0.1f);
					Hologram.runHologramTask(team.getColor() + "New team spawn added: N°" + team.getSpawns().size(), player.getLocation(), 5, this.plugin);
				}
			} else if (sub.equalsIgnoreCase("clearboosters")) {
				if (!Permissions.USW_ADMIN.hasPermission(player, true))
					return true;
				for (Location loc : ConfigManager.getLocations(Field.BOOSTERS))
					UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.SMOKE_LARGE, loc, 0f, 0f, 0f, 1, 0.1f);
				ConfigManager.clearLocations(Field.BOOSTERS);
				player.sendMessage(PREFIX + ChatColor.GREEN + "Booster locations where cleared!");
			} else if (sub.equalsIgnoreCase("addbooster")) {
				if (!Permissions.USW_ADMIN.hasPermission(player, true))
					return true;
				final Location location = player.getLocation().subtract(0.0, 1.0, 0.0).getBlock().getLocation();
				if (location.getBlock().getType() != Material.AIR) {
					player.sendMessage(PREFIX + ChatColor.RED + "You must have an air block under your feet.");
				} else {
					ConfigManager.addLocation(Field.BOOSTERS, location);
					player.sendMessage(PREFIX + ChatColor.GREEN + "You have added a magic wool at (" + location.getBlockX() + ", " + location.getY() + ", " + location.getZ() + ")");
					UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.REDSTONE, location, 1.0f, 1.0f, 1.0f, 20, 1.0f);
					Hologram.runHologramTask(Message.getDecoration() + ChatColor.GREEN + " Magic wool N°" + ConfigManager.getLocations(Field.BOOSTERS).size() + " " + Message.getDecoration(), player.getLocation(), 5, this.plugin);
				}
			} else if (sub.equalsIgnoreCase("start")) {
				if (!Permissions.USW_START.hasPermission(player, true))
					return true;
				if (!this.plugin.hasPreGameTaskStarted())
					new BeginCountdown(this.plugin);
				this.plugin.getPreGameTask().shortenCountdown();
			} else if (sub.equalsIgnoreCase("test")) {
				if (!Permissions.USW_DEVELOPER.hasPermission(player, true))
					return true;
				player.sendMessage("Active workers: " + Bukkit.getScheduler().getActiveWorkers().size());
				player.sendMessage("Pending tasks: " + Bukkit.getScheduler().getPendingTasks().size());
				player.sendMessage("Particle players: " + PlayerData.getParticlePlayers().size());
				player.sendMessage("Players data: " + PlayerData.getData().size());

			} else if (sub.equalsIgnoreCase("sheeps")) {

				if (!Permissions.USW_DEVELOPER.hasPermission(player, true))
					return true;
				int i = -1;
				if (args.length > 1 && Utils.isInteger(args[1]))
					i = Integer.parseInt(args[1]);
				if (i >= 0 && SheepManager.getAvailableSheeps().size() > 0) {
					SheepManager sheep = SheepManager.getAvailableSheeps().get(0);
					if (i < SheepManager.getAvailableSheeps().size())
						sheep = SheepManager.getAvailableSheeps().get(i);
					player.sendMessage("∙ " + ChatColor.GRAY + sheep.getName(player));
					player.sendMessage("  ∙ " + ChatColor.GRAY + "Duration : " + ChatColor.YELLOW + (sheep.getDuration() <= 0 ? "∞" : sheep.getDuration() + " seconds"));
					player.sendMessage("  ∙ " + ChatColor.GRAY + "Health : " + ChatColor.YELLOW + sheep.getHealth() + " half hearts");
					player.sendMessage("  ∙ " + ChatColor.GRAY + "Luck get rate : " + ChatColor.YELLOW + (sheep.getRandom() * 100.0) + " %");
					player.sendMessage("  ∙ " + ChatColor.GRAY + "Abilities : " + ChatColor.YELLOW + sheep.getAbilities().toString());
					player.sendMessage("  ∙ " + ChatColor.GRAY + "Color : " + ChatColor.YELLOW + sheep.getColor());
					player.sendMessage("  ∙ " + ChatColor.GRAY + "Is friendly : " + ChatColor.YELLOW + sheep.isFriendly());
					player.sendMessage("  ∙ " + ChatColor.GRAY + "Drop wool : " + ChatColor.YELLOW + sheep.isDropAllowed());
					if (args.length > 2 && args[2].equalsIgnoreCase("throw")) {
						if (!GameState.isStep(GameState.WAITING)) {
							player.sendMessage(ChatColor.RED + "You cannot try to throw a sheep during or after the game.");
						} else {
							sheep.throwSheep(player, this.plugin);
						}
					}
				} else {
					player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Loaded Sheeps :");
					int x = 0;
					for (SheepManager sheep : SheepManager.getAvailableSheeps()) {
						player.sendMessage("∙ " + ChatColor.GRAY + sheep.getName(player) + ChatColor.GRAY + " (Id " + ChatColor.YELLOW + x + ChatColor.GRAY + ")");
						x++;
					}
				}
				Sounds.playSound(player, player.getLocation(), Sounds.ORB_PICKUP, 1f, 1f);

			} else if (sub.equalsIgnoreCase("kits")) {

				if (!Permissions.USW_DEVELOPER.hasPermission(player, true))
					return true;
				int i = -1;
				if (args.length > 1 && Utils.isInteger(args[1]))
					i = Integer.parseInt(args[1]);
				if (i >= 0 && KitManager.getAvailableKits().size() > 0) {
					KitManager kit = KitManager.getAvailableKits().get(0);
					for (KitManager k : KitManager.getAvailableKits())
						if (k.getId() == i)
							kit = k;
					player.sendMessage("∙ " + ChatColor.GRAY + kit.getName(player) + ChatColor.GRAY + " (Id " + ChatColor.YELLOW + kit.getId() + ChatColor.GRAY + ")");
					player.sendMessage("  ∙ " + ChatColor.GRAY + "Required wins : " + ChatColor.YELLOW + kit.getRequiredWins());
					player.sendMessage("  ∙ " + ChatColor.GRAY + "Permission : " + ChatColor.YELLOW + kit.getPermission());
					player.sendMessage("  ∙ " + ChatColor.GRAY + "Price : " + ChatColor.YELLOW + kit.getPrice());
					player.sendMessage("  ∙ " + ChatColor.GRAY + "Icon : " + ChatColor.YELLOW + kit.getIcon().toItemStack().getType().toString().replaceAll("_", " "));
					player.sendMessage("  ∙ " + ChatColor.GRAY + "Description : " + ChatColor.YELLOW + kit.getDescription(player));
				} else {
					player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Loaded Kits :");
					for (KitManager kit : KitManager.getAvailableKits()) {
						player.sendMessage("∙ " + ChatColor.GRAY + kit.getName(player) + ChatColor.GRAY + " (Id " + ChatColor.YELLOW + kit.getId() + ChatColor.GRAY + ")");
					}
				}
				Sounds.playSound(player, player.getLocation(), Sounds.ORB_PICKUP, 1f, 1f);

			} else if (sub.equalsIgnoreCase("boosters")) {

				if (!Permissions.USW_DEVELOPER.hasPermission(player, true))
					return true;
				int i = -1;
				if (args.length > 1 && Utils.isInteger(args[1]))
					i = Integer.parseInt(args[1]);
				if (i >= 0 && BoosterManager.getAvailableBoosters().size() > 0) {
					BoosterManager boost = BoosterManager.getAvailableBoosters().get(0);
					if (i < BoosterManager.getAvailableBoosters().size())
						boost = BoosterManager.getAvailableBoosters().get(i);
					player.sendMessage("∙ " + ChatColor.GRAY + boost.getName().getMessage(player));
					player.sendMessage("  ∙ " + ChatColor.GRAY + "Duration : " + ChatColor.YELLOW + boost.getDuration() + " seconds");
					player.sendMessage("  ∙ " + ChatColor.GRAY + "BossBar color : " + ChatColor.YELLOW + boost.getDisplayColor().toString().replaceAll("_", " "));
					player.sendMessage("  ∙ " + ChatColor.GRAY + "Wool color : " + ChatColor.YELLOW + boost.getWoolColor().toString().replaceAll("_", " "));
				} else {
					player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Loaded Boosters :");
					int x = 0;
					for (BoosterManager boost : BoosterManager.getAvailableBoosters()) {
						player.sendMessage("∙ " + ChatColor.GRAY + boost.getName().getMessage(player) + ChatColor.GRAY + " (Id " + ChatColor.YELLOW + x + ChatColor.GRAY + ")");
						x++;
					}
				}
				Sounds.playSound(player, player.getLocation(), Sounds.ORB_PICKUP, 1f, 1f);
			} else if (sub.equalsIgnoreCase("give")) {
				if (GameState.isStep(GameState.INGAME)) {
					List<SheepManager> sheeps = new ArrayList<>(SheepManager.getAvailableSheeps());
					List<Integer> amounts = new ArrayList<>();
					List<Player> players = new ArrayList<>();
					players.add(player);

					if (args.length > 1) { // A été spécifié le/les joueurs.

						if (args[1].equalsIgnoreCase("*")) {
							if (!Permissions.USW_GIVE_ALL.hasPermission(player, true))
								return true;
							players.clear();
							for (Player online : player.getWorld().getPlayers())
								if (!PlayerData.getPlayerData(online).isSpectator() && !players.contains(online))
									players.add(online);
						}
						else if (Bukkit.getPlayer(args[1]) != null) {
							if (!Permissions.USW_GIVE_OTHER.hasPermission(player, true))
								return true;
							players.clear();
							players.add(Bukkit.getPlayer(args[1]));
						} else {
							if (!Permissions.USW_GIVE_OTHER.hasPermission(player, true))
								return true;
							player.sendMessage(ChatColor.RED + args[1] + " is not connected.");
							return true;
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
							
							List<SheepManager> availableSheeps = SheepManager.getAvailableSheeps();
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
						return true;
					
					if (sheeps.isEmpty()) {
						player.sendMessage(ChatColor.RED + "No sheep has been given (check the syntax of your command).");
						return true;
					}
					
					for (Player people : players) {
						int amount = 64;
						for (int i = 0; i < sheeps.size(); i++) {
							if (i < amounts.size())
								amount = amounts.get(i);
							SheepManager.giveSheep(people, sheeps.get(i), amount);
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
			} else {
				sender.sendMessage(ChatColor.RED + "Wrong command !");
			}
			return true;
		}
		player.sendMessage("∙ " + ChatColor.GRAY + "Plugin " + ChatColor.GREEN + "UltimateSheepWars v" + (URLManager.isUpToDate() ? "" : ChatColor.RED) + this.plugin.getDescription().getVersion() + ChatColor.GRAY + " by " + ChatColor.GREEN + "The Asynchronous" + ChatColor.GRAY + ".");
		UltimateSheepWarsPlugin.getVersionManager().getNMSUtils().displayInteractiveText(player, "∙ " + ChatColor.GRAY + "Special thanks to all the following contributors : ", ChatColor.AQUA + "(hover)", "", InteractiveType.SHOW_TEXT, ChatColor.GRAY + "- @Roytreo28 (" + ChatColor.RED + "Developer" + ChatColor.GRAY + ")\n" + ChatColor.GRAY + "- @KingRider26 (" + ChatColor.RED + "Co-Developer" + ChatColor.GRAY + ")\n" + ChatColor.GRAY + "- @6985jjorda (" + ChatColor.GOLD + "English Translation" + ChatColor.GRAY + ")\n" + ChatColor.GRAY + "- @felibouille (" + ChatColor.GOLD + "German Translation" + ChatColor.GRAY + ")\n" + ChatColor.GRAY + "- @jeussa (" + ChatColor.YELLOW + "Instant Explosion Firework Effect" + ChatColor.GRAY + ")\n" + ChatColor.GRAY + "- @Finoway (" + ChatColor.LIGHT_PURPLE + "Official tester" + ChatColor.GRAY + ")");
		UltimateSheepWarsPlugin.getVersionManager().getNMSUtils().displayInteractiveText(player, "∙ " + ChatColor.GRAY + "If you encounter any issue, come and talk to us : ", ChatColor.AQUA + "(click)", "", InteractiveType.OPEN_URL, "https://discord.gg/nZthcPh");
		player.sendMessage("∙ " + ChatColor.GRAY + "Use " + ChatColor.WHITE + "/usw help " + ChatColor.GRAY + "to see the list of all available commands.");
		return false;
	}
}
