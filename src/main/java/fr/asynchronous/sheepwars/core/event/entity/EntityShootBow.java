package fr.asynchronous.sheepwars.core.event.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.MinecraftVersion;

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
