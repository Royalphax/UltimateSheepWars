package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.gui.GuiValidateOwner;
import fr.asynchronous.sheepwars.core.gui.manager.GuiManager;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.task.ParticleTask;
import fr.asynchronous.sheepwars.core.util.Utils;

public class PlayerMove extends UltimateSheepWarsEventListener
{
    public PlayerMove(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Location from = event.getFrom();
        final Location to = event.getTo();
        if (!this.plugin.OWNER_VALIDATED)
        {
        	if (!player.isFlying() && player.getLocation().subtract(0,1,0).getBlock().getType() == Material.AIR) {
        		player.setAllowFlight(true);
        		player.setFlying(true);
        	}
        	if (!GuiManager.isPlayer(player))
        		GuiManager.openGui(this.plugin, new GuiValidateOwner(this.plugin, player));
        }
        if (player.hasMetadata("cancel_move") && ((from.getBlockX() != to.getBlockX()) || (from.getBlockY() != to.getBlockY()) || (from.getBlockZ() != to.getBlockZ())))
        	player.teleport(from);
        if (((from.getX() != to.getX()) || (from.getY() != to.getY()) || (from.getZ() != to.getZ())))
        	ParticleTask.move(player);
        if (to.getBlockY() < 0 && (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ())) {
            final TeamManager team = TeamManager.getPlayerTeam(player);
            if (GameState.isStep(GameState.POST_GAME) && team != TeamManager.SPEC)
            {
            	player.setAllowFlight(true);
            	player.setFlying(true);
            	Location spawn = TeamManager.SPEC.getNextSpawn();
                player.teleport((spawn == null) ? this.plugin.LOBBY_LOCATION : spawn);
            }
            else if (GameState.isStep(GameState.LOBBY) || team == TeamManager.SPEC) {
            	player.setFallDistance(0.0f);
            	this.plugin.versionManager.getTitleUtils().titlePacket(player, 5, 10, 5, "", ChatColor.ITALIC+"Flap!");
                player.teleport((GameState.isStep(GameState.LOBBY) || team.getSpawns().size() == 0) ? this.plugin.LOBBY_LOCATION : team.getNextSpawn());
            }
            else {
                if (!player.hasMetadata("falling") || System.currentTimeMillis() - (player.getMetadata("falling").get(0)).asLong() >= 2000L) {
                    if (!player.hasMetadata("falling"))
                    	Utils.playSound(player, to, Sounds.BLAZE_HIT, 1f, 1f);
                    player.setMetadata("falling", new FixedMetadataValue(this.plugin, System.currentTimeMillis()));
                    this.plugin.versionManager.getTitleUtils().titlePacket(player, 0, 20, 0, "", ChatColor.RED + Language.getMessageByLanguage(PlayerData.getPlayerData(plugin, player).getLocale(), Message.OUT_OF_THE_GAME));
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 1));
            }
        }
    }
}
