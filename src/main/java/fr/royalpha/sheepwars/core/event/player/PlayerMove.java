package fr.royalpha.sheepwars.core.event.player;

import fr.royalpha.sheepwars.api.GameState;
import fr.royalpha.sheepwars.api.SheepWarsTeam;
import fr.royalpha.sheepwars.core.handler.Sounds;
import fr.royalpha.sheepwars.core.manager.ConfigManager;
import fr.royalpha.sheepwars.core.message.Message;
import fr.royalpha.sheepwars.core.version.ATitleUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.royalpha.sheepwars.core.task.ParticleTask;
import fr.royalpha.sheepwars.core.util.RandomUtils;

public class PlayerMove extends UltimateSheepWarsEventListener
{
	private static final String FALLING_METADATA = "falling";
	
    public PlayerMove(final SheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Location from = event.getFrom();
        final Location to = event.getTo();
        
        /** On fait bouger les particules **/
        if (((from.getX() != to.getX()) || (from.getY() != to.getY()) || (from.getZ() != to.getZ())))
        	ParticleTask.move(player);
        
        /** Si le joueur sort de la map (tombe dans le vide), on s'occupe de lui ! **/
        if (to.getBlockY() < 0 && (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ())) {
        	final PlayerData data = PlayerData.getPlayerData(player);
        	if (GameState.isStep(GameState.WAITING)) {
        		player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 5, false, false));
        		player.setFallDistance(0.0f);
        		player.teleport(ConfigManager.getLocation(ConfigManager.Field.LOBBY).toBukkitLocation());
            	SheepWarsPlugin.getVersionManager().getTitleUtils().titlePacket(player, 5, 10, 5, "", "Flip!");
                Sounds.playSound(player, player.getLocation(), Sounds.ENDERMAN_TELEPORT, 1f, 1f);
            
        	} else if (data.getTeam() == SheepWarsTeam.SPEC) {
        		player.teleport(RandomUtils.getRandom(SheepWarsPlugin.getWorldManager().getVotedMap().getTeamSpawns(SheepWarsTeam.SPEC).getBukkitLocations()));
        		
        	} else if (!GameState.isStep(GameState.INGAME))
            {
            	player.setAllowFlight(true);
            	player.setFlying(true);
            	player.teleport(RandomUtils.getRandom(SheepWarsPlugin.getWorldManager().getVotedMap().getBoosterSpawns().getBukkitLocations()));
            }
            else {
                if (!player.hasMetadata(FALLING_METADATA) || System.currentTimeMillis() - (player.getMetadata(FALLING_METADATA).get(0)).asLong() >= 2000L) {
                    if (!player.hasMetadata(FALLING_METADATA))
                    	Sounds.playSound(player, to, Sounds.BLAZE_HIT, 1f, 2.0f);
                    player.setMetadata(FALLING_METADATA, new FixedMetadataValue(this.plugin, System.currentTimeMillis()));
                    SheepWarsPlugin.getVersionManager().getTitleUtils().defaultTitle(ATitleUtils.Type.TITLE, player, "", ChatColor.RED + data.getLanguage().getMessage(Message.Messages.OUT_OF_THE_GAME));
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 1));
            }
        }
    }
}
