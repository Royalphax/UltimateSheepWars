package fr.asynchronous.sheepwars.core.sheep.sheeps;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.sheep.SheepWarsSheep;
import fr.asynchronous.sheepwars.core.util.EntityUtils;

public class SwapSheep extends SheepWarsSheep
{
    public SwapSheep() {
		super(MsgEnum.SWAP_SHEEP_NAME, DyeColor.MAGENTA, 5, false, true, 0.25f);
	}

	@Override
	public boolean onGive(Player player) {
		return true;
	}

	@Override
	public void onSpawn(Player player, Sheep bukkitSheep, Plugin plugin) {
		// Do nothing 
	}

	@Override
	public boolean onTicking(Player player, long ticks, Sheep bukkitSheep, Plugin plugin) {
		if (!bukkitSheep.hasMetadata("onGround")) {
			bukkitSheep.setMetadata("onGround", (MetadataValue)new FixedMetadataValue(plugin, (Object)true));
			final PlayerData playerData = PlayerData.getPlayerData(player);
            final TeamManager playerTeam = playerData.getTeam();
            final Location location = player.getLocation();
            int distance = 10;
            Player nearest = null;
            Location lastLocation = null;
            for (Player online : Bukkit.getOnlinePlayers())
            {
              TeamManager team = PlayerData.getPlayerData(online).getTeam();
              if ((online != player) && (team != TeamManager.SPEC) && (team != playerTeam))
              {
                int dist = (int)(lastLocation = online.getLocation()).distance(bukkitSheep.getLocation());
                if (dist < distance)
                {
                  distance = dist;
                  nearest = online;
                }
              }
            }
            if (nearest == null) {
                Message.sendMessage(player, MsgEnum.SWAP_SHEEP_ACTION_NOPLAYER);
                return true;
            }
            final Player nearestFinal = nearest;
            Sounds.playSound(player, location, Sounds.PORTAL_TRAVEL, 1f, 1f);
            Sounds.playSound(nearestFinal, lastLocation, Sounds.PORTAL_TRAVEL, 1f, 1f);
            
            new BukkitRunnable() {
                private int ticks = 80;
                
                public void run() {
                	SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.PORTAL, player.getLocation().add(0,1,0), 0.3f, 0.3f, 0.3f, 10, 0.1f);
                	SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(nearestFinal, Particles.PORTAL, nearestFinal.getLocation().add(0,1,0), 0.3f, 0.3f, 0.3f, 10, 0.1f);
                    if (this.ticks <= 0) {
                        this.cancel();
                        if (EntityUtils.isOverVoid(player) || EntityUtils.isOverVoid(nearestFinal) 
                        		|| PlayerData.getPlayerData(player).isSpectator() || PlayerData.getPlayerData(nearestFinal).isSpectator()
                        		|| player.getFallDistance() > 2.0f || nearestFinal.getFallDistance() > 2.0f)
                        {
                        	player.removeMetadata("cancel_move", plugin);
                            nearestFinal.removeMetadata("cancel_move", plugin);
                            Sounds.playSound(player, location, Sounds.WITHER_SPAWN, 1f, 2f);
                            Sounds.playSound(nearestFinal, nearestFinal.getLocation(), Sounds.WITHER_SPAWN, 1f, 2f);
                        	bukkitSheep.remove();
                        	return;
                        }
                        final Location location = player.getLocation();
                        player.setFallDistance(0.0f);
                        player.teleport(nearestFinal.getLocation());
                        nearestFinal.setFallDistance(0.0f);
                        nearestFinal.teleport(location);
                        player.removeMetadata("cancel_move", plugin);
                        nearestFinal.removeMetadata("cancel_move", plugin);
                        Message.sendMessage(player, MsgEnum.SWAP_SHEEP_ACTION_TELEPORTATION);
                        Message.sendMessage(nearestFinal, MsgEnum.SWAP_SHEEP_ACTION_TELEPORTATION);
                        bukkitSheep.remove();
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
	public void onFinish(Player player, Sheep bukkitSheep, boolean death, Plugin plugin) {
		// Do nothing
	}
}
