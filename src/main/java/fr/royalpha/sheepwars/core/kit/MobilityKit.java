package fr.royalpha.sheepwars.core.kit;

import fr.royalpha.sheepwars.core.legacy.LegacyMaterial;
import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.api.SheepWarsTeam;
import fr.royalpha.sheepwars.api.util.ItemBuilder;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.royalpha.sheepwars.api.SheepWarsKit;

public class MobilityKit extends SheepWarsKit {

	public static final Integer PROTECTION_FALL = 2;

	public MobilityKit() {
		super(5, Message.Messages.KIT_MOBILITY_NAME, new ItemBuilder(Material.LEATHER_BOOTS), new MobilityKitLevel());
	}

	public static class MobilityKitLevel extends SheepWarsKitLevel {

		public MobilityKitLevel() {
			super(Message.Messages.KIT_MOBILITY_DESCRIPTION, "sheepwars.kit.mobility", 10, 10);
		}

		@Override
		public boolean onEquip(Player player) {
			final PlayerData data = PlayerData.getPlayerData(player);
			SheepWarsTeam team = data.getTeam();
			Color color = team.getLeatherColor();

			player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setLeatherArmorColor(color).addEnchant(Enchantment.PROTECTION_PROJECTILE, 2).setUnbreakable().toItemStack());
			player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherArmorColor(color).addEnchant(Enchantment.PROTECTION_PROJECTILE, 2).setUnbreakable().toItemStack());
			player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).setLeatherArmorColor(color).addEnchant(Enchantment.PROTECTION_PROJECTILE, 2).setUnbreakable().toItemStack());
			player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setLeatherArmorColor(color).addEnchant(Enchantment.PROTECTION_PROJECTILE, 2).addEnchant(Enchantment.PROTECTION_FALL, PROTECTION_FALL).setUnbreakable().toItemStack());
			player.getInventory().setItem(8, new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherArmorColor(color).setName(team.getColor() + "" + ChatColor.BOLD + team.getDisplayName(player)).setUnbreakable().toItemStack());
			player.getInventory().addItem(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_INFINITE, 1).setUnbreakable().toItemStack());
			player.getInventory().addItem(new ItemBuilder(LegacyMaterial.WOODEN_SWORD.getMaterial()).setUnbreakable().toItemStack());
			player.getInventory().setItem(9, new ItemStack(Material.ARROW));

			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
			return false;
		}
	}
}
