package fr.royalpha.sheepwars.core.command;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.command.commands.ContributorCommand;
import fr.royalpha.sheepwars.core.command.commands.HubCommand;
import fr.royalpha.sheepwars.core.command.commands.LangCommand;
import fr.royalpha.sheepwars.core.command.commands.StatsCommand;
import fr.royalpha.sheepwars.core.command.subcommands.*;
import fr.royalpha.sheepwars.core.handler.Permissions;
import fr.royalpha.sheepwars.core.manager.ConfigManager;
import fr.royalpha.sheepwars.core.manager.UpdateManager;
import fr.royalpha.sheepwars.core.util.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Command manager.
 *
 * @author Royalpha
 */
public class CommandManager implements CommandExecutor {

	private static final int MAX_COMMANDS = 6;
	/**
	 * List of the registered commands.
	 */
	private List<SubCommand> commands = new ArrayList<>();

	private SheepWarsPlugin ultimateSheepWars;

	public CommandManager(SheepWarsPlugin ultimateSheepWars) {
		this.ultimateSheepWars = ultimateSheepWars;
		final Server server = this.ultimateSheepWars.getServer();
		server.getPluginCommand("ultimatesheepwars").setExecutor(this);
		registerExternalCommands(server);
		registerCommands(ultimateSheepWars);
	}

	/**
	 * Registers a command.
	 *
	 * @param meCommand
	 *            The command to register.
	 */
	public void registerCommand(SubCommand meCommand) {
		commands.add(meCommand);
	}

