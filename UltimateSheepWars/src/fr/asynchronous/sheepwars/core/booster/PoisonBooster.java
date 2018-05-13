package fr.asynchronous.sheepwars.core.booster;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.asynchronous.sheepwars.core.handler.DisplayColor;
import fr.asynchronous.sheepwars.core.manager.BoosterManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class PoisonBooster extends BoosterManager
{
    public PoisonBooster() {
		super(MsgEnum.BOOSTER_POISON, DisplayColor.GREEN, 5);
	}

	@Override
    public boolean onStart(final Player player, final TeamManager team) {
        final TeamManager opponents = (team == TeamManager.BLUE) ? TeamManager.RED : TeamManager.BLUE;
        for (final Player opponent : opponents.getOnlinePlayers()) {
            opponent.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1));
        }
        this.setDisplayColor(DisplayColor.valueOf(opponents.getDyeColor().toString()));
        return true;
    }
    
    @Override
	public void onFinish() {
		// Do nothing
	}
}
