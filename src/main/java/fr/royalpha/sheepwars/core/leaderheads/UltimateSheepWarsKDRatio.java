package fr.royalpha.sheepwars.core.leaderheads;
import java.util.Arrays;

import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.api.PlayerData;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;

public class UltimateSheepWarsKDRatio extends OnlineDataCollector {

    public UltimateSheepWarsKDRatio() {
        super("sheepwars-kdratio", "UltimateSheepWars", BoardType.DEFAULT, "&6SheepWars > K/D Ratio", "opensheepwarsgui-kdratio", Arrays.asList(null, null, "&e{amount} K/D", null));
    }
    @Override
    public Double getScore(Player player) {
        return Double.parseDouble(PlayerData.getPlayerData(player).getKDRatio());
    }
}