	public void showHelp(CommandSender commandSender, int page) {
		final boolean isPlayer = commandSender instanceof Player;
		final List<SubCommand> commands = getAllowedCommands(commandSender);
		if (isPlayer) {
			commandSender.sendMessage("");
			commandSender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "============" + ChatColor.RESET + " " + ChatColor.YELLOW + ChatColor.BOLD + "ULTIMATE SHEEP WARS " + ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "=============");
			commandSender.sendMessage(ChatColor.ITALIC + "Hover commands for more informations, click to suggest.");
			commandSender.sendMessage("");
		} else {
			commandSender.sendMessage(ChatColor.RED + "UltimateSheepWars help menu (page " + page + "/" + getMaxPages(commands.size()) + ")");
		}
		int from = 1;
		if (page > 1)
			from = MAX_COMMANDS * (page - 1) + 1;
		int to = MAX_COMMANDS * page;
		for (int h = from; h <= to; h++) {
			if (h > commands.size())
				break;
			final SubCommand sub = commands.get(h - 1);
			final String line = sub.getUsage() + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + sub.getDescription(SubCommand.DescriptionType.SHORT);
			if (isPlayer) {
				TextComponent lineComponent = new TextComponent(TextComponent.fromLegacyText(line));
				lineComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(sub.getDescription(SubCommand.DescriptionType.FULL))));
				lineComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, sub.getUsage()));
				final Player player = (Player) commandSender;
				player.spigot().sendMessage(lineComponent);
			} else {
				commandSender.sendMessage(line);
			}
		}
		if (commands.isEmpty()) {
			commandSender.sendMessage(ChatColor.RED + "There's no command that you're allowed to do.");
		}
		if (isPlayer)
			commandSender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "================" + ChatColor.RESET + " " + ChatColor.YELLOW + ChatColor.BOLD + "PAGE " + ChatColor.WHITE + ChatColor.BOLD + page + ChatColor.YELLOW + ChatColor.BOLD + " ON " + ChatColor.WHITE + ChatColor.BOLD + getMaxPages(commands.size()) + ChatColor.GREEN + " " + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "=================");
	}

	public void showCredits(CommandSender commandSender) {
		if (commandSender instanceof Player)
			commandSender.sendMessage("");
		
		commandSender.sendMessage("??? " + ChatColor.GRAY + "Plugin " + ChatColor.GREEN + "UltimateSheepWars v" + (UpdateManager.isUpToDate() ? "" : ChatColor.RED) + this.ultimateSheepWars.getDescription().getVersion() + ChatColor.GRAY + " by " + ChatColor.GREEN + "Royalpha" + ChatColor.GRAY + ".");

		if (commandSender instanceof Player) {
			final Player player = (Player) commandSender;
			TextComponent message1 = new TextComponent(TextComponent.fromLegacyText("??? " + ChatColor.GRAY + "Special thanks to all the following contributors : "));
			TextComponent hover = new TextComponent("\u00bb hover \u00ab");
			hover.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
			hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GRAY + "- @Royalpha (" + ChatColor.RED + "Developer" + ChatColor.GRAY + ")\n" + ChatColor.GRAY + "- @KingRider26 (" + ChatColor.RED + "Co-Developer" + ChatColor.GRAY + ")\n" + ChatColor.GRAY + "- @MathieuAR (" + ChatColor.LIGHT_PURPLE + "Test server hosting" + ChatColor.GRAY + ")\n" + ChatColor.GRAY + "- @Newen (" + ChatColor.LIGHT_PURPLE + "Tester" + ChatColor.GRAY + ")\n"+ ChatColor.GRAY + "- @6985jjorda (" + ChatColor.GOLD + "English Translation" + ChatColor.GRAY + ")\n" + ChatColor.GRAY + "- @felibouille (" + ChatColor.GOLD + "German Translation" + ChatColor.GRAY + ")\n" + ChatColor.GRAY + "- @jeussa (" + ChatColor.YELLOW + "Instant Explosion Firework Effect" + ChatColor.GRAY + ")")));
			message1.addExtra(hover);
			player.spigot().sendMessage(message1);

			TextComponent message2 = new TextComponent(TextComponent.fromLegacyText("??? " + ChatColor.GRAY + "If you encounter any issue, come and talk to us : "));
			TextComponent click = new TextComponent("\u00bb click \u00ab");
			click.setColor(net.md_5.bungee.api.ChatColor.GOLD);
			click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/nZthcPh"));
			message2.addExtra(click);
			player.spigot().sendMessage(message2);
		}

		commandSender.sendMessage("??? " + ChatColor.GRAY + "Use " + ChatColor.WHITE + "/usw 1 " + ChatColor.GRAY + "to see the " + ChatColor.WHITE + "first" + ChatColor.GRAY + " page of all available commands.");
	}

	/**
	 * Gets the max amount of pages.
	 *
	 * @return the maximum amount of pages.
	 */
	private int getMaxPages(int commandsSize) {
		int i = Math.max(1, commandsSize);
		if (i % MAX_COMMANDS == 0)
			return i / MAX_COMMANDS;
		double j = i / MAX_COMMANDS;
		int h = (int) Math.floor(j * 100) / 100;
		return h + 1;
	}
	
	private List<SubCommand> getAllowedCommands(CommandSender sender) {
		List<SubCommand> output = new ArrayList<>();
		for (SubCommand subC : this.commands) {
			boolean isAllowed = false;
			for (Permissions perm : subC.getPermissions())
				if (perm.hasPermission(sender)) {
					isAllowed = true;
					break;
				}
			if (isAllowed) {
				output.add(subC);
			}
		}
		return output;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {

		if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
			return false;
		}
		
		if (arguments == null || arguments.length == 0) {
			showCredits(sender);
			return true;
		}

		if (arguments.length == 1) {
			if (arguments[0].equalsIgnoreCase("help")) {
				showHelp(sender, 1);
				return true;
			}
			if (Utils.isInteger(arguments[0])) {
				showHelp(sender, Math.max(1, Math.min(Integer.parseInt(arguments[0]), getMaxPages(getAllowedCommands(sender).size()))));
				return true;
			}
		}

		for (SubCommand comm : commands) {
			if (comm.is(arguments[0])) {

				Iterator<Permissions> it = comm.getPermissions().iterator();
				while (true) {
					Permissions perm = Permissions.USW_ADMIN;
					if (it.hasNext()) {
						perm = it.next();
						if (perm.hasPermission(sender))
							break;
					} else {
						perm.warn(sender);
						return true;
					}
				}

				if (sender instanceof Player) {
					comm.onExePlayer((Player) sender, arguments);
				} else {
					comm.onExeConsole((ConsoleCommandSender) sender, arguments);
				}
				return true;
			}
		}
		return true;
	}

	public List<SubCommand> getCommands() {
		return commands;
	}

	private void registerCommands(SheepWarsPlugin ultimateSheepWars) {
		registerCommand(new SetLobbySubCommand(ultimateSheepWars));
		registerCommand(new AddTeamSpawnSubCommand(ultimateSheepWars));
		registerCommand(new ClearTeamSpawnsSubCommand(ultimateSheepWars));
		registerCommand(new AddBoosterSubCommand(ultimateSheepWars));
		registerCommand(new ClearBoostersSubCommand(ultimateSheepWars));
		registerCommand(new CheckSetupSubCommand(ultimateSheepWars));
		registerCommand(new SetMapDisplayNameSubCommand(ultimateSheepWars));
		registerCommand(new SetMapSheepVelocitySubCommand(ultimateSheepWars));
		registerCommand(new GoToWorldSubCommand(ultimateSheepWars));
		registerCommand(new StartGameSubCommand(ultimateSheepWars));
		registerCommand(new GiveSheepSubCommand(ultimateSheepWars));
		registerCommand(new ShowBoostersSubCommand(ultimateSheepWars));
		registerCommand(new ShowKitsSubCommand(ultimateSheepWars));
		registerCommand(new ShowSheepsSubCommand(ultimateSheepWars));
	}

	private void registerExternalCommands(Server server) {
		server.getPluginCommand("lang").setExecutor(new LangCommand(this.ultimateSheepWars));
		server.getPluginCommand("stats").setExecutor(new StatsCommand());
		server.getPluginCommand("contributor").setExecutor(new ContributorCommand(this.ultimateSheepWars));
		if (ConfigManager.getBoolean(ConfigManager.Field.ENABLE_HUB_COMMAND))
			server.getPluginCommand("hub").setExecutor(new HubCommand(this.ultimateSheepWars));
	}
}
