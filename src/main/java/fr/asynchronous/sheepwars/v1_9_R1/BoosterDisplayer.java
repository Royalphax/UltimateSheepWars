package fr.asynchronous.sheepwars.v1_9_R1;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fr.asynchronous.sheepwars.api.Language;
import fr.asynchronous.sheepwars.api.PlayerData;
import fr.asynchronous.sheepwars.api.SheepWarsBooster;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.handler.DisplayColor;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.version.IBoosterDisplayer;

public class BoosterDisplayer implements IBoosterDisplayer {

	private static HashMap<UUID, CustomBossBar> barMap = new HashMap<>();

	@Override
	public UUID startDisplay(SheepWarsBooster booster) {
		UUID id = UUID.randomUUID();
		barMap.put(id, new CustomBossBar(booster.getName(), booster.getDisplayColor()));
		barMap.get(id).show();
		return id;
	}

	@Override
	public void tickDisplay(UUID id, int duration, int maxDuration) {
		double progress = (double)duration / (double)maxDuration;
		barMap.get(id).tick(progress);
	}

	@Override
	public void endDisplay(UUID id) {
		barMap.get(id).hide();
		barMap.remove(id);
	}

	private class CustomBossBar {

		private Message message;
		private DisplayColor color;
		private HashMap<Language, BossBar> bossBars;

		public CustomBossBar(Message msg, DisplayColor color) {
			this.message = msg;
			this.color = color;
			this.bossBars = new HashMap<>();
			for (Language lang : Language.getLanguages())
				addLanguage(lang);
		}

		public void show() {
			for (Player online : Bukkit.getOnlinePlayers()) {
				Language lang = PlayerData.getPlayerData(online).getLanguage();
				if (!this.bossBars.containsKey(lang))
					addLanguage(lang);
				BossBar bar = this.bossBars.get(lang);
				bar.addPlayer(online);
			}
		}

		public void tick(double progress) {
			for (Map.Entry<Language, BossBar> entry : this.bossBars.entrySet())
				entry.getValue().setProgress(progress);
		}

		public void hide() {
			for (Player online : Bukkit.getOnlinePlayers()) {
				Language lang = PlayerData.getPlayerData(online).getLanguage();
				if (!this.bossBars.containsKey(lang))
					addLanguage(lang);
				BossBar bar = this.bossBars.get(lang);
				bar.removePlayer(online);
			}
		}

		private void addLanguage(Language lang) {
			BossBar bar = Bukkit.createBossBar(lang.getMessage(this.message), org.bukkit.boss.BarColor.valueOf(color.toString()), BarStyle.SOLID);
			bar.setProgress(1);
			this.bossBars.put(lang, bar);
		}
	}

}
