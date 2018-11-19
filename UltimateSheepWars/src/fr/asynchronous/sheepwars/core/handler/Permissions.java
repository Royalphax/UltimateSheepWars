package fr.asynchronous.sheepwars.core.handler;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public enum Permissions {
	
	USW_ADMIN("usw.admin", "sheepwars.admin"),
	USW_START("usw.startgame", "usw.admin", "sheepwars.admin"),
	USW_BYPASS_LOGIN("usw.login.bypass", "sheepwars.vip", "usw.admin", "sheepwars.admin"),
	USW_BYPASS_TEAMS("usw.teams.bypass", "sheepwars.teams.bypass"),
	USW_DEVELOPER("usw.developer", "usw.admin", "sheepwars.admin"),
	USW_GIVE_SELF("usw.give.self", "usw.give", "usw.admin", "sheepwars.admin"),
	USW_GIVE_OTHER("usw.give.other", "usw.give", "usw.admin", "sheepwars.admin"),
	USW_GIVE_ALL("usw.give.all", "usw.give", "usw.admin", "sheepwars.admin");
	
	public String[] perms;

	private Permissions(String... perms) {
		this.perms = perms;
	}
	
	public boolean hasPermission(Player player) {
		return hasPermission(player, false);
	}
	
	public boolean hasPermission(Player player, boolean warn) {
		boolean output = false;
		for (String perm : this.perms) {
			if (player.hasPermission(perm)) {
				output = true;
				break;
			}
		}
		if (!output && warn)
			warn(player);
		return output;
	}
	
	public static void warn(Player player) {
		player.sendMessage(ChatColor.RED + "You don't have the permission to do that.");
	}
}
