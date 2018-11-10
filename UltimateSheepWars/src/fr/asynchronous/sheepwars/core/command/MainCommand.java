package fr.asynchronous.sheepwars.core.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.Hologram;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.BoosterManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.manager.SheepManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.task.BeginCountdown;
import fr.asynchronous.sheepwars.core.util.Utils;

public class MainCommand implements CommandExecutor {

	public static final String PREFIX = ChatColor.AQUA + "" + ChatColor.BOLD + "ULTIMATE SHEEP WARS " + ChatColor.DARK_GRAY + "| " + ChatColor.RESET;

	public UltimateSheepWarsPlugin plugin;
	public boolean initializeBool = false;
	public boolean pluginConfigured = false;

	public MainCommand(UltimateSheepWarsPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Please connect on the server, then do this command again.");
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
				player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "===========" + ChatColor.RESET + " " + ChatColor.AQUA + ChatColor.BOLD + "ULTIMATE SHEEP WARS " + ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "============");
				player.sendMessage("");
				if (!pluginConfigured) {
					final String ok = ChatColor.DARK_GRAY + "| " + ChatColor.GREEN + "✔" + ChatColor.DARK_GRAY + " |" + ChatColor.GRAY;
					final String no = ChatColor.DARK_GRAY + "| " + ChatColor.DARK_RED + "✖" + ChatColor.DARK_GRAY + " |" + ChatColor.GRAY;
					player.sendMessage(ChatColor.GRAY + "/sw setLobby " + (ConfigManager.getLocation(Field.LOBBY) == Utils.getDefaultLocation() ? no : ok) + " (set the lobby)");
					player.sendMessage(ChatColor.GRAY + "/sw addBooster/clearBoosters " + (ConfigManager.getLocations(Field.BOOSTERS).isEmpty() ? no : ok) + " (add a booster)");
					player.sendMessage(ChatColor.GRAY + "/sw addSpawn/clearSpawns <team> " + (ConfigManager.getLocations(Field.RED_SPAWNS).isEmpty() || ConfigManager.getLocations(Field.BLUE_SPAWNS).isEmpty() || ConfigManager.getLocations(Field.SPEC_SPAWNS).isEmpty() ? no : ok) + " (" + (!ConfigManager.getLocations(Field.BLUE_SPAWNS).isEmpty() ? ChatColor.GREEN : ChatColor.RED) + "blue§7/" + (!ConfigManager.getLocations(Field.RED_SPAWNS).isEmpty() ? ChatColor.GREEN : ChatColor.RED) + "red§7/" + (!ConfigManager.getLocations(Field.SPEC_SPAWNS).isEmpty() ? ChatColor.GREEN : ChatColor.RED) + "spec§7)");
					player.sendMessage("");
					player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "When ingame configuration is over, stop your server, edit");
					player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "the config to suit your needs and restart the server.");
				} else {
					player.sendMessage("/sw setLobby " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Set the lobby");
					player.sendMessage("/sw addBooster/clearBoosters " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Add a booster");
					player.sendMessage("/sw addSpawn/clearSpawns <team> " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Add a blue/red/spec spawn");
					player.sendMessage("");
					player.sendMessage("/sw start " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Reduce countdown");
					player.sendMessage("/sw give" + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Give you all sheeps");
					player.sendMessage("/sw kits " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "See all loaded kits");
					player.sendMessage("/sw sheeps " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "See all loaded sheeps");
					player.sendMessage("/sw boosters " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "See all loaded boosters");
					player.sendMessage("/sw listmsg " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "See all loaded messages");
					player.sendMessage("");
					player.sendMessage("/stats <player> " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Check player's stats");
					player.sendMessage("/lang " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Change your language");
					player.sendMessage("/hub " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Return to the fallback server");
				}
				player.sendMessage("");
				player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "===========================================");
			} else if (sub.equalsIgnoreCase("setlobby")) {
				ConfigManager.setLocation(Field.LOBBY, player.getLocation().add(0, 2, 0));
				player.sendMessage(PREFIX + ChatColor.GREEN + "Lobby set.");
				UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.VILLAGER_HAPPY, player.getLocation().add(0, 1, 0), 0f, 0f, 0f, 1, 0.1f);
				Hologram.runHologramTask(ChatColor.GREEN + "Lobby set!", player.getLocation().add(0, 2, 0), 5, this.plugin);
			} else if (sub.equalsIgnoreCase("clearspawns") && args.length == 2) {
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
				for (Location loc : ConfigManager.getLocations(Field.BOOSTERS))
					UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.SMOKE_LARGE, loc, 0f, 0f, 0f, 1, 0.1f);
				ConfigManager.clearLocations(Field.BOOSTERS);
				player.sendMessage(PREFIX + ChatColor.GREEN + "Booster locations where cleared!");
			} else if (sub.equalsIgnoreCase("addbooster")) {
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
				if (!this.plugin.hasPreGameTaskStarted())
					new BeginCountdown(this.plugin);
				this.plugin.getPreGameTask().shortenCountdown();
			} else if (sub.equalsIgnoreCase("test")) {
				player.sendMessage("Active workers: " + Bukkit.getScheduler().getActiveWorkers().size());
				player.sendMessage("Pending tasks: " + Bukkit.getScheduler().getPendingTasks().size());
				player.sendMessage("Particle players: " + PlayerData.getParticlePlayers().size());
				player.sendMessage("Players data: " + PlayerData.getData().size());

				player.sendMessage("Tested.");
			} else if (sub.equalsIgnoreCase("sheeps")) {
				player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Loaded Sheeps :");
				for (SheepManager sheep : SheepManager.getAvailableSheeps()) {
					player.sendMessage(ChatColor.GRAY + "∙ " + sheep.getName(player));
					player.sendMessage(ChatColor.GRAY + "  ∙ Duration : " + ChatColor.YELLOW + sheep.getDuration() + " seconds");
					player.sendMessage(ChatColor.GRAY + "  ∙ Health : " + ChatColor.YELLOW + sheep.getHealth() + " half hearts");
					player.sendMessage(ChatColor.GRAY + "  ∙ Luck get rate : " + ChatColor.YELLOW + (sheep.getRandom() * 100.0) + " %");
					player.sendMessage(ChatColor.GRAY + "  ∙ Abilities : " + ChatColor.YELLOW + sheep.getAbilities().toString());
					player.sendMessage(ChatColor.GRAY + "  ∙ Color : " + ChatColor.YELLOW + sheep.getColor());
					player.sendMessage(ChatColor.GRAY + "  ∙ Is friendly : " + ChatColor.YELLOW + sheep.isFriendly());
					player.sendMessage(ChatColor.GRAY + "  ∙ Drop wool : " + ChatColor.YELLOW + sheep.isDropAllowed());
				}
				Sounds.playSound(player, player.getLocation(), Sounds.ORB_PICKUP, 1f, 1f);
			} else if (sub.equalsIgnoreCase("kits")) {
				player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Loaded Kits :");
				for (KitManager kit : KitManager.getAvailableKits()) {
					player.sendMessage(ChatColor.GRAY + "∙ " + kit.getName(player) + ChatColor.GRAY + " (Id " + ChatColor.YELLOW + kit.getId() + ChatColor.GRAY + ")");
					player.sendMessage(ChatColor.GRAY + "  ∙ Required wins : " + ChatColor.YELLOW + kit.getRequiredWins());
					player.sendMessage(ChatColor.GRAY + "  ∙ Permission : " + ChatColor.YELLOW + kit.getPermission());
					player.sendMessage(ChatColor.GRAY + "  ∙ Price : " + ChatColor.YELLOW + kit.getPrice());
					player.sendMessage(ChatColor.GRAY + "  ∙ Icon : " + ChatColor.YELLOW + kit.getIcon().toItemStack().getType().toString().replaceAll("_", " "));
					player.sendMessage(ChatColor.GRAY + "  ∙ Description : " + ChatColor.YELLOW + kit.getDescription(player));
				}
				Sounds.playSound(player, player.getLocation(), Sounds.ORB_PICKUP, 1f, 1f);
			} else if (sub.equalsIgnoreCase("boosters")) {
				player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Loaded Boosters :");
				for (BoosterManager boost : BoosterManager.getAvailableBoosters()) {
					player.sendMessage(ChatColor.GRAY + "∙ " + boost.getName().getMessage(player));
					player.sendMessage(ChatColor.GRAY + "  ∙ Duration : " + ChatColor.YELLOW + boost.getDuration());
					player.sendMessage(ChatColor.GRAY + "  ∙ BossBar color : " + ChatColor.YELLOW + boost.getDisplayColor().toString().replaceAll("_", " "));
					player.sendMessage(ChatColor.GRAY + "  ∙ Wool color : " + ChatColor.YELLOW + boost.getWoolColor().toString().replaceAll("_", " "));
				}
				Sounds.playSound(player, player.getLocation(), Sounds.ORB_PICKUP, 1f, 1f);
			} else if (sub.equalsIgnoreCase("give")) {
				if (GameState.isStep(GameState.INGAME)) {
					if (args.length > 1) {
						if (args[1].equalsIgnoreCase("*")) {
							for (Player online : Bukkit.getOnlinePlayers())
								giveSheeps(online, player);
							player.sendMessage(ChatColor.GREEN + "The sheep were given to all the players !");
						} else if (Bukkit.getPlayer(args[1]) != null) {
							Player receiver = Bukkit.getPlayer(args[1]);
							giveSheeps(receiver, player);
						} else {
							player.sendMessage(ChatColor.RED + args[1] + " is not connected.");
						}
					} else {
						giveSheeps(player, null);
					}
				} else {
					player.sendMessage(ChatColor.RED + "You can't give you the sheeps while the game hasn't started.");
					Sounds.playSound(player, player.getLocation(), Sounds.VILLAGER_NO, 1f, 1f);
				}
			} else if (sub.equalsIgnoreCase("listmsg") && player.isOp()) {
				for (MsgEnum msg : MsgEnum.values())
					player.sendMessage(msg.getMessage() + "" + ChatColor.DARK_AQUA + " ➡ " + ChatColor.RESET + " " + Message.getMessage(player, msg));
				player.sendMessage(ChatColor.GREEN + "Default Language (English)" + ChatColor.DARK_AQUA + " ➡ " + ChatColor.GREEN + " Your Language (" + PlayerData.getPlayerData(player).getLanguage().getName() + ")");
			} else {
				sender.sendMessage(ChatColor.RED + "Wrong command !");
			}
			return true;
		}
		player.sendMessage(ChatColor.GRAY + "∙ Plugin " + ChatColor.GREEN + "UltimateSheepWars v" + this.plugin.getDescription().getVersion() + ChatColor.GRAY + " by " + ChatColor.GREEN + "The Asynchronous" + ChatColor.GRAY + ".");
		player.sendMessage(ChatColor.GRAY + "∙ Many thanks to @Roytreo28 (" + ChatColor.RED + "Developer" + ChatColor.GRAY + "), @KingRider26 (" + ChatColor.RED + "Co-Developer" + ChatColor.GRAY + "), @6985jjorda (English Translation), @felibouille (German Translation), @jeussa (Instant Explosion Firework Effect)");
		player.sendMessage(ChatColor.GRAY + "∙ If you encounter any issue, come and talk to us: " + ChatColor.AQUA + "https://discord.gg/nZthcPh");
		return false;
	}
	
	public void giveSheeps(Player player, Player giver) {
		for (SheepManager sheep : SheepManager.getAvailableSheeps()) {
			ItemStack i = sheep.getIcon(player).clone();
			i.setAmount(32);
			player.getInventory().addItem(i);
		}
		Sounds.playSound(player, player.getLocation(), Sounds.VILLAGER_YES, 1f, 1f);
		if (giver != null) {
			player.sendMessage(ChatColor.GREEN + "You got all the sheep of the game. You can thank " + giver.getName() + ".");
		}
	}
}
