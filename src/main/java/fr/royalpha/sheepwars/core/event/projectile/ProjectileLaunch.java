package fr.royalpha.sheepwars.core.event.projectile;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;

public class ProjectileLaunch extends UltimateSheepWarsEventListener
{
    public ProjectileLaunch(final SheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onProjectileLaunch(final ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {
        	Player player = (Player) event.getEntity().getShooter();
        	boolean isInvisible = false;
        	for (PotionEffect potioneffect : player.getActivePotionEffects())
        		if (potioneffect.getType() == PotionEffectType.INVISIBILITY)
        			isInvisible = true;
        	event.setCancelled(isInvisible);
        }
    }
}
