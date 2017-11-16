package fr.asynchronous.sheepwars.core.manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;

public class RewardsManager {
	
	private UltimateSheepWarsPlugin instance;
	
	public RewardsManager(UltimateSheepWarsPlugin instance) {
		this.instance = instance;
		FileConfiguration config = instance.getConfig();
        for (Events ev : Events.values()) {
        	String key = ev.getConfigPath();
        	Boolean doCommand = config.getBoolean("rewards." + key + ".do-command");
        	ev.initValues(config.getDouble("rewards." + key + ".vault-reward"), doCommand);
        	if (doCommand)
        		for (final Object com : config.getList("rewards." + key + ".commands"))
                    ev.addCommand(com.toString());
        }
	}
	
	public void rewardPlayer(Events event, Player player) {
		if (!event.commands.isEmpty())
			for (String comm : event.commands)
				this.instance.getServer().dispatchCommand(Bukkit.getConsoleSender(), comm.replaceAll("%PLAYER%", player.getName()));
		try {
			this.instance.ECONOMY_PROVIDER.depositPlayer(player, event.vaultReward);
		} catch (NoClassDefFoundError | NullPointerException e) {
			// Do nothing
		}
	}
	
	public enum Events {
		ON_KILL("on-kill"),
		ON_DEATH("on-death"),
		ON_WIN("on-win"),
		ON_LOOSE("on-lose");
		
		private String configPath;
		private Double vaultReward;
		private Boolean doCommand;
		private List<String> commands;
		
		private Events(String configPath) {
			this.configPath = configPath;
			this.vaultReward = 0.0;
			this.doCommand = false;
			this.commands = new ArrayList<>();
		}
		
		public String getConfigPath() {
			return this.configPath;
		}
		
		public Double getVaultReward() {
			return this.vaultReward;
		}
		
		public Boolean areCommandsEnabled() {
			return this.doCommand;
		}
		
		public List<String> getCommands() {
			return this.commands;
		}
		
		public void initValues(Double vaultReward, Boolean doCommand) {
			this.vaultReward = vaultReward;
			this.doCommand = doCommand;
		}
		
		public void addCommand(String comm) {
			this.commands.add(comm);
		}
	}
}
