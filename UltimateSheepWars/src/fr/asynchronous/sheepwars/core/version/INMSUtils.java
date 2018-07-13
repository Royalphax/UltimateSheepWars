package fr.asynchronous.sheepwars.core.version;

import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;

public interface INMSUtils {

	public void setKiller(final Entity entity, final Entity killer);
	
	public Block getBoosterBlock(final Arrow arrow, final UltimateSheepWarsPlugin plugin);
	
	public ItemStack setIllegallyGlowing(final ItemStack item, boolean activate);
	
	public void setUnbreakable(final ItemMeta meta, final boolean bool);
	
	public void setHealth(final LivingEntity ent, final Double health);
	
	public void displayAvailableLanguages(final Player player);
	
	public void displayRedScreen(final Player player, final boolean activate);
	
	public MaterialData getDye(DyeColor color);
	
}
