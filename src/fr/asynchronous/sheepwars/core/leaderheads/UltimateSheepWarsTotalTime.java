package fr.asynchronous.sheepwars.core.leaderheads;
import java.util.Arrays;

import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.handler.PlayerData;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;

public class UltimateSheepWarsTotalTime extends OnlineDataCollector {

    public UltimateSheepWarsTotalTime() {
        super("sheepwars-totaltime", "UltimateSheepWars", BoardType.TIME, "&6SheepWars > Total Time", "opensheepwarsgui-totaltime", Arrays.asList(null, null, "&e{amount} total time", null));
    }
    @Override
    public Double getScore(Player player) {
        return (double) (PlayerData.getPlayerData(player).getTotalTime()/60);
    }
}