package fr.asynchronous.sheepwars.v1_11_R1.entity;

import java.lang.reflect.Field;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Sets;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.util.BlockUtils;
import net.minecraft.server.v1_11_R1.EntityHuman;
import net.minecraft.server.v1_11_R1.EntityLiving;
import net.minecraft.server.v1_11_R1.EntitySheep;
import net.minecraft.server.v1_11_R1.EnumColor;
import net.minecraft.server.v1_11_R1.EnumMoveType;
import net.minecraft.server.v1_11_R1.MathHelper;
import net.minecraft.server.v1_11_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_11_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_11_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_11_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_11_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_11_R1.PathfinderGoalSelector;

public class CustomSheep extends EntitySheep {
	private fr.asynchronous.sheepwars.core.handler.Sheeps sheep;
	private Player player;
	private net.minecraft.server.v1_11_R1.World world;
	private boolean explosion = true;
	private boolean ground;
	private long defaultTicks;
	private long ticks;
	private boolean drop;
	private int noclipDistance;
	private UltimateSheepWarsPlugin plugin;

	public CustomSheep(net.minecraft.server.v1_11_R1.World world) {
		super(world);
	}

	public CustomSheep(net.minecraft.server.v1_11_R1.World world, Player player, UltimateSheepWarsPlugin plugin) {
		super(world);
		this.player = player;
		this.plugin = plugin;
		this.world = ((CraftWorld) player.getWorld()).getHandle();
	}

