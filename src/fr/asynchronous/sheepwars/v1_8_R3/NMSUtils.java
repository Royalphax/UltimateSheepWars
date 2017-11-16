package fr.asynchronous.sheepwars.v1_8_R3;

import java.lang.reflect.Field;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArrow;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.util.Utils;
import fr.asynchronous.sheepwars.core.version.INMSUtils;
import fr.asynchronous.sheepwars.v1_8_R3.util.SpecialMessage;
import net.minecraft.server.v1_8_R3.ChatClickable.EnumClickAction;
import net.minecraft.server.v1_8_R3.ChatHoverable;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.ChatModifier;
import net.minecraft.server.v1_8_R3.EntityArrow;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_8_R3.WorldBorder;

public class NMSUtils implements INMSUtils {

	@Override
	public void setKiller(final Entity entity, final Entity killer) {
		EntityPlayer entityKiller = ((CraftPlayer)killer).getHandle();
		((CraftPlayer)entity).getHandle().killer = (EntityHuman)entityKiller;
	}

	@Override
	public Block getBoosterBlock(final Arrow arrow, final UltimateSheepWarsPlugin plugin) {
		try {
        	final EntityArrow entityArrow = ((CraftArrow)arrow).getHandle();
            final Field fieldX = EntityArrow.class.getDeclaredField("d");
            final Field fieldY = EntityArrow.class.getDeclaredField("e");
            final Field fieldZ = EntityArrow.class.getDeclaredField("f");
            fieldX.setAccessible(true);
            fieldY.setAccessible(true);
            fieldZ.setAccessible(true);
            final int x = fieldX.getInt(entityArrow);
            final int y = fieldY.getInt(entityArrow);
            final int z = fieldZ.getInt(entityArrow);
            final Block block = arrow.getWorld().getBlockAt(x, y, z);
            entityArrow.die();
            if (block.getType() == Material.WOOL && plugin.GAME_TASK.isBooster(block.getLocation())) {
            	return block;
            }
        }
        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            Utils.registerException(e, true);
        }
		return null;
	}

	@Override
	public ItemStack setIllegallyGlowing(final ItemStack item) {
		net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
	    NBTTagCompound tag = null;
	    if (!nmsStack.hasTag()) {
	      tag = new NBTTagCompound();
	      nmsStack.setTag(tag);
	    }
	    if (tag == null) tag = nmsStack.getTag();
	    NBTTagList ench = new NBTTagList();
	    tag.set("ench", ench);
	    nmsStack.setTag(tag);

	    return CraftItemStack.asCraftMirror(nmsStack);
	}

	@Override
	public void displayAvailableLanguages(final Player player) {
		for (Language langs : Language.getLanguages())
    	{
    		SpecialMessage msg = new SpecialMessage(ChatColor.YELLOW + "- " + langs.getName() + " " + ChatColor.DARK_GRAY + "[");
    		msg.setClick(ChatColor.GREEN + "➔", EnumClickAction.RUN_COMMAND, "/lang " + langs.getLocale().replace(".yml", "")).setChatModifier(new ChatModifier().setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatMessage(ChatColor.YELLOW +"Click to select", new Object[0]))));
    		msg.append(ChatColor.DARK_GRAY + "]");
    		msg.sendToPlayer(player);
    	}
	}
	
	@Override
	public void displayRedScreen(final Player player, final boolean activate) {
		if (activate) {
			WorldBorder w = new WorldBorder();
	        w.setSize(1.0D);
	        w.setCenter(player.getLocation().getX() + 10000.0D, player.getLocation().getZ() + 10000.0D);
	        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(
	          new PacketPlayOutWorldBorder(w, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE));
		} else {
			WorldBorder ww = new WorldBorder();
			ww.setSize(3.0E7D);
			ww.setCenter(player.getLocation().getX(), player.getLocation().getZ());
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(
					new PacketPlayOutWorldBorder(ww, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE));
		}
	}

	@Override
	public void setUnbreakable(final ItemMeta meta, final boolean bool) {
		meta.spigot().setUnbreakable(bool);
	}

	@Override
	public void setMaxHealth(final LivingEntity ent, final Double maxHealth) {
		ent.setMaxHealth(maxHealth);
	}
}
