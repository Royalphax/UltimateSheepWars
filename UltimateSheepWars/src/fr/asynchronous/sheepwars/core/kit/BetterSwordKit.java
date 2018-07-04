package fr.asynchronous.sheepwars.core.kit;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public class BetterSwordKit extends KitManager {

	public static final int PERCENT_TO_DO_MORE_DAMAGE = 5;
	
	public BetterSwordKit() {
		super(2, MsgEnum.KIT_BETTER_SWORD_NAME, MsgEnum.KIT_BETTER_SWORD_DESCRIPTION, "sheepwars.kit.bettersword", 10, 10, new ItemBuilder(Material.STONE_SWORD));
	}

	@Override
	public boolean onEquip(Player player) {
		final PlayerData data = PlayerData.getPlayerData(player);
		TeamManager team = data.getTeam();
		Color color = team.getLeatherColor();
		
		player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setLeatherArmorColor(color).addEnchant(Enchantment.PROTECTION_PROJECTILE, 2).setUnbreakable().toItemStack());
		player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherArmorColor(color).addEnchant(Enchantment.PROTECTION_PROJECTILE, 2).setUnbreakable().toItemStack());
		player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).setLeatherArmorColor(color).addEnchant(Enchantment.PROTECTION_PROJECTILE, 2).setUnbreakable().toItemStack());
		player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setLeatherArmorColor(color).addEnchant(Enchantment.PROTECTION_PROJECTILE, 2).setUnbreakable().toItemStack());
		player.getInventory().setItem(8, new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherArmorColor(color).setName(team.getColor() + "" + ChatColor.BOLD + team.getDisplayName(player)).setUnbreakable().toItemStack());
		player.getInventory().addItem(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_INFINITE, 1).setUnbreakable().toItemStack());
		player.getInventory().addItem(new ItemBuilder(Material.STONE_SWORD).setUnbreakable().toItemStack());
		player.getInventory().setItem(9, new ItemStack(Material.ARROW));
		
		return false;
	}

	@EventHandler
	public void onPlayerDamage(final EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			final Player player = (Player) event.getDamager();
			final PlayerData data = PlayerData.getPlayerData(player);
			if (RandomUtils.getRandomByPercent(PERCENT_TO_DO_MORE_DAMAGE) && data.getKit().getId() == this.getId()) {
				event.setCancelled(true);
				if (event.getEntity() instanceof Damageable) 
					((Damageable) event.getEntity()).damage(event.getFinalDamage() * 1.5, player);
			}
		}
	}
}
