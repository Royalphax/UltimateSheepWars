package fr.asynchronous.sheepwars.core.sheep;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Sheeps.SheepAction;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.util.EntityUtils;

public class SwapSheep implements SheepAction
{
    @Override
    public void onSpawn(final Player player, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
    }
    
    @Override
    public boolean onTicking(final Player player, final long ticks, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
        if (!sheep.hasMetadata("onGround")) {
            sheep.setMetadata("onGround", (MetadataValue)new FixedMetadataValue(plugin, (Object)true));
            final TeamManager playerTeam = TeamManager.getPlayerTeam(player);
            final Location location = player.getLocation();
            int distance = 10;
            Player nearest = null;
            Location lastLocation = null;
            for (Player online : Bukkit.getOnlinePlayers())
            {
              TeamManager team = TeamManager.getPlayerTeam(online);
              if ((online != player) && (team != TeamManager.SPEC) && (team != playerTeam))
              {
                int dist = (int)(lastLocation = online.getLocation()).distance(sheep.getLocation());
                if (dist < distance)
                {
                  distance = dist;
                  nearest = online;
                }
              }
            }
            if (nearest == null) {
                player.sendMessage(String.valueOf(plugin.PREFIX) + ChatColor.RED + Language.getMessageByLanguage(PlayerData.getPlayerData(plugin, player).getLocale(), Message.SWAP_SHEEP_ACTION_NOPLAYER));
                return true;
            }
            final Player nearestFinal = nearest;
            Sounds.playSound(player, location, Sounds.PORTAL_TRAVEL, 1f, 1f);
            Sounds.playSound(nearestFinal, lastLocation, Sounds.PORTAL_TRAVEL, 1f, 1f);
            
            new BukkitRunnable() {
                private int ticks = 80;
                
                public void run() {
                	plugin.versionManager.getParticleFactory().playParticles(player, Particles.PORTAL, player.getLocation().add(0,1,0), 0.3f, 0.3f, 0.3f, 10, 0.1f);
                	plugin.versionManager.getParticleFactory().playParticles(nearestFinal, Particles.PORTAL, nearestFinal.getLocation().add(0,1,0), 0.3f, 0.3f, 0.3f, 10, 0.1f);
                    if (this.ticks <= 0) {
                        this.cancel();
                        if (EntityUtils.isOverVoid(player) || EntityUtils.isOverVoid(nearestFinal) 
                        		|| TeamManager.getPlayerTeam(player) == TeamManager.SPEC || TeamManager.getPlayerTeam(nearestFinal) == TeamManager.SPEC
                        		|| player.getFallDistance() > 2.0f || nearestFinal.getFallDistance() > 2.0f)
                        {
                        	player.removeMetadata("cancel_move", plugin);
                            nearestFinal.removeMetadata("cancel_move", plugin);
                            Sounds.playSound(player, location, Sounds.WITHER_SPAWN, 1f, 2f);
                            Sounds.playSound(nearestFinal, nearestFinal.getLocation(), Sounds.WITHER_SPAWN, 1f, 2f);
                        	sheep.remove();
                        	return;
                        }
                        final Location location = player.getLocation();
                        player.setFallDistance(0.0f);
                        player.teleport(nearestFinal.getLocation());
                        nearestFinal.setFallDistance(0.0f);
                        nearestFinal.teleport(location);
                        player.removeMetadata("cancel_move", plugin);
                        nearestFinal.removeMetadata("cancel_move", plugin);
                        player.sendMessage(plugin.PREFIX + ChatColor.RED + Language.getMessageByLanguage(PlayerData.getPlayerData(plugin, player).getLocale(), Message.SWAP_SHEEP_ACTION_TELEPORTATION));
                        nearestFinal.sendMessage(plugin.PREFIX + ChatColor.RED + Language.getMessageByLanguage(PlayerData.getPlayerData(plugin, nearestFinal).getLocale(), Message.SWAP_SHEEP_ACTION_TELEPORTATION));
                        sheep.remove();
                        return;
                    } else if (this.ticks == 30) {
                    	player.setMetadata("cancel_move", new FixedMetadataValue(plugin, true));
                        nearestFinal.setMetadata("cancel_move", new FixedMetadataValue(plugin, true));
                    }
                    --this.ticks;
                }
            }.runTaskTimer(plugin, 0L, 0L);
        }
        return false;
    }
    
    @Override
    public void onFinish(final Player player, final org.bukkit.entity.Sheep sheep, final boolean death, final UltimateSheepWarsPlugin plugin) {
    }
}
