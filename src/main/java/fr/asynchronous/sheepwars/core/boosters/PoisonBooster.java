package fr.asynchronous.sheepwars.core.boosters;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.asynchronous.sheepwars.api.SheepWarsBooster;
import fr.asynchronous.sheepwars.core.handler.DisplayColor;
import fr.asynchronous.sheepwars.api.SheepWarsTeam;
import fr.asynchronous.sheepwars.core.message.Message.Messages;

public class PoisonBooster extends SheepWarsBooster
{
    public PoisonBooster() {
		super(Messages.BOOSTER_POISON, DisplayColor.GREEN, 5);
	}

	@Override
    public boolean onStart(final Player player, final SheepWarsTeam team) {
        final SheepWarsTeam opponents = (team == SheepWarsTeam.BLUE) ? SheepWarsTeam.RED : SheepWarsTeam.BLUE;
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
