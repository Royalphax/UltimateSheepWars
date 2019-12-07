package fr.asynchronous.sheepwars.v1_8_R3;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.asynchronous.sheepwars.core.version.INMSUtils;
import fr.asynchronous.sheepwars.v1_8_R3.entity.EntityCancelMove;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_8_R3.WorldBorder;

public class NMSUtils implements INMSUtils {

	@Override
	public void setKiller(Entity entity, Entity killer) {
		EntityPlayer entityKiller = ((CraftPlayer) killer).getHandle();
		((CraftPlayer) entity).getHandle().killer = (EntityHuman) entityKiller;
	}

	@Override
	public void setItemInHand(final ItemStack item, final Player player) {
		player.getInventory().setItemInHand(item);
	}

	@Override
	public ItemStack setIllegallyGlowing(ItemStack item, boolean activate) {
		net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
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
		WorldBorder border = new WorldBorder();
		border.setWarningDistance(activate ? Integer.MAX_VALUE : 0);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldBorder(border, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_BLOCKS));
	}

	@Override
	public ItemMeta setUnbreakable(final ItemMeta meta, final boolean bool) {
		meta.spigot().setUnbreakable(bool);
		return meta;
	}

	@Override
	public void setHealth(final LivingEntity ent, final Double maxHealth) {
		ent.setMaxHealth(maxHealth);
		if (ent instanceof Player)
			((Player) ent).setHealthScaled(false);
		ent.setHealth(maxHealth);
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemStack color(ItemStack current, DyeColor color) {
		ItemStack i = current.clone();
		if (i.getType() == Material.INK_SACK) {
			i.setDurability(color.getDyeData());
		} else if (i.getType() == Material.WOOL) {
			i.setDurability(color.getWoolData());
		} else {
			i.setDurability(color.getData());
		}
		return i;
	}
	
	public static Map<Player, EntityCancelMove> cancelMoveMap = new HashMap<>();

	@Override
	public void cancelMove(Player player, boolean bool) {
		if (bool) {
			if (!cancelMoveMap.containsKey(player)) {
				final EntityCancelMove entity = new EntityCancelMove(player);
				entity.spawnClientEntity();
				entity.updateClientEntityLocation();
				entity.rideClientEntity();
				cancelMoveMap.put(player, entity);
			}
		} else {
			if (cancelMoveMap.containsKey(player)) {
				final EntityCancelMove entity = cancelMoveMap.get(player);
				entity.unrideClientEntity();
				entity.destroyClientEntity();
				cancelMoveMap.remove(player);
			}
		}
	}
}
