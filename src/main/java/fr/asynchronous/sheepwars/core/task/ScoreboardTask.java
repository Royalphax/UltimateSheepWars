package fr.asynchronous.sheepwars.core.task;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.api.Language;

public class ScoreboardTask extends BukkitRunnable
{
	private String[] list;
    private String defaut;
    private Integer round;
    
    public ScoreboardTask(String ip, SheepWarsPlugin plugin) {
    	this.round = -1;
        this.defaut = ip;
        this.runTaskTimer(plugin, 0, 1);
    }
    
    public void run() {
    	final String news = getPosition();
    	
		for (Language langs : Language.getLanguages())
			langs.getScoreboardWrapper().setLine(1, news, true);
		
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
