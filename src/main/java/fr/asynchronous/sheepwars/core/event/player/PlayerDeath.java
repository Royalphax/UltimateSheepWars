package fr.asynchronous.sheepwars.core.event.player;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.api.PlayerData;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.api.GameState;
import fr.asynchronous.sheepwars.api.util.ItemBuilder;
import fr.asynchronous.sheepwars.api.SheepWarsTeam;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.RewardsManager.Events;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.Messages;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public class PlayerDeath extends UltimateSheepWarsEventListener
{
    public PlayerDeath(final SheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
    	event.setDeathMessage((String)null);
        final Player player = event.getEntity();
    	for (ItemStack i : event.getDrops())
        	if (i.getType().toString().contains("WOOL"))
        		player.getWorld().dropItem(player.getLocation(), i);
    	event.getDrops().clear();
        final PlayerData playerData = PlayerData.getPlayerData(player);
        final SheepWarsTeam playerTeam = playerData.getTeam();
        final int countdown = ConfigManager.getInt(Field.KILLER_VIEW_STAY_TIME);
        if (!GameState.isStep(GameState.WAITING) && !playerData.isSpectator()) {
            final Player killer = player.getKiller();
            String subtitle = "";
            if (killer != null) {
            	Sounds.playSound(killer, player.getLocation(), Sounds.VILLAGER_HIT, 1f, 1f);
                final PlayerData data = PlayerData.getPlayerData(killer);
                data.increaseKills(1);
                this.plugin.getRewardsManager().rewardPlayer(Events.ON_KILL, killer);
                subtitle = playerData.getLanguage().getMessage(Messages.KILLED_MESSAGE).replaceAll("%PLAYER%", killer.getName());
            }
            for (Player online : Bukkit.getOnlinePlayers())
            {
            	PlayerData data = PlayerData.getPlayerData(online);
            	if (killer == null)
            	{
            		online.sendMessage(data.getLanguage().getMessage(Messages.DIED_MESSAGE).replaceAll("%VICTIM%", playerTeam.getColor() + player.getName()));
            	} else {
            		online.sendMessage(data.getLanguage().getMessage(Messages.SLAYED_MESSAGE).replaceAll("%VICTIM%", playerTeam.getColor() + player.getName()).replaceAll("%KILLER%", PlayerData.getPlayerData(killer).getTeam().getColor() + killer.getName()));
            	}
            }
            Sounds.playSoundAll(player.getLocation(), Sounds.VILLAGER_DEATH, 1.0f, 2.0f);
            this.plugin.getRewardsManager().rewardPlayer(Events.ON_DEATH, player);
            Message.sendMessage(player, Messages.GHOST_DESCRIPTION);
            SheepWarsPlugin.getVersionManager().getTitleUtils().titlePacket(player, 0, 60, 20, playerData.getLanguage().getMessage(Messages.ELIMINATED), subtitle);
            this.plugin.setSpectator(event.getEntity(), true);
            Location loc = player.getLocation().add(0,1,0);
            World w = loc.getWorld();
            final Random rdm = new Random();
            for (int i = 0; i < 5; i++)
            {
            	final ItemBuilder itemBuilder = new ItemBuilder(Material.BONE);
            	final ItemStack deathStack = itemBuilder.toItemStack();
            	final Item item = w.dropItem(loc, deathStack);
            	item.setVelocity(RandomUtils.getRandomVector().multiply(0.5));
            	item.setPickupDelay(Integer.MAX_VALUE);
            	new BukkitRunnable()
            	{
            		public void run()
            		{
            			item.remove();
            		}
            	}.runTaskLater(this.plugin, (20 + (rdm.nextInt(90))));
            }
            if (countdown > 0 && player.getGameMode() == GameMode.SPECTATOR) {
            	new BukkitRunnable()
            	{
            		int i = countdown * 20;
            		public void run()
            		{
            			player.setSpectatorTarget(killer);
            			if (i <= 0) {
            				cancel();
            				if (player.getGameMode() == GameMode.SPECTATOR)
            					player.setSpectatorTarget(null);
            			}
            			i--;
            		}
            	}.runTaskTimer(this.plugin, 0, 0);
            }
        }
        if (player.getLocation().getY() <= 5 && countdown <= 0)
        {
        	Location spawn = SheepWarsTeam.SPEC.getNextSpawn();
            player.teleport((spawn == null) ? ConfigManager.getLocation(Field.LOBBY).toBukkitLocation() : spawn);
        }
    }
}