package fr.royalpha.sheepwars.core.leaderheads;
import java.util.Arrays;

import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.api.PlayerData;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;

public class UltimateSheepWarsSheepKilled extends OnlineDataCollector {

    public UltimateSheepWarsSheepKilled() {
        super("sheepwars-sheepkilled", "UltimateSheepWars", BoardType.DEFAULT, "&6SheepWars > Sheep Killed", "opensheepwarsgui-sheepkilled", Arrays.asList(null, null, "&e{amount} sheep killed", null));
    }
    @Override
    public Double getScore(Player player) {
        return (double) PlayerData.getPlayerData(player).getSheepKilled();
    }
}