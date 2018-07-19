package fr.asynchronous.sheepwars.core.event.player;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.gui.manager.GuiManager;
import fr.asynchronous.sheepwars.core.handler.Contributor;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.SheepManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.util.MathUtils;
import fr.asynchronous.sheepwars.core.util.Utils;

public class PlayerInteract extends UltimateSheepWarsEventListener
{
	
    public PlayerInteract(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
	@EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final PlayerData data = PlayerData.getPlayerData(player);
        
        if (event.getAction() == null || event.getPlayer() == null)
        	return;
        
        if (!GameState.isStep(GameState.INGAME) || data.isSpectator())
            event.setCancelled(true);
        
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        		&& event.hasItem() && event.getItem().hasItemMeta() && !data.isSpectator()) {
        	final ItemStack item = event.getItem();
        	final Material mat = item.getType();
        	
        	if (GameState.isStep(GameState.INGAME) && mat.equals(Material.WOOL)) {
        		if (!data.getTeam().isBlocked() && !player.isInsideVehicle()) {
        			SheepManager sheep = SheepManager.getCorrespondingSheep(item, player);
        			if (sheep != null) {
        				ItemStack newItem = item.clone();
        				final int amount = item.getAmount() - 1;
        				if (amount <= 0) {
        					newItem = new ItemStack(Material.AIR);
        				} else {
        					newItem.setAmount(amount);
        				}
        				setItemInHand(newItem, player);
        				boolean launch = sheep.throwSheep(player, this.plugin);
        				if (launch) {
        					data.increaseSheepThrown(1);
        				} else {
        					newItem.setAmount(amount + 1);
        					setItemInHand(newItem, player);
        				}
        				player.updateInventory();
        			}
        		} else {
        			Message.sendMessage(player, MsgEnum.PLAYER_CANT_LAUNCH_SHEEP);
        		}
        		event.setCancelled(true);
        	} else if (GameState.isStep(GameState.WAITING)) {
        		
        		if (mat.equals(ConfigManager.getItemStack(Field.KIT_ITEM).getType())) {
        			
                    String inventoryName = data.getLanguage().getMessage(MsgEnum.KIT_INVENTORY_NAME).replaceAll("%KIT%", data.getKit().getName(player));
                    if (inventoryName.length() > 32)
                    	inventoryName = inventoryName.substring(0, 32);
                    GuiManager.openGui(this.plugin, player, inventoryName, GuiManager.getKitsInventoryNewInstance());
                    
        		} else if (mat.equals(ConfigManager.getItemStack(Field.RETURN_TO_HUB_ITEM).getType())) {
        			
                    player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*5, 5));
                    Sounds.playSound(player, player.getLocation(), Sounds.PORTAL_TRAVEL, 1f, 1f);
                	Message.sendMessage(player, MsgEnum.HUB_TELEPORTATION);
                	Utils.returnToHub(this.plugin, player);
                	new BukkitRunnable(){
                		public void run(){
                			if (player.isOnline())
                				Message.sendMessage(player, MsgEnum.CONNECTION_FAILED);
                		}
                	}.runTaskLater(this.plugin, (20 * 5));
                	
        		} else if (mat.equals(ConfigManager.getItemStack(Field.PARTICLES_ON_ITEM).getType()) || mat.equals(ConfigManager.getItemStack(Field.PARTICLES_OFF_ITEM).getType())) {
        			
        			if (data.getAllowedParticles())
                    {
                    	data.setAllowParticles(false);
                    	player.getInventory().setItem(4, new ItemBuilder(ConfigManager.getItemStack(Field.PARTICLES_OFF_ITEM)).setName(data.getLanguage().getMessage(MsgEnum.PARTICLES_OFF)).toItemStack());
                    } else {
                    	data.setAllowParticles(true);
                    	player.getInventory().setItem(4, new ItemBuilder(ConfigManager.getItemStack(Field.PARTICLES_ON_ITEM)).setName(data.getLanguage().getMessage(MsgEnum.PARTICLES_ON)).toItemStack());
                    	UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.SPELL_INSTANT, player.getLocation().add(0, 1, 0), 1f, 0.5f, 1f, 10, 0.0f);
                    }
        			player.updateInventory();
                    Sounds.playSound(player, player.getLocation(), Sounds.NOTE_STICKS, 1f, 1f);
                    
        		} else if (item.isSimilar(TeamManager.RED.getIcon(player)) || item.isSimilar(TeamManager.BLUE.getIcon(player))) {
        			for (TeamManager team : Arrays.asList(TeamManager.RED, TeamManager.BLUE)) {
                        if (item.isSimilar(team.getIcon(player))) {
                            final String displayName = team.getDisplayName(player);
                            final TeamManager playerTeam = data.getTeam();
                            if (playerTeam == team) {
                        		Message.sendMessage(player, MsgEnum.ALREADY_IN_THIS_TEAM);
                                break;
                            }
                            if (!player.hasPermission(TeamManager.BYPASS_TEAMS_PERMISSION) && !Contributor.isImportant(player) && Bukkit.getOnlinePlayers().size() > 1 && team.getOnlinePlayers().size() >= MathUtils.ceil((Bukkit.getOnlinePlayers().size() / 2)))
                            {
								Message.sendMessage(player, MsgEnum.CANT_JOIN_FULL_TEAM);
								break;
                            }
                            data.setTeam(team);
                            player.sendMessage(data.getLanguage().getMessage(MsgEnum.TEAM_JOIN_MESSAGE).replaceAll("%TEAM%", team.getColor() + displayName));
                            Sounds.playSound(player, player.getLocation(), Sounds.CLICK, 1f, 1f);
                            break;
                        }
                    }
                    player.updateInventory();
        		}
        	}
        }
    }
	
	public void setItemInHand(final ItemStack item, final Player player) {
		UltimateSheepWarsPlugin.getVersionManager().getNMSUtils().setItemInHand(item, player);
	}
}
