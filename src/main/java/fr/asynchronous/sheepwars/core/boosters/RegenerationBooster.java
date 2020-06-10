package fr.asynchronous.sheepwars.core.boosters;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.asynchronous.sheepwars.api.SheepWarsBooster;
import fr.asynchronous.sheepwars.core.handler.DisplayColor;
import fr.asynchronous.sheepwars.api.SheepWarsTeam;
import fr.asynchronous.sheepwars.core.message.Message.Messages;

public class RegenerationBooster extends SheepWarsBooster
{
    public RegenerationBooster() {
		super(Messages.BOOSTER_REGENERATION, DisplayColor.PINK, 7);
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
