package fr.asynchronous.sheepwars.v1_9_R1.entity;

import java.lang.reflect.Field;

import com.google.common.collect.Sets;
import fr.asynchronous.sheepwars.api.PlayerData;
import fr.asynchronous.sheepwars.api.SheepWarsSheep;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.util.UnsafeList;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.SheepAbility;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
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

	private SheepWarsSheep sheep;
	private Player player;
	private boolean ground = false;
	private boolean upComingCollision = false;
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
	public CustomSheep(net.minecraft.server.v1_9_R1.World world, Player player, SheepWarsSheep sheep, Plugin plugin) {
		this(world, player, plugin);
		getNavigation();
		a(0.9F, 1.3F);

		this.sheep = sheep;
		this.ticks = sheep.getDuration() <= 0 ? Long.MAX_VALUE : sheep.getDuration() * 20;

		setColor(EnumColor.valueOf(sheep.getColor().toString()));
		sheep.onSpawn(player, getBukkitSheep(), plugin);

		if (sheep.hasAbility(SheepAbility.FIRE_PROOF))
			this.fireProof = true;
		if (sheep.hasAbility(SheepAbility.SEEK_PLAYERS)) {
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
				ExceptionManager.register(e, true);
			}
			this.getNavigation();
			this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));
			this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
			this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
			this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
			this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
		}
	}

	private static final int RADIUS = 2;
	private static final int SPEED_DIVIDER = 20;
	private static double relativeX = 0.0;
	private static double relativeY = 0.0;
	private static double relativeZ = 0.0;
	
	private boolean collidePlayer = false;

	//private PacketPlayOutAttachEntity boardingPacket; // To fix issue with passenger player on boarding sheep

	@Override
	public void move(double d0, double d1, double d2) {
		if (this.sheep != null) {
			/*if (sheep.hasAbility(SheepAbility.CONTROLLABLE) && getBukkitEntity().getPassenger() != null) {
				if (boardingPacket == null)
					boardingPacket = new PacketPlayOutAttachEntity(0, ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle(), this);
				((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(boardingPacket);
			}*/
			final Location from = new Location(this.getBukkitEntity().getWorld(), this.locX, this.locY, this.locZ);
			final Location to = from.clone().add(this.motX, this.motY, this.motZ);
			if (!this.ground && !this.sheep.isFriendly()) {
				final Vector dir = to.subtract(from).toVector();
				Vector copy = dir.clone();
				boolean noclip = true;
				for (double i = 0; i <= 1; i += 0.2) {
					copy.multiply(i);
					final Location loc = from.clone().add(copy);
					SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.FIREWORKS_SPARK, from.clone().add(0, 1, 0), 0.0F, 0.0F, 0.0F, 1, 0.0F);
					final Location frontLoc = loc.clone().add(copy.clone().normalize().multiply(RADIUS));
					if (!this.upComingCollision && frontLoc.getBlock().getType() != Material.AIR) { // Check pour futur collision
						this.upComingCollision = true;
						relativeX = d0 / (double) SPEED_DIVIDER;
						relativeY = d1 / (double) SPEED_DIVIDER;
						relativeZ = d2 / (double) SPEED_DIVIDER;
					}
					if (loc.getBlock().getType() != Material.AIR) {
						noclip = false;
						break;
					}
					copy = dir.clone();
				}
				if (!collidePlayer && ConfigManager.getBoolean(fr.asynchronous.sheepwars.core.manager.ConfigManager.Field.ENABLE_SHEEP_PLAYER_COLLISION)) {
					for (Entity ent : from.getWorld().getNearbyEntities(from.clone(), 0.5d, 1d, 0.5d)) { // Check pour la collision d'un joueur
						if (ent instanceof Player && !((Player) ent).getName().equals(this.player.getName()) && ((Player) ent).getGameMode() != GameMode.SPECTATOR) {
							if (!collidePlayer) {
								collidePlayer = true;
								this.upComingCollision = true;
								relativeX = d0 / (double) SPEED_DIVIDER;
								relativeY = d1 / (double) SPEED_DIVIDER;
								relativeZ = d2 / (double) SPEED_DIVIDER;
								getBukkitEntity().setVelocity(new Vector().zero());
							}
							Sounds.playSoundAll(ent.getLocation(), Sounds.DIG_WOOL, 1f, 0.5f);
							final Vector vect = ent.getLocation().clone().subtract(from.clone()).toVector().multiply(0.5);
							SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.FIREWORKS_SPARK, from.clone().add(0, 1, 0).add(vect), 0F, 0F, 0F, 8, 0.1F);
							ent.setVelocity(new Vector(d0, d1, d2));
							Sounds.SHEEP_IDLE.playSound((Player) ent, 1f, 1.3f);
						}
					}
				}
				this.noclip = noclip;
			}
			if (this.sheep.hasAbility(SheepAbility.EAT_BLOCKS) && this.upComingCollision) {
				if (!this.ground)
					this.ground = true;
				d0 = relativeX;
				d1 = relativeY;
				d2 = relativeZ;
			}
		}
		super.move(d0, d1, d2);
	}

	@Override
	public void g(float sideMot, float forMot) {
		if (this.sheep != null && this.sheep.getAbilities().contains(SheepAbility.CONTROLLABLE) && this.onGround && this.passengers.size() == 1) {
			final net.minecraft.server.v1_9_R1.Entity passenger = this.passengers.get(0);
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
				if ((this.onGround || this.inWater || this.sheep.isFriendly()) && !this.ground) {
					this.ground = true;
					if (this.sheep.hasAbility(SheepAbility.DISABLE_SLIDE)) {
						this.motX = 0;
						this.motY = 0;
						this.motZ = 0;
					}
				}
				if (this.ground) {
					if (!this.isDead && (this.ticks <= 0 || !isAlive() || this.sheep.onTicking(this.player, this.ticks, getBukkitSheep(), this.plugin))) {
						this.isDead = true;
						boolean death = true;
						if (!this.passengers.isEmpty())
							for (net.minecraft.server.v1_9_R1.Entity ent : this.passengers)
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
					SheepWarsSheep.giveSheep(killer, this.sheep);
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
		SheepWarsPlugin.getVersionManager().getWorldUtils().createExplosion(this.player, getBukkitSheep().getWorld(), this.locX, this.locY, this.locZ, power, breakBlocks, fire);
	}
}
