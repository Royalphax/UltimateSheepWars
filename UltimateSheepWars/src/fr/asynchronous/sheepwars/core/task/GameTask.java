package fr.asynchronous.sheepwars.core.task;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Kit;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Sheeps;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.manager.DataManager;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.manager.RewardsManager.Events;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.util.MathUtils;
import fr.asynchronous.sheepwars.core.util.RandomUtils;
import fr.asynchronous.sheepwars.core.util.Utils;

public class GameTask extends BukkitRunnable
{
	public final UltimateSheepWarsPlugin plugin;
	public int remainingDurationInSecs;
    public int boosterCountdown;
    public int sheepCountdown;
    
    public GameTask(final UltimateSheepWarsPlugin plugin) {
        this.plugin = plugin;
        this.remainingDurationInSecs = (plugin.GAME_TIME*60);
        this.boosterCountdown = 30;
        this.sheepCountdown = plugin.GIVE_SHEEP_INTERVAL;
        plugin.GAME_TASK = this;
        for (Player player : Bukkit.getOnlinePlayers()) {
            Sheeps.giveRandomSheep(player, plugin);
        }
        new BukkitRunnable() {
            public void run() {
            	sheepCountdown--;
            	if (sheepCountdown <= 0) {
            		for (Player player : Bukkit.getOnlinePlayers())
                        Sheeps.giveRandomSheep(player, plugin);
            		sheepCountdown = plugin.GIVE_SHEEP_INTERVAL;
            	}
            }
        }.runTaskTimer(plugin, 0, 20);
        this.runTaskTimer(plugin, 0, 20);
    }
    
