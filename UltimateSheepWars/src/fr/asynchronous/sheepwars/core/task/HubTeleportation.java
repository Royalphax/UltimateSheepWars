package fr.asynchronous.sheepwars.core.task;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;

public class HubTeleportation extends BukkitRunnable
{
    private int timeUntilTeleporation;
    private final UltimateSheepWarsPlugin plugin;
    private final Player player;
    
    public HubTeleportation(final UltimateSheepWarsPlugin plugin, final Player player) {
        this.timeUntilTeleporation = 15;
        this.plugin = plugin;
        this.player = player;
        this.runTaskTimer((Plugin)plugin, 0L, 20L);
    }
    
    public void run() {
        if (this.timeUntilTeleporation == 3) {
        	teleportToLobby(this.plugin, this.player);
        }
        if (this.timeUntilTeleporation <= 0) {
        	this.player.kickPlayer(Language.getMessageByLanguage(PlayerData.getPlayerData(plugin, player).getLocale(), Message.HUB_TELEPORTATION)+ "\n\n" + Language.getMessageByLanguage(PlayerData.getPlayerData(plugin, player).getLocale(), Message.CONNECTION_FAILED));
        	this.cancel();
        }
        --this.timeUntilTeleporation;
    }
    
    public static void returnToHub(final UltimateSheepWarsPlugin plugin, final Player player) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(plugin.getConfig().getString("fallback-server-name"));
        player.sendPluginMessage((Plugin)plugin, "BungeeCord", out.toByteArray());
    }
}
