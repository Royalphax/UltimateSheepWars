package fr.asynchronous.sheepwars.core.leaderheads;
import java.util.Arrays;

import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.data.PlayerData;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;

public class UltimateSheepWarsWins extends OnlineDataCollector {

    public UltimateSheepWarsWins() {
        super("sheepwars-wins", "UltimateSheepWars", BoardType.DEFAULT, "&6SheepWars > Wins", "opensheepwarsgui-wins", Arrays.asList(null, null, "&e{amount} wins", null));
    }
    @Override
    public Double getScore(Player player) {
        return (double) PlayerData.getPlayerData(player).getWins();
    }
}