	public void convertColor() {
		sheep.getColor().ordinal();
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	public CustomSheep(net.minecraft.server.v1_11_R1.World world, Player player,
			fr.asynchronous.sheepwars.core.handler.Sheeps sheep, UltimateSheepWarsPlugin plugin) {
		this(world, player, plugin);
		getNavigation();
		a(0.9F, 1.3F);

		this.sheep = sheep;
		this.ticks = (sheep.getDuration() == -1 ? Long.MAX_VALUE : sheep.getDuration() * 20);
		this.defaultTicks = this.ticks;
		this.ground = (!sheep.isOnGround());
		this.drop = sheep.isDrop();
		this.noclip = !sheep.isFriendly();
		this.noclipDistance = BlockUtils.getViewField(player, 6);// -1 ??

		setColor((EnumColor.fromColorIndex(sheep.getColor().getWoolData())));
		if (sheep != null) {
			sheep.getAction().onSpawn(player, getBukkitSheep(), plugin);
		}
		if ((sheep == fr.asynchronous.sheepwars.core.handler.Sheeps.INTERGALACTIC)
				|| (sheep == fr.asynchronous.sheepwars.core.handler.Sheeps.LIGHTNING)) {
			this.fireProof = true;
		} else if (sheep == fr.asynchronous.sheepwars.core.handler.Sheeps.SEEKER) {
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
				e.printStackTrace();
			}
			this.getNavigation();
			this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));// 1.5
			this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
			this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
			this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
			this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
		} else {
			PotionEffect effect = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1, false, false);
			getBukkitSheep().addPotionEffect(effect);
		}
	}

	@Override
	public void move(EnumMoveType enummovetype, double d0, double d1, double d2) {
		if (this.noclip && this.player.getLocation().distance(getBukkitEntity().getLocation()) >= noclipDistance) {
			this.noclip = false;
		}
		super.move(enummovetype, d0, d1, d2);
	}

	public void g(double d0, double d1, double d2) {
	}

	public void g(float sideMot, float forMot) {
		if (this.sheep != null && this.onGround && this.sheep == fr.asynchronous.sheepwars.core.handler.Sheeps.REMOTE
				&& this.passengers.size() == 1) {
			for (net.minecraft.server.v1_11_R1.Entity passenger : this.passengers) {
				if (passenger == null || !(passenger instanceof EntityHuman)
						|| this.sheep != fr.asynchronous.sheepwars.core.handler.Sheeps.REMOTE) {
					super.g(sideMot, forMot);
					this.P = 1.0f;
					this.aO = 0.02f;
					return;
				}
				this.lastYaw = (this.yaw = passenger.yaw);
				this.pitch = (passenger.pitch * 0.5F);
				setYawPitch(this.yaw, this.pitch);
				this.aN = (this.aL = this.yaw);
				sideMot = ((EntityLiving) passenger).be * 0.15F;
				forMot = ((EntityLiving) passenger).bf * 0.15F;// 0.5
				if (forMot <= 0.0F) {
					forMot *= 0.15F;// 0.25
				}

				Field jump = null;
				try {
					jump = EntityLiving.class.getDeclaredField("bd");
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

				this.aP = (bO() * 0.1F);
				if (!this.world.isClientSide) {
					l(0.35F);
					super.g(sideMot, forMot);/// !\
				}

				this.aC = this.aD;
				double d0 = this.locX - this.lastX;
				double d1 = this.locZ - this.lastZ;
				float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
				if (f4 > 1.0F) {
					f4 = 1.0F;
				}

				this.aD += (f4 - this.aD) * 0.4F;
				this.aF += this.aD;
			}
		}
		super.g(sideMot, forMot);
	}

	public void n() {
		try {
			if (this.sheep != null) {
				Location location = getBukkitEntity().getLocation();
				if (!this.ground) {
					this.ground = (this.sheep.isFriendly() || this.onGround || this.inWater);
				} else {
					if (((this.sheep.isFriendly()) || (this.ticks <= this.defaultTicks - 20L))
							&& ((this.ticks == 0L) || (this.sheep.getAction().onTicking(this.player, this.ticks,
									getBukkitSheep(), this.plugin)) || (!isAlive()))) {
						boolean death = true;
						if (getBukkitSheep().getPassenger() != null)
							getBukkitSheep().getPassenger().eject();
						if (isAlive()) {
							die();
							death = false;
						}
						this.sheep.getAction().onFinish(this.player, getBukkitSheep(), death, this.plugin);
						this.dropDeathLoot();
						return;
					}
					--this.ticks;
				}
				if (!this.onGround && this.ticksLived < 100 && !sheep.isFriendly()) {
					this.plugin.versionManager.getParticleFactory().playParticles(Particles.FIREWORKS_SPARK,
							location.add(0, 0.5, 0), 0.0F, 0.0F, 0.0F, 1, 0.0F);
				}
				this.explosion = !this.explosion;
			}
		} catch (Exception ex) {
			return;
		} finally {
			super.n();
		}
		super.n();
	}

	public void dropDeathLoot() {
		if (this.drop) {
			this.drop = false;
			if (getBukkitEntity().getLastDamageCause().getCause() == DamageCause.ENTITY_ATTACK) {
				if (getBukkitSheep().getKiller() instanceof Player) {
					PlayerData.getPlayerData(plugin, getBukkitSheep().getKiller()).increaseSheepKilled(1);
					fr.asynchronous.sheepwars.core.handler.Sheeps.giveSheep(getBukkitSheep().getKiller(), this.sheep, this.plugin);
				}
			} else if (getBukkitEntity().getLastDamageCause().getCause() == DamageCause.PROJECTILE) {
				if (getBukkitSheep().getKiller() instanceof Player) {
					PlayerData.getPlayerData(plugin, getBukkitSheep().getKiller()).increaseSheepKilled(1);
					getBukkitSheep().getWorld().dropItem(getBukkitSheep().getLocation(),
							this.sheep.getIcon(getBukkitSheep().getKiller()));
				}
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
		this.drop = false;
		getBukkitEntity().remove();
		this.plugin.versionManager.getWorldUtils().createExplosion(this.player, getBukkitSheep().getWorld(), this.locX,
				this.locY, this.locZ, power, breakBlocks, fire);
	}
}
