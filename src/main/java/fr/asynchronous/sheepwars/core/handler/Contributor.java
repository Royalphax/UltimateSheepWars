package fr.asynchronous.sheepwars.core.handler;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.handler.Particles.ParticleEffect;

public enum Contributor {
	
	ROYALPHA("Royalpha", "711e5a53-14a1-409b-bf48-7e8c1cc22440", ChatColor.RED + "Lead-Developer " + ChatColor.YELLOW, 4, ParticleEffect.FLAME_RINGS, "Effet actif: " + ChatColor.GREEN + "Flame Rings"),
	MATHIEUAR("MathieuAR", "f47cb903-e981-448d-b0ea-2099adcfce86", ChatColor.LIGHT_PURPLE + "Friend " + ChatColor.YELLOW, 1, ParticleEffect.FLAME_SPIRAL, "En remerciement de ton aide incroyable dans de nombreux domaines et de tout nos délires, je t'offre un effet de particule."),
	GHOSTRIDER584("GhostRider584", "a5a61038-3281-457a-84e7-8bbb1c0c62b5", ChatColor.LIGHT_PURPLE + "Friend " + ChatColor.YELLOW, 1, ParticleEffect.ENCHANT, "En remerciement de ton aide indispensable et de toutes tes astuces de programmation qui me facilitent bien la vie, je t'offre un effet de particule."),
	VIPEERS("Vipeers", "da6610cb-3f5b-4638-aaf7-e6bb8fa1c22a", ChatColor.LIGHT_PURPLE + "Friend " + ChatColor.YELLOW, 3, ParticleEffect.WITCH_SPIRAL, "Effet actif: " + ChatColor.GREEN + "Witch Spiral"),
	ISIZ_("IsiZ_", "fb053956-f24e-4ef5-aa2a-747575701858", ChatColor.RED + "Co-Developer " + ChatColor.YELLOW, 3, ParticleEffect.SHADOW_WALK, "Salut bg! Amuses-toi bien."),
	FINOWAY("Finoway", "aa42d5e0-a244-4da8-84df-95138f8e7371", "", 1, ParticleEffect.FLAME_SPIRAL, "Salut Finoway! Tu m'as bien aidé pour le plugin, je te remercie en t'offrant un effet de particule."),
	MILOWGAMING("MilowGaming", "5c09ddc7-1186-4338-97eb-baf9c11da551", "", 1, ParticleEffect.ENCHANT, "Salut milowgaming, merci pour ton aide d'un soir. Je t'offre l'effet enchanteur !"),
	JJORDA6985("6985jjorda", "f884fabc-3c99-4986-b0d7-9865d9f3c0e9", "", 1, ParticleEffect.SHADOW_WALK, "Hey Jjorda! thanks for all your help for this plugin. I give you the Shadow Walk effect, enjoy !"),
	SHIKA258("shika258", "f053b7b3-40d9-4edf-b4b6-c2f0ba4a0f93", "", 1, ParticleEffect.SHADOW_WALK, "Salut Shika! Merci pour tes rapports de bugs. En échange, je t'offre le Shadow Walk effet, amuses-toi bien !"),
	HAKOGAMING("HakoGaming", "107bb14a-f394-4581-aaa2-33feffebb420", "", 1, ParticleEffect.FLAME_CIRCLE, "Hey Hako! Merci pour toute ton aide. En échange, je t'offre le Flame Circle effet, amuses-toi bien !"),
	LUMINATI_MC("NovaXCIV", "ce320d7a-2ed3-4fdf-8241-7e07b1a3ac73", "", 1, ParticleEffect.FLAME_CIRCLE, "Hi NovaXCIV! Thanks for all your help since the begining. I give you the Flame Circle effect. Enjoy !"),
	COCTLE("Coctle", "b9c67c2f-2008-45d8-ba9c-1798b8bf52b0", ChatColor.LIGHT_PURPLE + "Friend " + ChatColor.YELLOW, 3, ParticleEffect.WITCH_SPIRAL, "Effet actif: " + ChatColor.GREEN + "Witch Spiral");

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
		return getContributor(player) != null;
	}
	
	public static Boolean isImportant(Player player)
	{
		return (!isContributor(player) ? false : (getContributor(player).getLevel() > 2 ? true : false));
	}
	
	public static Boolean isDeveloper(Player player)
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
		if (getContributor(player) != null)
		{
			return getContributor(player).getPrefix();
		}
		return "";
	}
}
