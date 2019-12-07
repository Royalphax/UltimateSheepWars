package fr.asynchronous.sheepwars.v1_11_R1;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.asynchronous.sheepwars.core.version.INMSUtils;
import fr.asynchronous.sheepwars.v1_11_R1.entity.EntityCancelMove;
import net.minecraft.server.v1_11_R1.EntityHuman;
import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_11_R1.WorldBorder;

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
		ItemStack copy = item.clone();
		ItemMeta meta = copy.getItemMeta();
		if (activate) {
			meta.addEnchant(Enchantment.LURE, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		} else {
			meta.removeEnchant(Enchantment.LURE);
			meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
        copy.setItemMeta(meta);
        return copy;
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

	@Override
	public ItemMeta setUnbreakable(final ItemMeta meta, final boolean bool) {
		meta.spigot().setUnbreakable(bool);
		return meta;
	}
}
