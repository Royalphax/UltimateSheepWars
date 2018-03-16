package fr.asynchronous.sheepwars.core.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.gui.GuiValidateOwner;
import fr.asynchronous.sheepwars.core.gui.manager.GuiManager;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.Hologram;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.SheepManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.task.BeginCountdown;
import fr.asynchronous.sheepwars.core.util.Utils;

public class MainCommand implements CommandExecutor {

	public UltimateSheepWarsPlugin plugin;

	public MainCommand(UltimateSheepWarsPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Please connect on the server, then do this command again.");
            return true;
        }
        final Player player = (Player)sender;
        if (args.length != 0) {
            final String sub = args[0];
            if (sub.equalsIgnoreCase("help")) {
            	player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "=============" + ChatColor.RESET + " " + ChatColor.AQUA + ChatColor.BOLD + "UltimateSheepWars " + ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "=============");
            	player.sendMessage("");
            	if (Utils.isPluginConfigured())
            	{
            		final String ok = ChatColor.DARK_GRAY + "| " + ChatColor.GREEN + "✔" + ChatColor.DARK_GRAY + " |" + ChatColor.GRAY;
                	final String no = ChatColor.DARK_GRAY + "| " + ChatColor.DARK_RED + "✖" + ChatColor.DARK_GRAY + " |" + ChatColor.GRAY;
            		player.sendMessage(ChatColor.GRAY + "/sw setLobby "+(ConfigManager.getLocation(Field.LOBBY) == Utils.getDefaultLocation() ? no : ok)+" (set the lobby)");
                	player.sendMessage(ChatColor.GRAY + "/sw (addBooster/clearBoosters) "+(ConfigManager.getLocations(Field.BOOSTERS).isEmpty() ? no : ok)+" (add a booster)");
                	player.sendMessage(ChatColor.GRAY + "/sw (addSpawn/clearSpawns) <team> "+no+" (" + (!ConfigManager.getLocations(Field.BLUE_SPAWNS).isEmpty() ? ChatColor.GREEN : ChatColor.RED) + "blue§7/" + (!ConfigManager.getLocations(Field.RED_SPAWNS).isEmpty() ? ChatColor.GREEN : ChatColor.RED) + "red§7/" + (!ConfigManager.getLocations(Field.SPEC_SPAWNS).isEmpty() ? ChatColor.GREEN : ChatColor.RED) + "spec§7)");
                	player.sendMessage("");
                	player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "When ingame configuration is over, stop your server, edit");
                	player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + " the config to suit your needs and restart the server.");
            	} else {
            		player.sendMessage("/sw setLobby " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Set the lobby");
                	player.sendMessage("/sw (addBooster/clearBoosters) " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Add a booster");
                	player.sendMessage("/sw (addSpawn/clearSpawns) <team> " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Add a blue/red/spec spawn");
            		player.sendMessage("");
                	player.sendMessage("/sw start " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Reduce countdown");
            		player.sendMessage("/sw give " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Give you all sheeps");
            		player.sendMessage("/sw changeowner " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Change plugin's owner");
            		player.sendMessage("");
            		player.sendMessage("/stats <player> " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Check player's stats");
            		player.sendMessage("/lang " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Change your language");
            		player.sendMessage("/hub " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Return to the fallback server");
            	}
            	player.sendMessage("");
            	player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "===========================================");
            }
            else if (sub.equalsIgnoreCase("setlobby")) {
            	ConfigManager.setLocation(Field.LOBBY, player.getLocation().add(0,2,0));
                player.sendMessage(ChatColor.GREEN + "Lobby set.");
                UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.VILLAGER_HAPPY, player.getLocation().add(0,1,0), 0f, 0f, 0f, 1, 0.1f);
                Hologram.runHologramTask(ChatColor.GREEN + "Lobby set!", player.getLocation().add(0,2,0), 5, this.plugin);
            }
            else if (sub.equalsIgnoreCase("clearspawns") && args.length == 2) {
                if (!args[1].equalsIgnoreCase("red") && !args[1].equalsIgnoreCase("blue") && !args[1].equalsIgnoreCase("spec")) {
                    player.sendMessage(ConfigManager.getString(Field.PREFIX) + ChatColor.RED + "Team " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " doesn't exist (only blue/red/spec).");
                }
                else {
                    final TeamManager team = TeamManager.getTeam(args[1]);
                    for (Location loc : team.getSpawns())
                    	UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.SMOKE_LARGE, loc, 0f, 0f, 0f, 1, 0.1f);
                    team.getSpawns().clear();
                    player.sendMessage(ConfigManager.getString(Field.PREFIX) + ChatColor.GREEN + "Team " + team.getColor() + team.getDisplayName(player) + ChatColor.GREEN + " spawns where cleared!");
                }
            }
            else if (sub.equalsIgnoreCase("addspawn") && args.length == 2) {
                if (!args[1].equalsIgnoreCase("red") && !args[1].equalsIgnoreCase("blue") && !args[1].equalsIgnoreCase("spec")) {
                    player.sendMessage(ConfigManager.getString(Field.PREFIX) + ChatColor.RED + "Team " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " doesn't exist. (blue/red/spec)");
                }
                else {
                    final TeamManager team = TeamManager.getTeam(args[1]);
                    team.getSpawns().add(player.getLocation());
                    player.sendMessage(ConfigManager.getString(Field.PREFIX) + ChatColor.GREEN + "You have added a spawn for " + team.getColor() + team.getDisplayName(player) + ChatColor.GREEN + " team.");
                    UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.VILLAGER_HAPPY, player.getLocation().add(0,1,0), 0f, 0f, 0f, 1, 0.1f);
                    Hologram.runHologramTask(team.getColor() + "New team spawn added: N°" + team.getSpawns().size(), player.getLocation(), 5, this.plugin);
                }
            }
            else if (sub.equalsIgnoreCase("clearboosters")) {
                for (Location loc : ConfigManager.getLocations(Field.BOOSTERS))
                	UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.SMOKE_LARGE, loc, 0f, 0f, 0f, 1, 0.1f);
                ConfigManager.clearLocations(Field.BOOSTERS);
                player.sendMessage(ConfigManager.getString(Field.PREFIX) + ChatColor.GREEN + "Booster locations where cleared!");
            }
            else if (sub.equalsIgnoreCase("addbooster")) {
                final Location location = player.getLocation().subtract(0.0, 1.0, 0.0).getBlock().getLocation();
                if (location.getBlock().getType() != Material.AIR) {
                    player.sendMessage(ConfigManager.getString(Field.PREFIX) + ChatColor.RED + "You must have an air block under your feet.");
                }
                else {
                	ConfigManager.addLocation(Field.BOOSTERS, location);
                    player.sendMessage(ChatColor.GREEN + "You have added a magic wool at (" + location.getBlockX() + "," + location.getY() + "," + location.getZ() + ")");
                    UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.REDSTONE, location, 1.0f, 1.0f, 1.0f, 20, 1.0f);
                    Hologram.runHologramTask(Message.getDecoration() + ChatColor.GREEN + " Magic wool N°" + ConfigManager.getLocations(Field.BOOSTERS).size() + " " + Message.getDecoration(), player.getLocation(), 5, this.plugin);
                }
            }
            else if (sub.equalsIgnoreCase("start")) {
            	if (!BeginCountdown.hasStarted())
            		new BeginCountdown(this.plugin);
            	this.plugin.getPreGameTask().shortenCountdown();
            }
            else if (sub.equalsIgnoreCase("test")) {
            	// DO NOTHING
            }
            else if (sub.equalsIgnoreCase("changeowner")) {
            	GuiManager.openGui(this.plugin, new GuiValidateOwner(this.plugin, player));
            }
            else if (sub.equalsIgnoreCase("give")) {
            	if (GameState.isStep(GameState.IN_GAME))
            	{
            		for (SheepManager sheep : SheepManager.getAvailableSheeps()) {
                		ItemStack i = sheep.getIcon(player).clone();
                		i.setAmount(32);
                		player.getInventory().addItem(i);
                	}
            	}
            	Sounds.playSound(player, player.getLocation(), Sounds.VILLAGER_NO, 1f, 1f);
            }
            else if (sub.equalsIgnoreCase("listmsg") && player.isOp()) {
            	for (MsgEnum m : MsgEnum.values())
            		player.sendMessage(ChatColor.UNDERLINE + m.toString() + ":" + ChatColor.RESET + " " + Message.getMessage(player, m));
            }
            else {
                sender.sendMessage(ChatColor.RED + "Wrong command !");
            }
            return true;
        }
        player.sendMessage(ChatColor.GREEN + "Plugin UltimateSheepWars v" + this.plugin.getDescription().getVersion() + " by Roytreo28 (@Asynchronous).");
        player.sendMessage(ChatColor.GREEN + "If you encounter any problem come and discuss it : " + ChatColor.AQUA + ChatColor.UNDERLINE + "https://discord.gg/nZthcPh");
		return false;
	}
}
