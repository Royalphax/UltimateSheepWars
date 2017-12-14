package fr.asynchronous.sheepwars.core.event.player;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
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
import fr.asynchronous.sheepwars.core.handler.Kit;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Sheeps;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.task.HubTeleportation;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.util.MathUtils;
import fr.asynchronous.sheepwars.core.util.Utils;

public class PlayerInteract extends UltimateSheepWarsEventListener
{
	private ArrayList<OfflinePlayer> boardingDelay = new ArrayList<>();
	
    public PlayerInteract(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (GameState.isStep(GameState.LOBBY) || TeamManager.getPlayerTeam(player) == TeamManager.SPEC) {
            event.setCancelled(true);
        }
        if (event.getAction().name().contains("RIGHT")) {
            if (!GameState.isStep(GameState.LOBBY)) {
                Block block = event.getClickedBlock();
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == Material.ANVIL && Kit.getPlayerKit(player) == Kit.BUILDER) {
                	event.setCancelled(true);
                	if (!Utils.inventoryContains(player, Material.ANVIL)) {
                		block.setType(Material.AIR);
                		Utils.playSound(player, null, Sounds.ITEM_PICKUP, 1f, 1f);
                		player.getInventory().addItem(new ItemStack(Material.ANVIL, 1));
                		player.updateInventory();
                	}
                }
                if (event.hasItem()) {
                    ItemStack item = event.getItem();
                    if (item.getType() == Material.BOW || (item.getType() != Material.WOOL && item.getType() != Material.TNT)) {
                        return;
                    }
                    event.setCancelled(true);
                    TeamManager playerTeam = TeamManager.getPlayerTeam(player);
                    if (playerTeam != TeamManager.SPEC) {
                    	if (item.getType() == Material.WOOL)
                    	{
                    		if (!playerTeam.isBlocked() && player.getVehicle() == null && (!boardingDelay.contains(player))) {
                        		if (isGoodHand(event)) {
                        			for (Sheeps sheep : Sheeps.getAvailableSheeps()) {
                                        ItemStack sheepStack = sheep.getIcon(player);
                                        if (sheepStack.isSimilar(item)) {
                                        	if (sheep == Sheeps.INTERGALACTIC && Sheeps.getIntergalacticSheepUsed()) {
                                        		Message.sendMessage(player, this.plugin.PREFIX, Message.PLAYER_CANT_LAUNCH_SHEEP, "");
                                        		return;
                                        	}
                                            if (item.getAmount() == 1) {
                                                player.setItemInHand((ItemStack)null);
                                            }
                                            else {
                                                item.setAmount(item.getAmount() - 1);
                                                player.setItemInHand(item);
                                            }
                                            player.updateInventory();
                                            Utils.playSound(player, null, Sounds.HORSE_SADDLE, 1f, 1f);
                                            Location playerLocation = player.getLocation().add(0,2,0);
                                            Location location = playerLocation.toVector().add(playerLocation.getDirection().multiply(0.5)).toLocation(player.getWorld());
                                            final org.bukkit.entity.Sheep sheepEntity = sheep.spawnSheep(location, player, this.plugin);
                                            if (this.plugin.APRIL_FOOL_MODE)
                                            	sheepEntity.setBaby();
                                            sheepEntity.getLocation().setYaw(player.getLocation().getYaw());
                                            sheepEntity.getLocation().setPitch(player.getLocation().getPitch());
                                            sheepEntity.setMetadata("sheepwars_sheep", new FixedMetadataValue(this.plugin, true));
                                            if (Kit.getPlayerKit(player) == Kit.ARMORED_SHEEP) {
                                                sheepEntity.setMaxHealth(this.plugin.SHEEP_HEALTH+6);
                                                sheepEntity.setHealth(this.plugin.SHEEP_HEALTH+6);
                                                sheepEntity.setMetadata("armored_sheep", new FixedMetadataValue(this.plugin, true));
                                            } else {
                                            	sheepEntity.setMaxHealth(this.plugin.SHEEP_HEALTH);
                                                sheepEntity.setHealth(this.plugin.SHEEP_HEALTH);
                                            }
                                            if (sheep == Sheeps.BOARDING || sheep == Sheeps.REMOTE) {
                                            	if (sheep == Sheeps.BOARDING) {
                                            		boardingDelay.add(player);
                                            		new BukkitRunnable()
                                            		{
                                            			public void run()
                                            			{
                                            				if (player.getLocation().subtract(0,1,0).getBlock().getType() != Material.AIR)
                                            				{
                                            					this.cancel();
                                            					boardingDelay.remove(player);
                                            				}
                                            			}
                                            		}.runTaskTimer(this.plugin, 0, 0);
                                            	}
                                            	this.plugin.versionManager.getTitleUtils().titlePacket(player, 5, 40, 20, "", ChatColor.AQUA + Language.getMessageByLanguage(PlayerData.getPlayerData(plugin, player).getLocale(), Message.SHEEP_GET_DOWN));
                                            	sheepEntity.setPassenger(player);
                                            }
                                            if (!sheep.isFriendly()) {
                                            	sheepEntity.setVelocity(playerLocation.getDirection().add(new Vector(0,0.1,0)).multiply(this.plugin.LAUNCH_SHEEP_VELOCITY));
                                                PlayerData.getPlayerData(plugin, player).increaseSheepThrown(1);
                                            }
                                            Utils.playSound(player, null, Sounds.HORSE_SADDLE, 1f, 1f);
                                        }
                                    }
                        		}
                        	} else {
                        		Message.sendMessage(player, this.plugin.PREFIX, Message.PLAYER_CANT_LAUNCH_SHEEP, "");
                        	}
                    	} else if (item.getType() == Material.TNT) {
                    		if (item.getAmount() == 1) {
                                player.setItemInHand((ItemStack)null);
                            }
                            else {
                                item.setAmount(item.getAmount() - 1);
                                player.setItemInHand(item);
                            }
                    		final org.bukkit.entity.TNTPrimed tnt = player.getWorld().spawn(player.getLocation().add(0,1.5,0), TNTPrimed.class);
                    		tnt.setMetadata("no-damage-team-" + TeamManager.getPlayerTeam(player).getName(), new FixedMetadataValue(this.plugin, true));
                    		Utils.playSound(player, null, Sounds.HORSE_SADDLE, 1f, 1f);
                    		Utils.playSound(player, null, Sounds.FUSE, 1f, 1f);
                    		tnt.setVelocity(new Vector(0.0, 0.1, 0.0).add(player.getLocation().getDirection().multiply((this.plugin.LAUNCH_SHEEP_VELOCITY-0.5 > 0 ? this.plugin.LAUNCH_SHEEP_VELOCITY-0.5 : 0.5))));
                            new BukkitRunnable()
                            {
                            	Location lastLoc = null;
                            	public void run()
                            	{
                            		if (tnt.isDead()) {
                            			if (lastLoc != null)
                            				plugin.versionManager.getParticleFactory().playParticles(Particles.CLOUD, lastLoc, 0f, 0f, 0f, 20, 0.3f);
                            			this.cancel();
                            		}
                            		plugin.versionManager.getParticleFactory().playParticles(Particles.SMOKE_NORMAL, tnt.getLocation().add(0,0.5,0), 0f, 0f, 0f, 3, 0.0f);
                            		lastLoc = tnt.getLocation();
                            	}
                            }.runTaskTimer(this.plugin, 0, 0);
                            player.updateInventory();
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
    
    public boolean isGoodHand(PlayerInteractEvent event)
    {
    	boolean output = true;
    	/*if (this.plugin.versionManager.getVersion().newerThan(Version.v1_9_R1))
    	{
    		try {
    			Class<?> clazzEvent = event.getClass();
        		Class<?> clazzEquipmentSlot = ReflectionUtils.getClass("EquipmentSlot", PackageType.BUKKIT_INVENTORY);

        		Method mHand = clazzEvent.getMethod("getHand");
        		Method mEquipmentSlot = clazzEquipmentSlot.getMethod("valueOf", String.class);
        		
        		Object objEquipmentSlot = mEquipmentSlot.invoke(clazzEquipmentSlot, "HAND");
        		Object objHand = mHand.invoke(clazzEquipmentSlot);
        		
        		if (objHand != objEquipmentSlot)
        			output = false;
    		} catch (Exception ex) {
    			Utils.registerException(ex, true);
    		}
    	}*/
    	return output;
    }
}
