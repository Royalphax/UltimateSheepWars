package fr.asynchronous.sheepwars.core.booster.boosters;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.asynchronous.sheepwars.core.booster.SheepWarsBooster;
import fr.asynchronous.sheepwars.core.handler.DisplayColor;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class RegenerationBooster extends SheepWarsBooster
{
    public RegenerationBooster() {
		super(MsgEnum.BOOSTER_REGENERATION, DisplayColor.PINK, 7);
	}

	@Override
    public boolean onStart(final Player player, final TeamManager team) {
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
