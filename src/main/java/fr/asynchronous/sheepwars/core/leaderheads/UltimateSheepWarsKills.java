package fr.asynchronous.sheepwars.core.leaderheads;
import java.util.Arrays;

import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.api.PlayerData;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;

public class UltimateSheepWarsKills extends OnlineDataCollector {

    public UltimateSheepWarsKills() {
        super("sheepwars-kills", "UltimateSheepWars", BoardType.DEFAULT, "&6SheepWars > Kills", "opensheepwarsgui-kills", 
        		Arrays.asList(null, null, "&e{amount} kills", null));
    }
    @Override
    public Double getScore(Player player) {
        return (double) PlayerData.getPlayerData(player).getKills();
    }
}