package fr.asynchronous.sheepwars.core.event.weather;

import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.api.GameState;

public class WeatherChange extends UltimateSheepWarsEventListener {
	public WeatherChange(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void on(final WeatherChangeEvent event) {
		if (event.toWeatherState() && GameState.isStep(GameState.WAITING)) // Veut dire qu'il va pleuvoir
			event.setCancelled(true);
	}
}
