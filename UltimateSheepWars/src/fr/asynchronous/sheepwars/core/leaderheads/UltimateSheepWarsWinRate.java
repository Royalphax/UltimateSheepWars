package fr.asynchronous.sheepwars.core.leaderheads;
import java.util.Arrays;

import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.handler.PlayerData;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;

public class UltimateSheepWarsWinRate extends OnlineDataCollector {

    public UltimateSheepWarsWinRate() {
        super("sheepwars-winrate", "UltimateSheepWars", BoardType.DEFAULT, "&6SheepWars > Win Rate", "opensheepwarsgui-winrate", Arrays.asList(null, null, "&e{amount}% win rate", null));
    }
    @Override
    public Double getScore(Player player) {
        return Double.parseDouble(PlayerData.getPlayerData(player).getWinRate());
    }
}