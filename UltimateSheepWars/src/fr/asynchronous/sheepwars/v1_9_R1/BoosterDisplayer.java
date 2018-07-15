package fr.asynchronous.sheepwars.v1_9_R1;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.DisplayColor;
import fr.asynchronous.sheepwars.core.manager.BoosterManager;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.version.IBoosterDisplayer;

public class BoosterDisplayer implements IBoosterDisplayer {

	private HashMap<BoosterManager, CustomBossBar> barMap = new HashMap<>();

	@Override
	public void startDisplay(BoosterManager booster) {
		if (!this.barMap.containsKey(booster))
			this.barMap.put(booster, new CustomBossBar(booster.getName(), booster.getDisplayColor()));
		this.barMap.get(booster).show();
	}

	@Override
	public void tickDisplay(BoosterManager booster, int duration) {
		double progress = (double)duration / (double)(booster.getDuration() * 20);
		this.barMap.get(booster).tick(progress);
	}

	@Override
	public void endDisplay(BoosterManager booster) {
		this.barMap.get(booster).hide();
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
			World world = Bukkit.getWorlds().get(0);
			for (Player online : world.getPlayers()) {
				Language lang = PlayerData.getPlayerData(online).getLanguage();
				if (!this.bossBars.containsKey(lang))
					addLanguage(lang);
				BossBar bar = this.bossBars.get(lang);
				bar.addPlayer(online);
			}
		}

		public void tick(double progress) {
			for (Entry<Language, BossBar> entry : this.bossBars.entrySet())
				entry.getValue().setProgress(progress);
		}

		public void hide() {
			World world = Bukkit.getWorlds().get(0);
			for (Player online : world.getPlayers()) {
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
