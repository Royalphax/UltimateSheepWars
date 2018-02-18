package fr.asynchronous.sheepwars.core.command;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.gui.GuiValidateOwner;
import fr.asynchronous.sheepwars.core.gui.manager.GuiManager;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.Hologram;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
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
            	if (this.plugin.SETUP_MODE)
            	{
            		String ok = ChatColor.DARK_GRAY + "| " + ChatColor.GREEN + "✔" + ChatColor.DARK_GRAY + " |" + ChatColor.GRAY;
                	String no = ChatColor.DARK_GRAY + "| " + ChatColor.DARK_RED + "✖" + ChatColor.DARK_GRAY + " |" + ChatColor.GRAY;
            		player.sendMessage(ChatColor.GRAY + "/sw setLobby "+(this.plugin.LOBBY_LOCATION == null ? no : ok)+" (set the lobby)");
                	player.sendMessage(ChatColor.GRAY + "/sw (addBooster/clearBoosters) "+(this.plugin.BOOSTER_LOCATIONS.isEmpty() ? no : ok)+" (add a booster)");
                	player.sendMessage(ChatColor.GRAY + "/sw (addSpawn/clearSpawns) <team> "+(Utils.isPluginConfigured(this.plugin) ? ok : no)+" (" + (TeamManager.BLUE.getSpawns().size() > 0 ? ChatColor.GREEN : ChatColor.RED) + "blue§7/" + (TeamManager.RED.getSpawns().size() > 0 ? ChatColor.GREEN : ChatColor.RED) + "red§7/" + (TeamManager.SPEC.getSpawns().size() > 0 ? ChatColor.GREEN : ChatColor.RED) + "spec§7)");
                	player.sendMessage("");
                	player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "When ingame configuration is over, stop your server, edit");
                	player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + " the config to suit your needs and start the server again.");
            	} else {
            		player.sendMessage("/sw setLobby " + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY + "Set the lobby");
                	player.sendMessage("/sw (addBooster/clearBoosters) " + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY + "Add a booster");
                	player.sendMessage("/sw (addSpawn/clearSpawns) <team> " + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY + "Add a blue/red/spec spawn");
            		player.sendMessage("");
                	player.sendMessage("/sw start " + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY + "Reduce countdown");
            		player.sendMessage("/sw give " + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY + "Give you all sheeps");
            		player.sendMessage("/sw changeowner " + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY + "Change plugin's owner");
            		player.sendMessage("");
            		player.sendMessage("/stats <player> " + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY + "Check player's stats");
            		player.sendMessage("/lang " + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY + "Change your language");
            		player.sendMessage("/hub " + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY + "Return to the fallback server");
            	}
            	player.sendMessage("");
            	player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "===========================================");
            }
            else if (sub.equalsIgnoreCase("setlobby")) {
            	this.plugin.LOBBY_LOCATION = player.getLocation().add(0,2,0);
                player.sendMessage(ChatColor.GREEN + "Lobby set.");
                UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.VILLAGER_HAPPY, player.getLocation().add(0,1,0), 0f, 0f, 0f, 1, 0.1f);
                Hologram.runHologramTask(ChatColor.GREEN + "Lobby set!", this.plugin.LOBBY_LOCATION, 5, this.plugin);
            }
            else if (sub.equalsIgnoreCase("clearspawns") && args.length == 2) {
                if (!args[1].equalsIgnoreCase("red") && !args[1].equalsIgnoreCase("blue") && !args[1].equalsIgnoreCase("spec")) {
                    player.sendMessage(ConfigManager.getString(Field.PREFIX) + ChatColor.RED + "Team " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " doesn't exist. (blue/red/spec)");
                }
                else {
                    final TeamManager team = TeamManager.getTeam(args[1]);
                    this.plugin.SETTINGS_CONFIG.set("teams." + team.getName(), null);
                    try {
                    	this.plugin.SETTINGS_CONFIG.save(this.plugin.SETTINGS_FILE);
					} catch (IOException ex) {
						new ExceptionManager(ex).register(true);
					}
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
            	this.plugin.SETTINGS_CONFIG.set("boosters", null);
                try {
                	this.plugin.SETTINGS_CONFIG.save(this.plugin.SETTINGS_FILE);
				} catch (IOException ex) {
					new ExceptionManager(ex).register(true);
				}
                for (Location loc : this.plugin.BOOSTER_LOCATIONS)
                	UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.SMOKE_LARGE, loc, 0f, 0f, 0f, 1, 0.1f);
                this.plugin.BOOSTER_LOCATIONS.clear();
                player.sendMessage(ConfigManager.getString(Field.PREFIX) + ChatColor.GREEN + "Booster locations where cleared!");
            }
            else if (sub.equalsIgnoreCase("addbooster")) {
                final Location location = player.getLocation().subtract(0.0, 1.0, 0.0).getBlock().getLocation();
                if (location.getBlock().getType() != Material.AIR) {
                    player.sendMessage(ConfigManager.getString(Field.PREFIX) + ChatColor.RED + "You must have an air block under your feet.");
                }
                else {
                	this.plugin.BOOSTER_LOCATIONS.add(location);
                    player.sendMessage(ChatColor.GREEN + "You have added a magic wool at (" + location.getBlockX() + "," + location.getY() + "," + location.getZ() + ")");
                    UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.REDSTONE, location, 1.0f, 1.0f, 1.0f, 20, 1.0f);
                    Hologram.runHologramTask(Message.getDecoration() + ChatColor.GREEN + " Magic wool N°" + this.plugin.BOOSTER_LOCATIONS.size() + " " + Message.getDecoration(), player.getLocation(), 5, this.plugin);
                }
            }
            else if (sub.equalsIgnoreCase("start")) {
            	if (!BeginCountdown.started)
            		new BeginCountdown(this.plugin);
            	BeginCountdown.forced = true;
            	if (this.plugin.PRE_GAME_TASK.timeUntilStart > 10) {
            		this.plugin.PRE_GAME_TASK.timeUntilStart = 10;
            	}
            }
            else if (sub.equalsIgnoreCase("test")) {
            	/*
            	Location playerLocation = player.getLocation().add(0,2,0);
                Location location = playerLocation.toVector().add(playerLocation.getDirection().multiply(0.5)).toLocation(player.getWorld());
                final org.bukkit.entity.Sheep sheepEntity = acI.DARK.spawnSheep(location, player, this.plugin);
                sheepEntity.setMetadata("sheepwars_sheep", new FixedMetadataValue(this.plugin, true));
                this.plugin.versionManager.getNMSUtils().setMaxHealth(sheepEntity, 8.0D);
                //sheepEntity.setMaxHealth(8.0); Incompatible < 1.11
                sheepEntity.setHealth(8.0D);
                sheepEntity.setVelocity(playerLocation.getDirection().add(new Vector(0,0.1,0)).multiply(this.plugin.LAUNCH_SHEEP_VELOCITY));
                aiI.playSound(player, null, aiH.HORSE_SADDLE, 1f, 1f);
                */
				for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
					if (entity instanceof ArmorStand) {
						ArmorStand armorstand = (ArmorStand) entity;
						int a = 0;
						int b = 0;
						int c = 0;
						int d = 0;
						try {
							a = Integer.parseInt(args[1]);
							b = Integer.parseInt(args[2]);
							c = Integer.parseInt(args[3]);
							d = Integer.parseInt(args[4]);
						} catch (NumberFormatException ex) {
							this.plugin.getLogger().warning(ex.getMessage());
						}
						EulerAngle ea = new EulerAngle(Math.toRadians(a), Math.toRadians(b), Math.toRadians(c));
						
						switch (d) {
							case 0:
								armorstand.setHeadPose(ea);
								break;
							case 1:
								armorstand.setLeftArmPose(ea);
								break;
							case 2:
								armorstand.setRightArmPose(ea);
								break;
							case 3:
								armorstand.setLeftLegPose(ea);
								break;
							case 4:
								armorstand.setRightLegPose(ea);
								break;
						}
					}
				}
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
        player.sendMessage(ChatColor.GREEN + "Plugin UltimateSheepWars v" + this.plugin.getDescription().getVersion() + " by Asynchronous.");
		return false;
	}
}
