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
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.RewardsManager.Events;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.EntityUtils;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

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
        	if (i.getType() != Material.WOOL)
        		event.getDrops().remove(i);
        final Player player = event.getEntity();
        final PlayerData playerData = PlayerData.getPlayerData(player);
        final TeamManager playerTeam = playerData.getTeam();
        if (!GameState.isStep(GameState.WAITING) && !playerData.isSpectator()) {
            final Player killer = player.getKiller();
            final PlayerData killerData = PlayerData.getPlayerData(killer);
            if (killer != null) {
            	Sounds.playSound(killer, player.getLocation(), Sounds.VILLAGER_HIT, 1f, 1f);
            	killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5, 1));
                final PlayerData data = PlayerData.getPlayerData(killer);
                data.increaseKills(1);
                this.plugin.getRewardsManager().rewardPlayer(Events.ON_KILL, killer);
            }
            for (Player online : Bukkit.getOnlinePlayers())
            {
            	PlayerData data = PlayerData.getPlayerData(online);
            	if (killer == null)
            	{
            		online.sendMessage(data.getLanguage().getMessage(MsgEnum.DIED_MESSAGE).replaceAll("%VICTIM%", playerTeam.getColor() + player.getName()));
            	} else {
            		online.sendMessage(data.getLanguage().getMessage(MsgEnum.SLAYED_MESSAGE).replaceAll("%VICTIM%", playerTeam.getColor() + player.getName()).replaceAll("%KILLER%", killerData.getTeam().getColor() + killer.getName()));
            	}
            }
            Sounds.playSoundAll(player.getLocation(), Sounds.VILLAGER_DEATH, 1.0f, 2.0f);
            this.plugin.getRewardsManager().rewardPlayer(Events.ON_DEATH, player);
            Message.sendMessage(player, MsgEnum.GHOST_MESSAGE_1);
            Message.sendMessage(player, MsgEnum.GHOST_MESSAGE_2);
            UltimateSheepWarsPlugin.getVersionManager().getTitleUtils().titlePacket(player, 0, 60, 20, playerData.getLanguage().getMessage(MsgEnum.ELIMINATED), "");
            this.plugin.getGameTask().setSpectator(event.getEntity(), true);
            Location loc = player.getLocation().add(0,1,0);
            World w = loc.getWorld();
            final Random rdm = new Random();
            for (int i = 0; i < 5; i++)
            {
            	final ItemBuilder itemBuilder = RandomUtils.getRandom(new ItemBuilder(Material.INK_SACK).setDyeColor(DyeColor.RED), new ItemBuilder(Material.BONE));
            	final ItemStack deathStack = itemBuilder.toItemStack();
            	final Item item = w.dropItem(loc, deathStack);
            	item.setVelocity(RandomUtils.getRandomVector());
            	item.setPickupDelay(Integer.MAX_VALUE);
            	new BukkitRunnable()
            	{
            		public void run()
            		{
            			item.remove();
            		}
            	}.runTaskLater(this.plugin, (20 + (rdm.nextInt(90))));
            }
        }
        EntityUtils.resetPlayer(player, GameMode.SPECTATOR);
        if (player.getLocation().getY() <= 5)
        {
        	Location spawn = TeamManager.SPEC.getNextSpawn();
            player.teleport((spawn == null) ? ConfigManager.getLocation(Field.LOBBY) : spawn);
        }
    }
}