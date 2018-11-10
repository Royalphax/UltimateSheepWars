package fr.asynchronous.sheepwars.v1_12_R1;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;
import org.bukkit.material.MaterialData;

import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.version.INMSUtils;
import fr.asynchronous.sheepwars.v1_12_R1.util.SpecialMessage;
import net.minecraft.server.v1_12_R1.ChatClickable.EnumClickAction;
import net.minecraft.server.v1_12_R1.ChatHoverable;
import net.minecraft.server.v1_12_R1.ChatMessage;
import net.minecraft.server.v1_12_R1.ChatModifier;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityPlayer;

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
	public void displayAvailableLanguages(Player player) {
		for (Language langs : Language.getLanguages()) {
			SpecialMessage msg = new SpecialMessage(ChatColor.YELLOW + "- " + langs.getName() + " " + ChatColor.DARK_GRAY + "[");
			msg.setClick(ChatColor.GREEN + "➔", EnumClickAction.RUN_COMMAND, "/lang " + langs.getLocale().replace(".yml", "")).setChatModifier(new ChatModifier().setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatMessage(ChatColor.YELLOW + "Click to select", new Object[0]))));
			msg.append(ChatColor.DARK_GRAY + "]");
			msg.sendToPlayer(player);
		}
	}

	@Override
	public void displayRedScreen(Player player, boolean activate) {
		// Do nothing
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
