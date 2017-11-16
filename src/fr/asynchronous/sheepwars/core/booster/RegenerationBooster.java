package fr.asynchronous.sheepwars.core.booster;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.asynchronous.sheepwars.core.handler.DisplayColor;
import fr.asynchronous.sheepwars.core.manager.BoosterManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class RegenerationBooster extends BoosterManager
{
    public RegenerationBooster() {
		super(MsgEnum.BOOSTER_REGENERATION, DisplayColor.WHITE, 7);
	}

	@Override
    public boolean onStart(final Player player, final TeamManager team) {
        for (final Player teamPlayer : team.getOnlinePlayers()) {
            teamPlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 140, 1));
        }
        this.setDisplayColor(DisplayColor.valueOf(team.getDyeColor().toString()));
        return true;
    }
    
    @Override
    public void onEvent(final Player player, final Event event, final BoosterManager.TriggerBoosterAction trigger) {
    	// Do nothing
    }
    
    @Override
	public void onFinish() {
		// Do nothing
	}
}
