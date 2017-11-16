package fr.asynchronous.sheepwars.core.task;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.message.Language;

public class ScoreboardTask extends BukkitRunnable
{
	private String[] list;
    private String news;
    private String defaut;
    private Integer round;
    private UltimateSheepWarsPlugin plugin;
    
    public ScoreboardTask(String ip, UltimateSheepWarsPlugin plugin) {
    	this.round = -1;
        this.defaut = ip;
        this.plugin = plugin;
        this.runTaskTimer(plugin, 0, 1);
    }
    
    public void run() {
    	this.news = getPosition();
		for (Language langs : Language.getLanguages())
			langs.getScoreboardWrapper().setLine(1, this.news, true);
		for (Player online : Bukkit.getOnlinePlayers())
			GameState.updateTabInfo(online, this.news, this.plugin);
		this.round++;
    	if (this.round > this.defaut.length()+1) {
    		this.round = -31;
    	}
    }
    
    private String getPosition() {
    	if (this.round < -1) return ChatColor.YELLOW + this.defaut;
    	if (this.list == null)
    		this.list = this.defaut.split("");
    	
    	String prefix = ChatColor.RED.toString();
    	String middle = ChatColor.GOLD.toString();
    	String suffix = ChatColor.YELLOW.toString();
    	
    	StringBuilder endBuilder = new StringBuilder(ChatColor.YELLOW+"");
    	
        for (int i = 0; i < this.list.length; i++)
        	endBuilder.append((this.round-1 == i ? prefix : "") + (this.round == i ? middle : "") + (this.round+1 == i ? suffix : "") + list[i].toString());
		
        return endBuilder.toString().trim();
    }
}
