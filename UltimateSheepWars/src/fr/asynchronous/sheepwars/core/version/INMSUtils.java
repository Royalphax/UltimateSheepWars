package fr.asynchronous.sheepwars.core.version;

import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;

public interface INMSUtils {

	public void setKiller(final Entity entity, final Entity killer);
	
	public Block getBoosterBlock(final Arrow arrow, final UltimateSheepWarsPlugin plugin);
	
	public ItemStack setIllegallyGlowing(final ItemStack item);
	
	public void setUnbreakable(final ItemMeta meta, final boolean bool);
	
	public void setMaxHealth(final LivingEntity ent, final Double maxHealth);
	
	public void displayAvailableLanguages(final Player player);
	
	public void displayRedScreen(final Player player, final boolean activate);
	
}
