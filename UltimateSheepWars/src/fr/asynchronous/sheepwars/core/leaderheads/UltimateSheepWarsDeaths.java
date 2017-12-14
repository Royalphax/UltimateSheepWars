package fr.asynchronous.sheepwars.core.leaderheads;
import java.util.Arrays;

import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.handler.PlayerData;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;

public class UltimateSheepWarsDeaths extends OnlineDataCollector {

    public UltimateSheepWarsDeaths() {
        super("sheepwars-deaths", "UltimateSheepWars", BoardType.DEFAULT, "&6SheepWars > Deaths", "opensheepwarsgui-deaths", Arrays.asList(null, null, "&e{amount} deaths", null));
    }
    @Override
    public Double getScore(Player player) {
        return (double) PlayerData.getPlayerData(player).getDeaths();
    }
}