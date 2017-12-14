package fr.asynchronous.sheepwars.core.sheep;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Sheeps.SheepAction;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;

public class RemoteSheep implements SheepAction
{
    private final Map<Player, ArmorStand> armor;
    
    public RemoteSheep() {
        this.armor = new HashMap<>();
    }
    
    @Override
    public void onSpawn(final Player player, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
    	for (Player online : Bukkit.getOnlinePlayers())
        	online.hidePlayer(player);
    	ArmorStand armor = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        armor.setBasePlate(false);
        armor.setArms(true);
        /*EulerAngle leftArmPose = armor.getLeftArmPose();
        EulerAngle rightArmPose = armor.getRightArmPose();
        if (aiE.randomBoolean()) {
        	armor.setLeftLegPose(armor.getLeftLegPose().add(aiF.getRandomAngle(35), aiF.getRandomAngle(35), , z));
        } else {, z));
        } else {
        	EulerAngle legPose = armor.getRightLegPose();
        }*/
        plugin.versionManager.getNMSUtils().setMaxHealth(armor, 100.0D);
        //armor.setMaxHealth(100.0D); Incompatible < 1.11
        armor.setHealth(100.0D);
        armor.setCustomName(TeamManager.getPlayerTeam(player).getColor()+player.getName());
        armor.setCustomNameVisible(true);
        armor.setHelmet(new ItemBuilder(Material.SKULL_ITEM).setSkullOwner(player.getName()).toItemStack());
        armor.setChestplate(player.getInventory().getChestplate());
        armor.setLeggings(player.getInventory().getLeggings());
        armor.setBoots(player.getInventory().getBoots());
        armor.setItemInHand(new ItemBuilder(Material.WOOD_SWORD).toItemStack());
        this.armor.put(player, armor);
    	player.setNoDamageTicks(Integer.MAX_VALUE);
    }
    
    @Override
    public boolean onTicking(final Player player, final long ticks, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
    	if (this.armor.containsKey(player)) {
    		ArmorStand armor = this.armor.get(player);
    		armor.getLocation().setYaw(player.getLocation().getYaw());
    		armor.getLocation().setPitch(player.getLocation().getPitch());
    	}
    	return (sheep.getPassenger() == null || sheep.getLocation().getY() <= 0);
    }
    
    @Override
    public void onFinish(final Player player, final org.bukkit.entity.Sheep sheep, final boolean death, final UltimateSheepWarsPlugin plugin) {
    	Location loc = player.getLocation();
    	if (this.armor.containsKey(player)) {
    		ArmorStand armor = this.armor.get(player);
    		loc = armor.getLocation();
    		armor.remove();
    	}
    	if (!death)
        	plugin.versionManager.getWorldUtils().createExplosion(player, sheep.getLocation(), 3.0f);
    	player.teleport(loc);
        player.setFireTicks(0);
        player.setNoDamageTicks(0);
        player.playEffect(EntityEffect.HURT);
        for (Player online : Bukkit.getOnlinePlayers())
        	online.showPlayer(player);
    }
}
