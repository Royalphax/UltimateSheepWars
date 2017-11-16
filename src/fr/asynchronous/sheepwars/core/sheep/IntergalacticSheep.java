package fr.asynchronous.sheepwars.core.sheep;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Sheeps;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.util.EntityUtils;
import fr.asynchronous.sheepwars.core.util.MathUtils;

public class IntergalacticSheep implements Sheeps.SheepAction
{
    @Override
    public void onSpawn(final Player player, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
    	letsNight(sheep.getWorld(), plugin);
    	for (Player online : Bukkit.getOnlinePlayers())
    	{
    		String lang = PlayerData.getPlayerData(plugin, online).getLocale();
    		online.sendMessage(plugin.PREFIX + Language.getMessageByLanguage(lang, Message.INTERGALACTIC_SHEEP_LAUNCHED).replace("%PLAYER%", TeamManager.getPlayerTeam(player).getColor() + player.getName()).replace("%SHEEP%", Message.getDecoration() + " " + Language.getMessageByLanguage(lang, Message.INTERGALACTIC_SHEEP_NAME) + " " + Message.getDecoration()));
    	}
    	new BukkitRunnable()
    	{
    		public void run()
    		{
    			letsDay(sheep.getWorld(), plugin);
    		}
    	}.runTaskLater(plugin, 20*15);
    }
    
    @Override
    public boolean onTicking(final Player player, final long ticks, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
        if (!sheep.hasMetadata("onGround")) {
        	sheep.setMetadata("onGround", (MetadataValue)new FixedMetadataValue(plugin, (Object)true));
            new BukkitRunnable() {
                private int seconds = MathUtils.random(4, 12) + 1;
                private Location location = sheep.getLocation();
                public void run() {
                    if (this.seconds == 0) {
                        this.cancel();
                        sheep.remove();
                        return;
                    }
                    if (this.seconds > 2) {
                        final Fireball fireball = plugin.versionManager.getCustomEntities().spawnFireball(location, player);
                        fireball.setBounce(false);
                        fireball.setIsIncendiary(true);
                        Sounds.playSoundAll(fireball.getLocation(), Sounds.GHAST_FIREBALL, 5.0f, 1.5f);
                        EntityUtils.moveToward((org.bukkit.entity.Entity)fireball, location.clone().add((double)MathUtils.random(-5, 5), 0.0, (double)MathUtils.random(-5, 5)), 0.7);
                    }
                    --this.seconds;
                }
            }.runTaskTimer(plugin, 0L, 20L);
        }
        plugin.versionManager.getParticleFactory().playParticles(Particles.FLAME, sheep.getLocation().add(0,1,0), 0.3f, 0.3f, 0.3f, 1, 0.05f);
        return false;
    }
    
    @Override
    public void onFinish(final Player player, final org.bukkit.entity.Sheep sheep, final boolean death, final UltimateSheepWarsPlugin plugin) {
    }
    
    public void letsNight(final World world, UltimateSheepWarsPlugin plugin)
    {
    	Sheeps.usingIntergalacticSheep(true);
    	Sounds.playSoundAll(null, Sounds.WITHER_SPAWN, 5.0f, 0.5f);
    	new BukkitRunnable()
    	{
    		long i = 6000;
    		public void run()
    		{
    			i = i + 200;
    			world.setTime(i);
    			if (i >= 18000)
    			{
    				cancel();
    			}
    		}
    	}.runTaskTimer(plugin, 0, 0);
    }
    
    public void letsDay(final World world, UltimateSheepWarsPlugin plugin)
    {
    	new BukkitRunnable()
    	{
    		long i = 18000;
    		public void run()
    		{
    			i = i + 100;
    			world.setTime(i);
    			if (i >= 30000)
    			{
    				cancel();
    				Sheeps.usingIntergalacticSheep(false);
    			}
    		}
    	}.runTaskTimer(plugin, 0, 0);
    }
}
