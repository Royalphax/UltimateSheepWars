package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.gui.GuiKits;
import fr.asynchronous.sheepwars.core.gui.manager.GuiManager;
import fr.asynchronous.sheepwars.core.handler.Contributor;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.Kit;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.manager.KitManager.TriggerKitAction;
import fr.asynchronous.sheepwars.core.manager.SheepManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.task.HubTeleportation;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.util.MathUtils;
import fr.asynchronous.sheepwars.core.util.Utils;

public class PlayerInteract extends UltimateSheepWarsEventListener
{
	
    public PlayerInteract(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = PlayerData.getPlayerData(player);
        
        if (GameState.isStep(GameState.WAITING) || playerData.getTeam() == TeamManager.SPEC)
            event.setCancelled(true);
        
        KitManager.triggerKit(playerData, event, TriggerKitAction.PLAYER_INTERACT);
        
		if (event.getAction().name().contains("RIGHT")) {
			if (!GameState.isStep(GameState.WAITING)) {
				if (event.hasItem()) {
					ItemStack item = event.getItem();
					if (playerData.getTeam() != TeamManager.SPEC && item.getType() == Material.WOOL) {
						if (!playerData.getTeam().isBlocked() && player.getVehicle() == null) {
							for (SheepManager sheep : SheepManager.getAvailableSheeps()) {
								ItemStack sheepStack = sheep.getIcon(player);
								if (sheepStack.isSimilar(item)) {
									if (item.getAmount() == 1) {
										player.setItemInHand((ItemStack) null);
									} else {
										item.setAmount(item.getAmount() - 1);
										player.setItemInHand(item);
									}
									player.updateInventory();
									Utils.playSound(player, null, Sounds.HORSE_SADDLE, 1f, 1f);
									Location playerLocation = player.getLocation().add(0, 2, 0);
									Location location = playerLocation.toVector().add(playerLocation.getDirection().multiply(0.5)).toLocation(player.getWorld());
									final org.bukkit.entity.Sheep sheepEntity = sheep.spawnSheep(location, player, this.plugin);
									if (this.plugin.APRIL_FOOL_MODE)
										sheepEntity.setBaby();
									sheepEntity.getLocation().setYaw(player.getLocation().getYaw());
									sheepEntity.getLocation().setPitch(player.getLocation().getPitch());
									sheepEntity.setMetadata("sheepwars_sheep", new FixedMetadataValue(this.plugin, true));
									final Double sheepHealth = ConfigManager.getDouble(Field.SHEEP_HEALTH);
									if (Kit.getPlayerKit(player) == Kit.ARMORED_SHEEP) {
										UltimateSheepWarsPlugin.getVersionManager().getNMSUtils().setMaxHealth(sheepEntity, sheepHealth + 6);
										sheepEntity.setHealth(sheepHealth + 6);
										sheepEntity.setMetadata("armored_sheep", new FixedMetadataValue(this.plugin, true));
									} else {
										UltimateSheepWarsPlugin.getVersionManager().getNMSUtils().setMaxHealth(sheepEntity, sheepHealth);
										sheepEntity.setHealth(sheepHealth);
									}
									if (!sheep.isFriendly()) {
										sheepEntity.setVelocity(playerLocation.getDirection().add(new Vector(0, 0.1, 0)).multiply(this.plugin.LAUNCH_SHEEP_VELOCITY));
										playerData.increaseSheepThrown(1);
									}
									Sounds.playSound(player, null, Sounds.HORSE_SADDLE, 1f, 1f);
								}
							}
						} else {
							Message.sendMessage(player, MsgEnum.PLAYER_CANT_LAUNCH_SHEEP);
						}
					}
				}
			}
            else {
                event.setCancelled(true);
                if (event.hasItem()) {
                    final ItemStack item2 = event.getItem();
                    if (item2.getType() == Material.getMaterial(this.plugin.ITEM_KIT) && item2.hasItemMeta()) {
                        PlayerData data = PlayerData.getPlayerData(this.plugin, player);
                        String lang = data.getLocale().toString();
                        Kit playerKit = Kit.getPlayerKit(player);
                        
                        String inventoryName = Language.getMessageByLanguage(lang, Message.KIT_INVENTORY_NAME).replaceAll("%KIT%", (playerKit == null ? Language.getMessageByLanguage(lang, Message.KIT_NULL_NAME) : playerKit.getName(player)));
                        if (inventoryName.length() > 32)
                        	inventoryName = inventoryName.substring(0, 32);
                        GuiManager.openGui(this.plugin, new GuiKits(this.plugin, player, inventoryName));
                    }
                    else if (item2.getType() == Material.getMaterial(this.plugin.ITEM_RETURN) && item2.hasItemMeta()) {
                    	final PlayerData data = PlayerData.getPlayerData(this.plugin, player);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*5, 5));
                        Utils.playSound(player, player.getLocation(), Sounds.PORTAL_TRAVEL, 1f, 1f);
                    	player.sendMessage(this.plugin.PREFIX+Language.getMessageByLanguage(data.getLocale(), Message.HUB_TELEPORTATION));
                    	HubTeleportation.teleportToLobby(this.plugin, player);
                    	new BukkitRunnable(){
                    		public void run(){
                    			if (player.isOnline())
                    				player.sendMessage(plugin.PREFIX+ChatColor.RED+Language.getMessageByLanguage(data.getLocale(), Message.CONNECTION_FAILED));
                    		}
                    	}.runTaskLater(this.plugin, 20*5);
                    }
                    else if (item2.getType() == Material.getMaterial(this.plugin.ITEM_PARTICLES_ON) || item2.getType() == Material.getMaterial(this.plugin.ITEM_PARTICLES_OFF) && item2.hasItemMeta()) {
                    	PlayerData data = PlayerData.getPlayerData(this.plugin, player);
                        if (data.getAllowedParticles() == true)
                        {
                        	data.setAllowParticles(false);
                        	player.getInventory().setItem(4, new ItemBuilder(Material.getMaterial(this.plugin.ITEM_PARTICLES_OFF)).setName(Message.getMessage(player, ChatColor.GOLD+"", Message.PARTICLES_OFF, "")).toItemStack());
                        } else {
                        	data.setAllowParticles(true);
                        	this.plugin.versionManager.getParticleFactory().playParticles(player, Particles.SPELL_INSTANT, player.getLocation(), 0.3f, 0.3f, 0.3f, 5, 0.0f);
                        	player.getInventory().setItem(4, new ItemBuilder(Material.getMaterial(this.plugin.ITEM_PARTICLES_ON)).setName(Message.getMessage(player, ChatColor.GOLD+"", Message.PARTICLES_ON, "")).toItemStack());
                        }
                        Utils.playSound(player, player.getLocation(), Sounds.NOTE_STICKS, 1f, 1f);
                    }
                    else if (((item2.getType() == TeamManager.BLUE.getMaterial()) || (item2.getType() == TeamManager.RED.getMaterial())) && item2.hasItemMeta()) {
                        for (TeamManager team : TeamManager.values()) {
                            if (item2.isSimilar(team.getIcon(player))) {
                                String displayName = team.getDisplayName(player);
                                TeamManager playerTeam = TeamManager.getPlayerTeam(player);
                                if (!player.hasPermission("sheepwars.teams.bypass") && !Contributor.isImportant(player))
                                {
                                	if (playerTeam == team) {
                                    	player.sendMessage(String.valueOf(this.plugin.PREFIX) + ChatColor.GRAY + Message.getMessage(player, "", Message.ALREADY_IN_THIS_TEAM, ""));
                                        break;
                                    }
                                    if (Bukkit.getOnlinePlayers().size() > 1 && team.getOnlinePlayers().size() >= MathUtils.ceil(Bukkit.getOnlinePlayers().size() / 2)) {
                                        player.sendMessage(String.valueOf(this.plugin.PREFIX) + ChatColor.GRAY + Message.getMessage(player, "", Message.CANT_JOIN_FULL_TEAM, ""));
                                        break;
                                    }
                                }
                                if (playerTeam != TeamManager.SPEC) {
                                    playerTeam.removePlayer(player);
                                }
                                team.addPlayer(player);
                                player.sendMessage(String.valueOf(this.plugin.PREFIX) + ChatColor.GRAY + Message.getMessage(player, "", Message.TEAM_JOIN_MESSAGE, "").replaceAll("%TEAM%", team.getColor() + displayName));
                                Utils.playSound(player, player.getLocation(), Sounds.NOTE_STICKS, 1f, 1f);
                                break;
                            }
                        }
                        player.updateInventory();
                    }
                }
            }
        } else if (this.plugin.CHRISTMAS_MODE && event.getAction().name().contains("LEFT"))
        {
        	if (event != null && !event.hasItem()) {
        		player.launchProjectile(Snowball.class);
        		Utils.playSound(player, null, Sounds.NOTE_STICKS, 1.0f, 2.0f);
        	}
        }
    }
}
