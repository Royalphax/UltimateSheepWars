package fr.asynchronous.sheepwars.core.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Permissions;
import fr.asynchronous.sheepwars.core.util.JustifyUtils;
import net.md_5.bungee.api.ChatColor;

public abstract class SubCommand {

	public static final String PREFIX = ChatColor.AQUA + "" + ChatColor.BOLD + "USW " + ChatColor.WHITE + "➢ " + ChatColor.RESET;

	private String[] aliases;
	private String shortDescription, longDescription, usage;
	private List<Permissions> permissions;
	private SheepWarsPlugin ultimateSheepWars;

	public SubCommand(String shortDescription, String longDescription, String usage, Permissions permission, SheepWarsPlugin ultimateSheepWars, String... aliases) {
		this.aliases = aliases;
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
		this.permissions = new ArrayList<>();
		this.permissions.add(permission);
		this.usage = usage;
		this.ultimateSheepWars = ultimateSheepWars;
	}

	public SubCommand(String shortDescription, String longDescription, String usage, List<Permissions> permissions, SheepWarsPlugin ultimateSheepWars, String... aliases) {
		this.aliases = aliases;
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
		this.permissions = permissions;
		this.usage = usage;
		this.ultimateSheepWars = ultimateSheepWars;
	}

	public boolean is(String arg) {
		return Arrays.asList(aliases).contains(arg.toLowerCase());
	}

	public String getUsage() {
		return usage;
	}

	public String getDescription(DescriptionType type) {
		switch (type) {
			case SHORT :
				return this.shortDescription;
			case LONG :
				return this.longDescription;
			case FULL :
				List<String> justified = JustifyUtils.fullJustify(this.longDescription.split(" "), 40);
				StringBuilder finalText = new StringBuilder();
				for (String just : justified)
					finalText.append(just + "\n" + ChatColor.GRAY);
				return ChatColor.WHITE + "Description :\n∙ " + ChatColor.GRAY + finalText.toString() + "\n" + ChatColor.WHITE + "Permission(s) required :\n∙ " + ChatColor.GRAY + this.getPermissions().toString() + "\n\n" + ChatColor.WHITE + "Aliase(s) :\n∙ " + ChatColor.GRAY + makeList(this.aliases).toString();
		}
		return "null";
	}

	public static enum DescriptionType {
		SHORT(),
		LONG(),
		FULL();
	}

	public List<Permissions> getPermissions() {
		return permissions;
	}

	protected abstract void onExePlayer(Player sender, String... args);

	protected abstract void onExeConsole(ConsoleCommandSender sender, String... args);

	protected void notAllowed(CommandSender commandSender) {
		if (commandSender instanceof Player) {
			commandSender.sendMessage(ChatColor.RED + "This command is not allowed from minecraft client.");
		} else {
			commandSender.sendMessage(ChatColor.RED + "This command is not allowed from console.");
		}
	}
	
	protected void usage(CommandSender commandSender) {
		commandSender.sendMessage(ChatColor.RED + "Usage: " + this.usage);
	}

	public SheepWarsPlugin getUltimateSheepWarsInstance() {
		return ultimateSheepWars;
	}

	@SuppressWarnings("unchecked")
	protected static <T> List<T> makeList(T... params) {
		List<T> output = new ArrayList<>();
		for (T param : params)
			output.add(param);
		return output;
	}
}
