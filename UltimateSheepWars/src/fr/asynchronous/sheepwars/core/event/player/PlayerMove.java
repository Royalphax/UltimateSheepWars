package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.task.ParticleTask;
import fr.asynchronous.sheepwars.core.version.ATitleUtils.Type;

public class PlayerMove extends UltimateSheepWarsEventListener
{
	private static final String FALLING_METADATA = "falling";
	
    public PlayerMove(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Location from = event.getFrom();
        final Location to = event.getTo();
        
        /** On verifie que monsieur a deja rentre ses identifiants **/
        if (this.plugin.getAccountManager().askForOwnerName())
        {
        	/**if (!player.isFlying() && player.getLocation().subtract(0,1,0).getBlock().getType() == Material.AIR) {
        		player.setAllowFlight(true);
        		player.setFlying(true);
        	}**/
        	this.plugin.getAccountManager().openGUI(player);
        	
        }
        
        /** On empeche les joueurs possedant la metadata de bouger **/
        if (player.hasMetadata("cancel_move") && ((from.getBlockX() != to.getBlockX()) || (from.getBlockY() != to.getBlockY()) || (from.getBlockZ() != to.getBlockZ())))
        	player.teleport(from);
        
        /** On fait bouger les particules **/
        if (((from.getX() != to.getX()) || (from.getY() != to.getY()) || (from.getZ() != to.getZ())))
        	ParticleTask.move(player);
        
        /** Si le joueur sort de la map (tombe dans le vide), on s'occupe de lui ! **/
        if (to.getBlockY() < 0 && (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ())) {
        	final PlayerData data = PlayerData.getPlayerData(player);
        	if (GameState.isStep(GameState.WAITING)) {
        		player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 60, 5));
        		player.setFallDistance(0.0f);
            	UltimateSheepWarsPlugin.getVersionManager().getTitleUtils().titlePacket(player, 5, 10, 5, "", ChatColor.ITALIC+"Woosh!");
                player.teleport(ConfigManager.getLocation(Field.LOBBY));
                Sounds.playSound(player, to, Sounds.ENDERDRAGON_WINGS, 1f, 2.0f);
            
        	} else if (data.getTeam() == TeamManager.SPEC) {
        		Field field = Field.SPEC_SPAWNS;
        		if (ConfigManager.getLocations(field).isEmpty())
        			field = Field.BOOSTERS;
        		player.teleport(ConfigManager.getRdmLocationFromList(field));
        		
        	} else if (!GameState.isStep(GameState.INGAME))
            {
            	player.setAllowFlight(true);
            	player.setFlying(true);
        		player.teleport(ConfigManager.getRdmLocationFromList(Field.BOOSTERS));
            }
            else {
                if (!player.hasMetadata(FALLING_METADATA) || System.currentTimeMillis() - (player.getMetadata(FALLING_METADATA).get(0)).asLong() >= 2000L) {
                    if (!player.hasMetadata(FALLING_METADATA))
                    	Sounds.playSound(player, to, Sounds.BLAZE_HIT, 1f, 2.0f);
                    player.setMetadata(FALLING_METADATA, new FixedMetadataValue(this.plugin, System.currentTimeMillis()));
                    UltimateSheepWarsPlugin.getVersionManager().getTitleUtils().defaultTitle(Type.TITLE, player, "", ChatColor.RED + data.getLanguage().getMessage(MsgEnum.OUT_OF_THE_GAME));
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 1));
            }
        }
    }
}
