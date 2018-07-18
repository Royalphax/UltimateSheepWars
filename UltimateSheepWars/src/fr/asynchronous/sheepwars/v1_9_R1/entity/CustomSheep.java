package fr.asynchronous.sheepwars.v1_9_R1.entity;

import java.lang.reflect.Field;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Sets;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.SheepAbility;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.manager.SheepManager;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.EntityLiving;
import net.minecraft.server.v1_9_R1.EntitySheep;
import net.minecraft.server.v1_9_R1.EnumColor;
import net.minecraft.server.v1_9_R1.MathHelper;
import net.minecraft.server.v1_9_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_9_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_9_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_9_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_9_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_9_R1.PathfinderGoalSelector;

public class CustomSheep extends EntitySheep {

	private SheepManager sheep;
	private Player player;
	private net.minecraft.server.v1_9_R1.World world;
	private boolean ground = false;
	private boolean isDead = false;
	private long ticks;
	private Plugin plugin;

	public CustomSheep(net.minecraft.server.v1_9_R1.World world) {
		super(world);
	}

	public CustomSheep(net.minecraft.server.v1_9_R1.World world, Player player, Plugin plugin) {
		super(world);
		this.player = player;
		this.plugin = plugin;
		this.world = ((CraftWorld) player.getWorld()).getHandle();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public CustomSheep(net.minecraft.server.v1_9_R1.World world, Player player, SheepManager sheep, Plugin plugin) {
		this(world, player, plugin);

		this.sheep = sheep;
		this.ticks = sheep.getDuration() <= 0 ? Long.MAX_VALUE : sheep.getDuration() * 20;

		setColor(EnumColor.valueOf(sheep.getColor().toString()));
		sheep.onSpawn(player, getBukkitSheep(), plugin);

		if (sheep.getAbilities().contains(SheepAbility.FIRE_PROOF))
			this.fireProof = true;
		if (sheep.getAbilities().contains(SheepAbility.SEEK_PLAYERS)) {
			try {
				Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
				bField.setAccessible(true);
				Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
				cField.setAccessible(true);
				bField.set(this.goalSelector, Sets.newLinkedHashSet());
				bField.set(this.targetSelector, Sets.newLinkedHashSet());
				cField.set(this.goalSelector, Sets.newLinkedHashSet());
				cField.set(this.targetSelector, Sets.newLinkedHashSet());
			} catch (Exception e) {
				new ExceptionManager(e).register(true);
			}
			this.getNavigation();
			this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));
			this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
			this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
			this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
			this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
		} else {
			PotionEffect effect = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1, false, false);
			getBukkitSheep().addPotionEffect(effect);
		}
	}

	/**@Override
	public void move(double d0, double d1, double d2) {
		Location newLocation = this.getBukkitEntity().getLocation().clone().add(d0, d1, d2);
		this.noclip = newLocation.getBlock().getType() == Material.AIR;
		super.move(d0, d1, d2);
	}**/

	@Override
	public void g(float sideMot, float forMot) {
		if (this.sheep != null && this.sheep.getAbilities().contains(SheepAbility.RIDEABLE) && this.onGround && this.passengers.size() == 1) {
			final Entity passenger = this.passengers.get(0);
			if (passenger == null || !(passenger instanceof EntityHuman)) {
				super.g(sideMot, forMot);
				this.P = 1.0f;
				this.aO = 0.02f;
				return;
			}
			this.lastYaw = (this.yaw = passenger.yaw);
			this.pitch = (passenger.pitch * 0.5F);
			setYawPitch(this.yaw, this.pitch);
			this.aM = (this.aK = this.yaw);
			sideMot = ((EntityLiving) passenger).bd * 0.15F;
			forMot = ((EntityLiving) passenger).be * 0.15F;// 0.5
			if (forMot <= 0.0F) {
				forMot *= 0.15F;// 0.25
			}

			Field jump = null;
			try {
				jump = EntityLiving.class.getDeclaredField("bc");
			} catch (NoSuchFieldException localNoSuchFieldException) {
			} catch (SecurityException localSecurityException) {
			}

			jump.setAccessible(true);

			if ((jump != null) && (this.onGround)) {
				try {
					if (jump.getBoolean(passenger)) {
						double jumpHeight = 0.5D;

						this.motY = jumpHeight;
					}
				} catch (IllegalAccessException localIllegalAccessException) {
				}

			}

			this.P = 1.0F;

			this.aO = (bK() * 0.1F);
			if (!this.world.isClientSide) {
				l(0.35F);
				super.g(sideMot, forMot);
			}

			this.aB = this.aC;
			double d0 = this.locX - this.lastX;
			double d1 = this.locZ - this.lastZ;
			float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
			if (f4 > 1.0F) {
				f4 = 1.0F;
			}

			this.aC += (f4 - this.aC) * 0.4F;
			this.aE += this.aC;
		}
		super.g(sideMot, forMot);
	}

	@Override
	public void n() {
		try {
			if (this.sheep != null) {
				/** On gère les particules **/
				if ((this.onGround || this.inWater || this.sheep.isFriendly()) && !this.ground)
					this.ground = true;
				if (!this.ground) {
					UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.FIREWORKS_SPARK, getBukkitEntity().getLocation().add(0, 0.5, 0), 0.0F, 0.0F, 0.0F, 1, 0.0F);
				} else if (!this.isDead && (this.ticks <= 0 || !isAlive() || this.sheep.onTicking(this.player, this.ticks, getBukkitSheep(), this.plugin))) {
					this.isDead = true;
					boolean death = true;
					if (!this.passengers.isEmpty())
						for (Entity ent : this.passengers)
							ent.getBukkitEntity().eject();
					if (isAlive()) {
						die();
						death = false;
					}
					this.sheep.onFinish(this.player, getBukkitSheep(), death, this.plugin);
					if (death)
						this.dropDeathLoot();
					return;
				}
				this.ticks--;
			}
		} catch (Exception ex) {
			return;
		} finally {
			super.n();
		}
	}

	public void dropDeathLoot() {
		if (this.sheep.isDropAllowed()) {
			final Player killer = getBukkitSheep().getKiller();
			final DamageCause damageCause = getBukkitEntity().getLastDamageCause().getCause();
			if (damageCause == DamageCause.ENTITY_ATTACK) {
				if (getBukkitSheep().getKiller() instanceof Player) {
					PlayerData.getPlayerData(killer).increaseSheepKilled(1);
					SheepManager.giveSheep(killer, this.sheep);
				}
			} else {
				Location location = getBukkitEntity().getLocation();
				location.getWorld().dropItemNaturally(location, this.sheep.getIcon(killer));
			}
		}
	}

	public Player getPlayer() {
		return this.player;
	}

	public org.bukkit.entity.Sheep getBukkitSheep() {
		return (org.bukkit.entity.Sheep) getBukkitEntity();
	}

	public void explode(float power) {
		explode(power, true, false);
	}

	public void explode(float power, boolean fire) {
		explode(power, true, fire);
	}

	public void explode(float power, boolean breakBlocks, boolean fire) {
		getBukkitEntity().remove();
		UltimateSheepWarsPlugin.getVersionManager().getWorldUtils().createExplosion(this.player, getBukkitSheep().getWorld(), this.locX, this.locY, this.locZ, power, breakBlocks, fire);
	}
}
