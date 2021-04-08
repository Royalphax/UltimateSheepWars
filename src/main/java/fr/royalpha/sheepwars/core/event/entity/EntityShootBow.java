package fr.royalpha.sheepwars.core.event.entity;

import fr.royalpha.sheepwars.core.handler.MinecraftVersion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;

public class EntityShootBow extends UltimateSheepWarsEventListener
{
    public EntityShootBow(final SheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onEntityShootBowEvent(final EntityShootBowEvent event) {
    	// Empeche le bug en 1.8 de la fleche rebondissante !
    	if (SheepWarsPlugin.getVersionManager().getVersion().equals(MinecraftVersion.v1_8_R3))
    		event.getProjectile().setVelocity(event.getProjectile().getVelocity());
    }
}
