package fr.royalpha.sheepwars.core.leaderheads;
import java.util.Arrays;

import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.api.PlayerData;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;

public class UltimateSheepWarsSheepThrown extends OnlineDataCollector {

    public UltimateSheepWarsSheepThrown() {
        super("sheepwars-sheepthrown", "UltimateSheepWars", BoardType.DEFAULT, "&6SheepWars > Sheep Thrown", "opensheepwarsgui-sheepthrown", Arrays.asList(null, null, "&e{amount} sheep thrown", null));
    }
    @Override
    public Double getScore(Player player) {
        return (double) PlayerData.getPlayerData(player).getSheepThrown();
    }
}