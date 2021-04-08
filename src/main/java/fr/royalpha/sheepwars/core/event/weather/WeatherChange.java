package fr.royalpha.sheepwars.core.event.weather;

import fr.royalpha.sheepwars.api.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;

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
