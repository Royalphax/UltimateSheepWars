package fr.royalpha.sheepwars.core.leaderheads;
import java.util.Arrays;

import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.api.PlayerData;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;

public class UltimateSheepWarsGames extends OnlineDataCollector {

    public UltimateSheepWarsGames() {
        super("sheepwars-games", "UltimateSheepWars", BoardType.DEFAULT, "&6SheepWars > Games", "opensheepwarsgui-games", Arrays.asList(null, null, "&e{amount} games", null));
    }
    @Override
    public Double getScore(Player player) {
        return (double) PlayerData.getPlayerData(player).getGames();
    }
}