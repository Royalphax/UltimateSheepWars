package fr.asynchronous.sheepwars.core.booster;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.asynchronous.sheepwars.core.handler.DisplayColor;
import fr.asynchronous.sheepwars.core.manager.BoosterManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class ResistanceBooster extends BoosterManager
{
    public ResistanceBooster() {
		super(MsgEnum.BOOSTER_RESISTANCE, DisplayColor.WHITE, 30);
	}

	@Override
    public boolean onStart(final Player player, final TeamManager team) {
        for (final Player teamPlayer : team.getOnlinePlayers()) {
            teamPlayer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 0));
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
