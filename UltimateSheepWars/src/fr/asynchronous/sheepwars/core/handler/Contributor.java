package fr.asynchronous.sheepwars.core.handler;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.particle.EnchantEffect;
import fr.asynchronous.sheepwars.core.particle.FlameCircleEffect;
import fr.asynchronous.sheepwars.core.particle.FlameRingsEffect;
import fr.asynchronous.sheepwars.core.particle.FlameSpiralEffect;
import fr.asynchronous.sheepwars.core.particle.ShadowWalkEffect;
import fr.asynchronous.sheepwars.core.particle.WitchSpiralEffect;
import fr.asynchronous.sheepwars.core.task.ParticleTask;

public enum Contributor {
	
	ROYTREO28("Roytreo28", "711e5a53-14a1-409b-bf48-7e8c1cc22440", ChatColor.RED + "Développeur " + ChatColor.YELLOW, 4, ParticleEffect.FLAME_RINGS, "Effet actif: " + ChatColor.GREEN + "Flame Rings"),
	DROLEUR("Droleur", "da6610cb-3f5b-4638-aaf7-e6bb8fa1c22a", ChatColor.LIGHT_PURPLE + "Contributeur " + ChatColor.YELLOW, 3, ParticleEffect.ENCHANT, "Effet actif: " + ChatColor.GREEN + "Witch Spiral"),
	KINGRIDER26("KingRider26", "fb053956-f24e-4ef5-aa2a-747575701858", ChatColor.LIGHT_PURPLE + "Contributeur " + ChatColor.YELLOW, 3, ParticleEffect.FLAME_RINGS, "Salut KingRider! Etant un des rares developpeurs que j'apprécie et que j'aide, je t'offre l'effet anneaux de feu en jeu."),
	FINOWAY("Finoway", "aa42d5e0-a244-4da8-84df-95138f8e7371", "", 1, ParticleEffect.FLAME_SPIRAL, "Salut Finoway! Tu m'as bien aidé pour le plugin, je te remercie en t'offrant un effet de particule."),
	MILOWGAMING("milowgaming", "5c09ddc7-1186-4338-97eb-baf9c11da551", "", 1, ParticleEffect.ENCHANT, "Salut milowgaming, merci pour ton aide d'un soir. Je t'offre l'effet enchanteur !"),
	THE_GUNTH("TheGunth", "aa8f4b05-808e-4bbf-a844-6ce7bc7afe2c", "", 1, ParticleEffect.ENCHANT, "Salut TheGunth, merci pour ton aide d'un soir. Je t'offre l'effet enchanteur !"),
	JJORDA6985("6985jjorda", "f884fabc-3c99-4986-b0d7-9865d9f3c0e9", "", 1, ParticleEffect.SHADOW_WALK, "Hey Jjorda! thanks for all your help for this plugin. I give you the Shadow Walk effect, enjoy !"),
	SHIKA258("shika258", "f053b7b3-40d9-4edf-b4b6-c2f0ba4a0f93", "", 1, ParticleEffect.SHADOW_WALK, "Salut Shika! Merci pour tes rapports de bugs. En échange, je t'offre le Shadow Walk effet, amuses-toi bien !"),
	HAKOGAMING("Hakogaming", "107bb14a-f394-4581-aaa2-33feffebb420", "", 1, ParticleEffect.FLAME_CIRCLE, "Hey Hako! Merci pour toute ton aide. En échange, je t'offre le Flame Circle effet, amuses-toi bien !"),
	LUMINATI_MC("NovaXCIV", "ce320d7a-2ed3-4fdf-8241-7e07b1a3ac73", "", 1, ParticleEffect.FLAME_CIRCLE, "Hi NovaXCIV! Thanks for all your help since the begining. I give you the Flame Circle effect. Enjoy !"),
	COCTLE("Coctle", "b9c67c2f-2008-45d8-ba9c-1798b8bf52b0", ChatColor.LIGHT_PURPLE + "Contributeur " + ChatColor.YELLOW, 3, ParticleEffect.WITCH_SPIRAL, "Effet actif: " + ChatColor.GREEN + "Witch Spiral");

	private String name;
	private UUID uuid;
	private String prefix;
	private String specialmessage;
	private ParticleEffect effect;
	private int level;
	private Boolean effect_active;
	private Contributor(String name, String id, String prefix, int level, ParticleEffect effect, String specialmessage)
	{
		this.name = name;
		this.uuid = UUID.fromString(id);
		this.prefix = prefix;
		this.level = level;
		this.effect = effect;
		this.specialmessage = specialmessage;
		this.effect_active = true;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public Boolean isEffectActive()
	{
		return this.effect_active;
	}
	
	public String getSpecialMessage()
	{
		return this.specialmessage;
	}
	
	public int getLevel()
	{
		return this.level;
	}
	
	public UUID getUUID()
	{
		return this.uuid;
	}
	public String getPrefix()
	{
		return this.prefix;
	}
	
	public ParticleEffect getEffect()
	{
		return this.effect;
	}
	
	public void setEffectActive(Boolean b)
	{
		this.effect_active = b;
	}
	
	public static Boolean isContributor(Player player)
	{
		if (Bukkit.getServer().getOnlineMode())
		{
			for (Contributor c : values())
			{
				if ((c.getUUID().toString().equals(player.getUniqueId().toString())))
					return true;
			}
		} else {
			for (Contributor c : values())
			{
				if ((c.getName().equals(player.getName())))
					return true;
			}
		}
		return false;
	}
	
	public static Boolean isImportant(Player player)
	{
		return (!isContributor(player) ? false : (getContributor(player).getLevel() > 2 ? true : false));
	}
	
	public static Boolean isAdministrator(Player player)
	{
		return (!isContributor(player) ? false : (getContributor(player).getLevel() > 3 ? true : false));
	}
	
	public static Contributor getContributor(Player player)
	{
		if (Bukkit.getServer().getOnlineMode())
		{
			for (Contributor c : values())
			{
				if ((c.getUUID().toString().equals(player.getUniqueId().toString())) && (c.getName().equals(player.getName())))
					return c;
			}
		} else {
			for (Contributor c : values())
			{
				if ((c.getName().equals(player.getName())))
					return c;
			}
		}
		return null;
	}
	
	public static String getPrefix(Player player)
	{
		if (isImportant(player) && (getContributor(player) != null))
		{
			return getContributor(player).getPrefix();
		}
		return "";
	}
	
	public static enum ParticleEffect {
		
		FLAME_RINGS((ParticleEffectType)new FlameRingsEffect(), 0),
		FLAME_CIRCLE((ParticleEffectType)new FlameCircleEffect(), 0),
		FLAME_SPIRAL((ParticleEffectType)new FlameSpiralEffect(), 0),
		WITCH_SPIRAL((ParticleEffectType)new WitchSpiralEffect(), 0),
		ENCHANT((ParticleEffectType)new EnchantEffect(), 5),
		SHADOW_WALK((ParticleEffectType)new ShadowWalkEffect(), 8);

		private ParticleEffectType action;
		private int ticks;
		
		private ParticleEffect(ParticleEffectType action, int ticks)
		{
			this.action = action;
			this.ticks = ticks;
		}
		
		public ParticleEffectType getAction()
		{
			return this.action;
		}
		
		public int getTicks()
		{
			return this.ticks;
		}
		
		public interface ParticleEffectType
	    {
	        void update(Player player, Boolean moving);
	    }
		
		public static void equipEffect(Player player, UltimateSheepWarsPlugin plugin)
		{
			if (isContributor(player))
				new ParticleTask(getContributor(player).getEffect(), player, plugin);
		}
	}
}
