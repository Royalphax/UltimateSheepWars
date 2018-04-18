package fr.asynchronous.sheepwars.core.kit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.util.MathUtils;

public class BetterSwordKit extends KitManager {

	public BetterSwordKit() {
		super(0, MsgEnum.KIT_BETTER_SWORD_NAME, MsgEnum.KIT_BETTER_SWORD_DESCRIPTION, "sheepwars.kit.bettersword", 10, 10, new ItemBuilder(Material.STONE_SWORD), TriggerKitAction.PLAYER_DAMAGE);
	}

	@Override
	public boolean onEquip(Player player) {
		return false;
	}

	@Override
	public void onEvent(Player player, Event event, TriggerKitAction triggerAction) {
		if (triggerAction == TriggerKitAction.PLAYER_DAMAGE && MathUtils.randomBoolean(0.05f)) {
			EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) event;
			ev.setCancelled(true);
			player.damage(ev.getFinalDamage() * 1.5, ev.getDamager());
		}
		
	}

}