    public void run() {
    	try {
    		for (Player online : Bukkit.getOnlinePlayers()){
        		if (TeamManager.getPlayerTeam(online) != TeamManager.SPEC) {
        			PlayerData data = PlayerData.getPlayerData(this.plugin, online);
        			data.increaseTotalTime(1);
        			UltimateSheepWarsPlugin.getVersionManager().getTitleUtils().actionBarPacket(online, Language.getMessageByLanguage(data.getLocale(), Message.ACTION_KILLS_STATS).replace("%KILLS%", data.getActualKills()+""));
        		}
        	}
            if (remainingDurationInSecs == 0 || !GameState.isStep(GameState.IN_GAME)) {
                this.cancel();
                if (GameState.isStep(GameState.IN_GAME))
                	for (Player online : Bukkit.getOnlinePlayers())
                		UltimateSheepWarsPlugin.getVersionManager().getTitleUtils().titlePacket(online, 5, 10*20, 20, Language.getMessageByLanguage(online.spigot().getLocale(), Message.FINISH_EQUALITY), Language.getMessageByLanguage(online.spigot().getLocale(), Message.GAME_END_EQUALITY_DESCRIPTION));
                if (!GameState.isStep(GameState.POST_GAME))
                	this.stopGame(null);
                return;
            }
            final int remainingMins = remainingDurationInSecs / 60 % 60;
            final int remainingSecs = remainingDurationInSecs % 60;
            for (Language langs : Language.getLanguages()) {
            	try {
            		langs.getScoreboardWrapper().setTitle(ChatColor.DARK_GRAY + "-" + ChatColor.YELLOW + " " + Language.getMessageByLanguage(langs.getName(), Message.SCOREBOARD_TITLE) + " " + ChatColor.GREEN + ((remainingMins < 10) ? "0" : "") + remainingMins + ":" + ((remainingSecs < 10) ? "0" : "") + remainingSecs + ChatColor.DARK_GRAY + " -");
            	} catch (IllegalArgumentException ex)
            	{
            		new ExceptionManager(ex).register(true);
            	}
            	langs.refreshSheepCountdown(sheepCountdown);
            	langs.refreshBoosterCountdown(boosterCountdown);
            }
            if (remainingDurationInSecs == (plugin.GAME_TIME*60-30)) {
                Message.broadcast(this.plugin.PREFIX+"", Message.BOOSTERS_MESSAGE, "");
                new BukkitRunnable() {
                	int max = plugin.BOOSTER_INTERVAL + plugin.BOOSTER_LIFE_TIME;
                	int t = max-plugin.BOOSTER_INTERVAL;
                	Block magicBlock;
                	Location magicBlockLocation;
                    @SuppressWarnings("deprecation")
    				public void run() {
                    	if (t <= max-plugin.BOOSTER_INTERVAL)
                    	{
                    		if (t == max-plugin.BOOSTER_INTERVAL)
                    		{
                    			magicBlockLocation = plugin.BOOSTER_LOCATIONS.get(MathUtils.random.nextInt(plugin.BOOSTER_LOCATIONS.size()));
                                magicBlock = magicBlockLocation.getBlock();
                                magicBlock.setType(Material.WOOL);
                                Sounds.playSoundAll(null, Sounds.LEVEL_UP, 1f, 1f);
                    		}
                    		if (magicBlock.getType() != Material.AIR) {
                    			magicBlock.setData(DyeColor.values()[MathUtils.random.nextInt(DyeColor.values().length)].getWoolData());
                    			UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.REDSTONE, magicBlockLocation, 1.0f, 1.0f, 1.0f, 20, 1.0f);
                            } else {
                            	t = 0;
                            }
                    	}
                    	if (t <= 0 || !GameState.isStep(GameState.IN_GAME)) {
                    		magicBlock.setType(Material.AIR);
                    		t = max;
                    		if (!GameState.isStep(GameState.IN_GAME))
                    			this.cancel();
                    	}
                    	t--;
                    }
                }.runTaskTimer(this.plugin, 0, 20);
            }
            else if (remainingDurationInSecs == (plugin.BOARDING_TIME*60)) {
                Message.broadcastTitle(Message.BOARDING_TITLE, Message.BOARDING_SUBTITLE);
                new BukkitRunnable() {
                    public void run() {
                    	for (Player player : Bukkit.getOnlinePlayers()) {
                            Sheeps.giveSheep(player , Sheeps.BOARDING, plugin);
                        }
                    }
                }.runTaskTimer(plugin, 0, 20*60);
            }
            if (boosterCountdown <= 0)
            	boosterCountdown = plugin.BOOSTER_INTERVAL + plugin.BOOSTER_LIFE_TIME;
            boosterCountdown--;
            --remainingDurationInSecs;
    	} catch (Exception ex) {
    		new ExceptionManager(ex).register(true);
    	}
    }
    
    public boolean isBooster(final Location location) {
        for (final Location booster : this.plugin.BOOSTER_LOCATIONS) {
            if (booster.getBlockX() == location.getBlockX() && booster.getBlockY() == location.getBlockY() && booster.getBlockZ() == location.getBlockZ()) {
                return true;
            }
        }
        return false;
    }
    
    public void setSpectator(final Player player, final boolean lose) {
        player.setAllowFlight(true);
        if (TeamManager.getPlayerTeam(player) != TeamManager.SPEC) {
        	if (lose)
        	{
        		this.removePlayer(player);
                final PlayerData data = PlayerData.getPlayerData(this.plugin, player);
                data.increaseDeaths(1);
        	}
        	TeamManager.SPEC.addPlayer(player);
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (player != online) {
                    player.showPlayer(online);
                    if (TeamManager.getPlayerTeam(online) != TeamManager.SPEC) {
                        online.hidePlayer(player);
                    }
                }
            }
        }
    }
	
	public void removePlayer(final Player player) {
		final TeamManager team = TeamManager.getPlayerTeam(player);
        if (team != TeamManager.SPEC) {
            team.removePlayer(player);
            Kit.setPlayerKit(player, null);
            if (GameState.isStep(GameState.LOBBY)) {
                PlayerData.getPlayers().remove(player);
            } else if (GameState.isStep(GameState.IN_GAME) && team.getOnlinePlayers().size() == 0) {
            	final fr.asynchronous.sheepwars.core.manager.TeamManager winnerTeam = fr.asynchronous.sheepwars.core.manager.TeamManager.BLUE == team ? fr.asynchronous.sheepwars.core.manager.TeamManager.RED : fr.asynchronous.sheepwars.core.manager.TeamManager.BLUE;
                GameState.setCurrentStep(GameState.POST_GAME, this.plugin);
                new BukkitRunnable() {
                    public void run() {
                        for (Player online : Bukkit.getOnlinePlayers()) {
                            if (TeamManager.getTeam(online.getName()) == winnerTeam) {
                            	PlayerData data = PlayerData.getPlayerData(plugin, online);
                            	data.increaseWins(1);
                            }
                            online.sendMessage(String.valueOf(plugin.PREFIX) + ChatColor.GOLD + ChatColor.BOLD + Message.getMessage(online, "", Message.VICTORY, " ").replaceAll("%WINNER%", winnerTeam.getColor() + winnerTeam.getDisplayName(online)) + " " + Message.getDecoration()+ChatColor.AQUA+""+ChatColor.BOLD+Message.getMessage(online, " ", Message.CONGRATULATIONS, " ")+ Message.getDecoration());
                            UltimateSheepWarsPlugin.getVersionManager().getTitleUtils().titlePacket(online, 5, 10*20, 20, ChatColor.YELLOW + "" + Message.getMessage(online, ChatColor.YELLOW.toString(), Message.GAME_END_TITLE, ""), Message.getDecoration()+""+ChatColor.GOLD+" "+ChatColor.BOLD+Message.getMessage(online, "", Message.VICTORY, " ").replaceAll("%WINNER%", winnerTeam.getColor() + winnerTeam.getDisplayName(online)) + " " + Message.getDecoration());
                        }
                        new BukkitRunnable() {
                            private int ticks = 300;
                            
                            public void run() {
                                if (this.ticks == 0) {
                                    this.cancel();
                                    return;
                                }
    							Location location = plugin.BOOSTER_LOCATIONS.get(MathUtils.random.nextInt(plugin.BOOSTER_LOCATIONS.size()));
    							ArrayList<Player> onlines = new ArrayList<>();
    				        	for (Player online : Bukkit.getOnlinePlayers())
    				        		onlines.add(online);
    				        	Random rdm = new Random();
    							FireworkEffect effect = FireworkEffect.builder().flicker(rdm.nextBoolean()).withColor(RandomUtils.getRandomColor()).withFade(RandomUtils.getRandomColor()).with(Type.BALL_LARGE).build();
    							UltimateSheepWarsPlugin.getVersionManager().getCustomEntities().spawnInstantExplodingFirework(location, effect, onlines);
    							this.ticks -= 20;
                            }
                        }.runTaskTimer(plugin, 0L, 20);
                        stopGame(winnerTeam);
                    }
                }.runTaskLater(this.plugin, 1L);
            }
        }
    }
	
	public void stopGame(final TeamManager winnerTeam) {
		new TerminatedGameTask(this.plugin);
        for (Entry<OfflinePlayer, PlayerData> entry : PlayerData.getData()) {
        	final OfflinePlayer player = entry.getKey();
        	final PlayerData data = entry.getValue();
            if (winnerTeam != null && winnerTeam != TeamManager.SPEC && player.isOnline()) {
                if (data.getTeam() == winnerTeam) {
                    data.increaseWins(1);
                    this.plugin.getRewardsManager().rewardPlayer(Events.ON_WIN, data.getPlayer());
                } else {
                	this.plugin.getRewardsManager().rewardPlayer(Events.ON_LOOSE, data.getPlayer());
                }
            }
            if (DataManager.isConnected())
            	data.uploadData(player);
        }
    }
}
