package fr.asynchronous.sheepwars.v1_9_R2;

import org.bukkit.DyeColor;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;
import org.bukkit.material.MaterialData;

import fr.asynchronous.sheepwars.core.version.INMSUtils;
import net.minecraft.server.v1_9_R2.EntityHuman;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.NBTTagCompound;
import net.minecraft.server.v1_9_R2.NBTTagList;
import net.minecraft.server.v1_9_R2.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_9_R2.WorldBorder;

public class NMSUtils implements INMSUtils {
	
	@Override
	public void setKiller(Entity entity, Entity killer) {
		EntityPlayer entityKiller = ((CraftPlayer) killer).getHandle();
		((CraftPlayer) entity).getHandle().killer = (EntityHuman) entityKiller;
	}

	@Override
	public void setItemInHand(final ItemStack item, final Player player) {
		player.getInventory().setItemInMainHand(item);
	}

	@Override
	public ItemStack setIllegallyGlowing(ItemStack item, boolean activate) {
		net.minecraft.server.v1_9_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = null;
		if (!nmsStack.hasTag()) {
			tag = new NBTTagCompound();
			nmsStack.setTag(tag);
		}
		if (tag == null)
			tag = nmsStack.getTag();
		if (activate) {
			NBTTagList ench = new NBTTagList();
			tag.set("ench", ench);
		} else {
			tag.remove("ench");
		}
		nmsStack.setTag(tag);

		return CraftItemStack.asCraftMirror(nmsStack);
	}

	@Override
	public void displayRedScreen(Player player, boolean activate) {
		if (activate) {
			WorldBorder w = new WorldBorder();
			w.setSize(1.0D);
			w.setCenter(player.getLocation().getX() + 10000.0D, player.getLocation().getZ() + 10000.0D);
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldBorder(w, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE));
		} else {
			WorldBorder ww = new WorldBorder();
			ww.setSize(3.0E7D);
			ww.setCenter(player.getLocation().getX(), player.getLocation().getZ());
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldBorder(ww, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE));
		}
	}

	@Override
	public void setUnbreakable(final ItemMeta meta, final boolean bool) {
		meta.spigot().setUnbreakable(bool);
	}

	@Override
	public void setHealth(final LivingEntity ent, final Double maxHealth) {
		ent.setMaxHealth(maxHealth);
		if (ent instanceof Player)
			((Player) ent).setHealthScaled(false);
		ent.setHealth(maxHealth);
	}

	@Override
	public MaterialData getDye(DyeColor color) {
		return new Dye(color);
	}
}
