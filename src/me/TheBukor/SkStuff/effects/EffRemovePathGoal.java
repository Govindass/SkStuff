package me.TheBukor.SkStuff.effects;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nullable;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Spider;
import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.TheBukor.SkStuff.SkStuff;
import me.TheBukor.SkStuff.util.ReflectionUtils;
import me.TheBukor.SkStuff.pathfinders.PathfinderGoalFollow_v1_13_R2;
import me.TheBukor.SkStuff.pathfinders.PathfinderGoalFollow_v1_14_R1;

public class EffRemovePathGoal extends Effect {
	private Expression<LivingEntity> entities;

	private int mark;

	private Class<?> entInsent = ReflectionUtils.getNMSClass("EntityInsentient");
	private Class<?> craftLivEnt = ReflectionUtils.getOBCClass("entity.CraftLivingEntity");

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		mark = result.mark;
		entities = (Expression<LivingEntity>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "remove pathfinder goal from " + entities.toString(e, debug);
	}

	@Override
	protected void execute(Event e) {
		LivingEntity[] ents = entities.getAll(e);
		for (LivingEntity ent : ents) {
			if (ent instanceof Player || ent instanceof ArmorStand || ent == null) {
				return;
			}
			Object obcEnt = craftLivEnt.cast(ent);
			Object nmsEnt = null;
			try {
				nmsEnt = entInsent.cast(obcEnt.getClass().getMethod("getHandle").invoke(obcEnt));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
				ex.printStackTrace();
			}
			Class<?> toRemove = null;
			boolean target = false;
			boolean resetGoalTarget = false;
			if (mark == 0) {
				if (ent instanceof Rabbit) {
					toRemove = ReflectionUtils.getNMSClass("EntityRabbit$PathfinderGoalRabbitAvoidTarget");
				} else {
					toRemove = ReflectionUtils.getNMSClass("PathfinderGoalAvoidTarget");
				}
			} else if (mark == 1) {
				toRemove = ReflectionUtils.getNMSClass("PathfinderGoalBreakDoor");
			} else if (mark == 2) {
				toRemove = ReflectionUtils.getNMSClass("PathfinderGoalBreed");
			} else if (mark == 3) {
				toRemove = ReflectionUtils.getNMSClass("PathfinderGoalEatTile");
			} else if (mark == 4) {
				toRemove = ReflectionUtils.getNMSClass("PathfinderGoalFleeSun");
			} else if (mark == 5) {
				toRemove = ReflectionUtils.getNMSClass("PathfinderGoalFloat");
			} else if (mark == 6) {
				toRemove = ReflectionUtils.getNMSClass("PathfinderGoalFollowOwner");
			} else if (mark == 7) {
				toRemove = ReflectionUtils.getNMSClass("PathfinderGoalFollowParent");
			} else if (mark == 8) {
				target = true;
				toRemove = ReflectionUtils.getNMSClass("PathfinderGoalHurtByTarget");
			} else if (mark == 9) {
				toRemove = ReflectionUtils.getNMSClass("PathfinderGoalJumpOnBlock");
			} else if (mark == 10) {
				toRemove = ReflectionUtils.getNMSClass("PathfinderGoalLeapAtTarget");
			} else if (mark == 11) {
				toRemove = ReflectionUtils.getNMSClass("PathfinderGoalLookAtPlayer");
			} else if (mark == 12) {
				if (ent instanceof Spider) {
					toRemove = ReflectionUtils.getNMSClass("EntitySpider$PathfinderGoalSpiderMeleeAttack");
				} else {
					toRemove = ReflectionUtils.getNMSClass("PathfinderGoalMeleeAttack");
				}
			} else if (mark == 13) {
				if (ent instanceof Ghast) {
					toRemove = ReflectionUtils.getNMSClass("EntityGhast$PathfinderGoalMoveTowardsTarget");
				} else {
					toRemove = ReflectionUtils.getNMSClass("PathfinderGoalMoveTowardsTarget");
				}
			} else if (mark == 14) {
				target = true;
				if (ent instanceof Spider) {
					toRemove = ReflectionUtils.getNMSClass("EntitySpider$PathfinderGoalSpiderNearestAttackableTarget");
				} else {
					toRemove = ReflectionUtils.getNMSClass("PathfinderGoalNearestAttackableTarget");
				}
			} else if (mark == 15) {
				toRemove = ReflectionUtils.getNMSClass("PathfinderGoalOcelotAttack");
			} else if (mark == 16) {
				toRemove = ReflectionUtils.getNMSClass("PathfinderGoalOpenDoor");
			} else if (mark == 17) {
				if (ent instanceof Rabbit) {
					toRemove = ReflectionUtils.getNMSClass("EntityRabbit$PathfinderGoalRabbitPanic");
				} else {
					toRemove = ReflectionUtils.getNMSClass("PathfinderGoalPanic");
				}
			} else if (mark == 18) {
				toRemove = ReflectionUtils.getNMSClass("PathfinderGoalRandomLookaround");
			} else if (mark == 19) {
				toRemove = ReflectionUtils.getNMSClass("PathfinderGoalRandomStroll");
			} else if (mark == 20) {
				toRemove = ReflectionUtils.getNMSClass("PathfinderGoalSit");
			} else if (mark == 21) {
				toRemove = ReflectionUtils.getNMSClass("PathfinderGoalSwell");
			} else if (mark == 22) {
				toRemove = ReflectionUtils.getNMSClass("EntitySquid$PathfinderGoalSquid");
			} else if (mark == 23) {
				resetGoalTarget = true;
				if (ent instanceof Blaze) {
					toRemove = ReflectionUtils.getNMSClass("EntityBlaze$PathfinderGoalBlazeFireball");
				} else if (ent instanceof Ghast) {
					toRemove = ReflectionUtils.getNMSClass("EntityGhast$PathfinderGoalGhastAttackTarget");
				}
			} else if (mark == 24) {
				toRemove = ReflectionUtils.getNMSClass("EntitySilverfish$PathfinderGoalSilverfishHideInBlock");
			} else if (mark == 25) {
				toRemove = ReflectionUtils.getNMSClass("EntitySilverfish$PathfinderGoalSilverfishWakeOthers");
			} else if (mark == 26) {
				toRemove = ReflectionUtils.getNMSClass("EntityEnderman$PathfinderGoalEndermanPickupBlock");
			} else if (mark == 27) {
				toRemove = ReflectionUtils.getNMSClass("EntityEnderman$PathfinderGoalEndermanPlaceBlock");
			} else if (mark == 28) {
				target = true;
				toRemove = ReflectionUtils.getNMSClass("EntityEnderman$PathfinderGoalPlayerWhoLookedAtTarget");
			} else if (mark == 29) {
				toRemove = ReflectionUtils.getNMSClass("EntityGhast$PathfinderGoalGhastMoveTowardsTarget");
			} else if (mark == 30) {
				toRemove = ReflectionUtils.getNMSClass("EntityGhast$PathfinderGoalGhastIdleMove");
			} else if (mark == 31) {
				toRemove = ReflectionUtils.getNMSClass("PathfinderGoalTempt");
			} else if (mark == 32) {
				target = true;
				toRemove = ReflectionUtils.getNMSClass("PathfinderGoalRandomTargetNonTamed");
			} else if (mark == 33) {
				resetGoalTarget = true;
				toRemove = ReflectionUtils.getNMSClass("EntityGuardian$PathfinderGoalGuardianAttack");
			} else if (mark == 34) {
				target = true;
				toRemove = ReflectionUtils.getNMSClass("EntityPigZombie$PathfinderGoalAnger");
			} else if (mark == 35) {
				target = true;
				toRemove = ReflectionUtils.getNMSClass("EntityPigZombie$PathfinderGoalAngerOther");
			} else if (mark == 36) {
				toRemove = ReflectionUtils.getNMSClass("EntityRabbit$PathfinderGoalEatCarrots");
			} else if (mark == 37) {
				toRemove = ReflectionUtils.getNMSClass("EntityRabbit$PathfinderGoalKillerRabbitMeleeAttack");
			} else if (mark == 38) {
				toRemove = ReflectionUtils.getNMSClass("EntitySlime$PathfinderGoalSlimeRandomJump");
			} else if (mark == 39) {
				toRemove = ReflectionUtils.getNMSClass("EntitySlime$PathfinderGoalSlimeRandomDirection");
			} else if (mark == 40) {
				toRemove = ReflectionUtils.getNMSClass("EntitySlime$PathfinderGoalSlimeIdle");
			} else if (mark == 41) {
				String version = ReflectionUtils.getVersion();
				if (version.equals("v1_13_R2.")) {
					toRemove = PathfinderGoalFollow_v1_13_R2.class;
				} else if (version.equals("v1_14_R1.")) {
					toRemove = PathfinderGoalFollow_v1_14_R1.class;
				}
			} else if (mark == 42) {
				if (Skript.isRunningMinecraft(1, 9)) { 
					toRemove = ReflectionUtils.getNMSClass("PathfinderGoalBowShoot");
				} else {
					Skript.warning("The pathfinder goal \"bow shoot\" is not present in 1.8!");
					return;
				}
			}
			if (toRemove == null)
				return;

			/* "Hey, why are you setting the entity's target to null?!"
			 *
			 * For some goals (Blaze/Ghast fireball and Guardian attack), if you remove the goal while the entity is attacking, it will not stop attacking imediatelly, it will keep attacking its target.
			 * So there's a "bug" with this behavior, as soon as the entity's target resets (null, A.K.A <none>) the server crashes. Because we messed with the entity's "attack target" goal, the game
			 * still thinks it needs to get the target's location for some reason, and since the target is null... It throws an unhandled NPE (it never happens in Vanilla behavior), crashing the server.
			 * So I'm just setting the target to null before removing the goal, so it stops attacking properly, and also prevents the said crash.
			 */

			if (resetGoalTarget) {
				((Creature) ent).setTarget(null);
			}

			SkStuff.getNMSMethods().removePathfinderGoal(nmsEnt, toRemove, target);
		}
	}
}