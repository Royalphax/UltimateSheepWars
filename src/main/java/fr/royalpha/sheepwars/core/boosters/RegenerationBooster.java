package fr.royalpha.sheepwars.core.boosters;

import fr.royalpha.sheepwars.api.SheepWarsTeam;
import fr.royalpha.sheepwars.core.handler.DisplayColor;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.royalpha.sheepwars.api.SheepWarsBooster;

public class RegenerationBooster extends SheepWarsBooster
{
    public RegenerationBooster() {
		super(Message.Messages.BOOSTER_REGENERATION, DisplayColor.PINK, 7);
	}

	@Override
    public boolean onStart(final Player player, final SheepWarsTeam team) {
        for (final Player teamPlayer : team.getOnlinePlayers()) {
            teamPlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 140, 1));
        }
        this.setDisplayColor(DisplayColor.valueOf(team.getDyeColor().toString()));
        return true;
    }
    
	public void onFinish() {
		// Do nothing
	}
}
