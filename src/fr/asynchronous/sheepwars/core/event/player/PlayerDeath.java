package fr.asynchronous.sheepwars.core.event.player;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.manager.RewardsManager.Events;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.util.EntityUtils;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.util.RandomUtils;
import fr.asynchronous.sheepwars.core.util.Utils;

public class PlayerDeath extends UltimateSheepWarsEventListener
{
    public PlayerDeath(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
    	event.setDeathMessage((String)null);
        ArrayList<ItemStack> copy = new ArrayList<>(event.getDrops());
    	for (ItemStack i : copy)
        {
        	if (i.getType() != Material.WOOL)
        		event.getDrops().remove(i);
        }
        final Player player = event.getEntity();
        final TeamManager playerTeam = TeamManager.getPlayerTeam(player);
        if (!GameState.isStep(GameState.LOBBY) && playerTeam != TeamManager.SPEC) {
            final Player killer = player.getKiller();
            final TeamManager killerTeam = TeamManager.getPlayerTeam(killer);
            if (killer != null) {
            	Utils.playSound(killer, player.getLocation(), Sounds.VILLAGER_HIT, 1f, 1f);
            	killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5, 1));
                final PlayerData data = PlayerData.getPlayerData(this.plugin, killer);
                data.increaseKills(1);
                this.plugin.rewardManager.rewardPlayer(Events.ON_KILL, killer);
            }
            for (Player online : Bukkit.getOnlinePlayers())
            {
            	if (killer == null)
            	{
            		online.sendMessage(this.plugin.PREFIX + Language.getMessageByLanguage(PlayerData.getPlayerData(plugin, online).getLocale(), Message.DIED_MESSAGE).replaceAll("%VICTIM%", playerTeam.getColor() + player.getName()));
            	} else {
            		online.sendMessage(this.plugin.PREFIX + Language.getMessageByLanguage(PlayerData.getPlayerData(plugin, online).getLocale(), Message.SLAYED_MESSAGE).replaceAll("%VICTIM%", playerTeam.getColor() + player.getName()).replaceAll("%KILLER%", killerTeam.getColor() + killer.getName()));
            	}
            }
            Utils.playSoundAll(player.getLocation(), Sounds.VILLAGER_DEATH, 1.0f, 2.0f);
            this.plugin.rewardManager.rewardPlayer(Events.ON_DEATH, player);
            player.sendMessage(Message.getMessage(player, this.plugin.PREFIX+"", Message.GHOST_MESSAGE_1, ""));
            player.sendMessage(Message.getMessage(player, this.plugin.PREFIX+"", Message.GHOST_MESSAGE_2, ""));
            this.plugin.versionManager.getTitleUtils().titlePacket(player, 0, 60, 20, Message.getMessage(player, "", Message.ELIMINATED, ""), "");
            this.plugin.GAME_TASK.setSpectator(event.getEntity(), true);
            Location loc = player.getLocation().add(0,1,0);
            World w = loc.getWorld();
            ItemStack deathStack = new ItemBuilder(Material.INK_SACK).setDyeColor(DyeColor.RED).toItemStack();
            for (int i = 0; i < 5; i++)
            {
            	final Item item = w.dropItem(loc, deathStack);
            	item.setVelocity(RandomUtils.getRandomVector());
            	item.setPickupDelay(Integer.MAX_VALUE);
            	new BukkitRunnable()
            	{
            		public void run()
            		{
            			item.remove();
            		}
            	}.runTaskLater(this.plugin, 20+(new Random().nextInt(90)));
            }
        }
        EntityUtils.resetPlayer(player, GameMode.SPECTATOR, this.plugin);
        if (player.getLocation().getY() <= 5)
        {
        	Location spawn = TeamManager.SPEC.getNextSpawn();
            player.teleport((spawn == null) ? this.plugin.LOBBY_LOCATION : spawn);
        }
    }
}