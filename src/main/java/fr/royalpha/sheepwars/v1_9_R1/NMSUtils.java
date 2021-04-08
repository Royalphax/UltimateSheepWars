package fr.royalpha.sheepwars.v1_9_R1;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import fr.royalpha.sheepwars.core.manager.ConfigManager;
import fr.royalpha.sheepwars.core.util.ReflectionUtils;
import fr.royalpha.sheepwars.core.version.INMSUtils;
import net.minecraft.server.v1_9_R1.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_9_R1.CraftServer;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.royalpha.sheepwars.v1_9_R1.entity.EntityCancelMove;
import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.NBTTagList;
import net.minecraft.server.v1_9_R1.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_9_R1.WorldBorder;

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
		net.minecraft.server.v1_9_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
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
	public void setHealth(final LivingEntity ent, final Double maxHealth) {
		ent.setMaxHealth(maxHealth);
		if (ent instanceof Player)
			((Player) ent).setHealthScaled(false);
		ent.setHealth(maxHealth);
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
	public void updateNMSServerMOTD(String MOTD) {
		MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
		server.setMotd(MOTD);
	}

	@Override
	public ItemMeta setUnbreakable(final ItemMeta meta, final boolean bool) {
		try {
			Method spigot = ReflectionUtils.getMethod(Class.forName("org.bukkit.inventory.meta.ItemMeta"), "spigot");
			Method setUnbreakable = ReflectionUtils.getMethod(Class.forName("org.bukkit.inventory.meta.ItemMeta$Spigot"), "setUnbreakable", boolean.class);
			setUnbreakable.invoke(spigot.invoke(meta), bool);
		} catch (ClassCastException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			if (ConfigManager.getBoolean(ConfigManager.Field.ALLOW_DEBUG))
				System.err.println("An issue occured while trying to set unbreakable item (" + e.getMessage() + ")");
		}
		return meta;
	}
}
