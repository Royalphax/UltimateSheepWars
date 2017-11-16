package fr.asynchronous.sheepwars.core.event.projectile;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.BoosterManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.util.BlockUtils;
import fr.asynchronous.sheepwars.core.util.MathUtils;

public class ProjectileHit extends UltimateSheepWarsEventListener
{
    public ProjectileHit(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onProjectileHit(final ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            final Arrow arrow = (Arrow)event.getEntity();
            if (arrow.getShooter() instanceof Player) {
                final Player player = (Player)arrow.getShooter();
                new BukkitRunnable() {
					public void run() {
                    	Block block = UltimateSheepWarsPlugin.getVersionManager().getNMSUtils().getBoosterBlock(arrow, plugin);
                        if (block != null)
                        {
                        	final TeamManager team = TeamManager.getPlayerTeam(player);
                            if (team != null) {
                                block.setType(Material.AIR);
                                if (plugin.GAME_TASK.boosterCountdown > plugin.BOOSTER_INTERVAL)
                                	plugin.GAME_TASK.boosterCountdown = plugin.BOOSTER_INTERVAL;
                                Sounds.playSoundAll(block.getLocation(), Sounds.CHICKEN_EGG_POP, 2f, 2f);
                                final BoosterManager booster = BoosterManager.values()[MathUtils.random.nextInt(BoosterManager.values().length)];
                                for (Player online : Bukkit.getOnlinePlayers())
                                {
                                	String lang = PlayerData.getPlayerData(plugin, online).getLocale();
                                	online.sendMessage((String.valueOf(plugin.PREFIX)) + Language.getMessageByLanguage(lang, Message.BOOSTER_ACTION).replaceAll("%PLAYER%", team.getColor() + player.getName()).replaceAll("%BOOSTER%", booster.getName(online)));
                                }
                                BoosterManager.activateRandomBooster(player, plugin).getAction().onStart(player, team, booster);
                            }
                         }
                    }
                }.runTaskLater(this.plugin, 1L);
            }
        } 
        if (this.plugin.CHRISTMAS_MODE && event.getEntity() instanceof Snowball)
        {
        	Block block = event.getEntity().getLocation().getBlock();
			Block top = block.getRelative(BlockFace.UP);
			if (block.getType() == Material.AIR)
			{
				if (block.getRelative(BlockFace.DOWN).getType() != Material.AIR)
				{
					top = block;
				} else {
					return;
				}
			}

            if (MathUtils.randomBoolean() && (top.getType() == Material.AIR || top.getType() == Material.SNOW) && BlockUtils.fullSolid(top.getRelative(BlockFace.DOWN))) {
            	if (top.getType() != Material.SNOW) {
            		top.setType(Material.SNOW);
                    top.setData((byte)0);
                }
                else {
                    final byte data = top.getData();
                    if (data < 7) {
                        top.setData((byte)(data + 1));
                    }
                }
            }
        }
    }
}
