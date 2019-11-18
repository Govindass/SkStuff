package me.TheBukor.SkStuff.effects;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import ch.njol.skript.Skript;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import me.TheBukor.SkStuff.SkStuff;
import me.TheBukor.SkStuff.pathfinders.PathfinderGoalFollow_v1_13_R2;
import me.TheBukor.SkStuff.pathfinders.PathfinderGoalFollow_v1_14_R1;
import me.TheBukor.SkStuff.util.ReflectionUtils;
import net.minecraft.server.v1_8_R3.EntityCreature;

public final class EffSetPathGoal extends Effect {
	private Expression<Integer> goalPriority;
	private Expression<? extends EntityData<?>> typeToAvoid;
	private Expression<Number> avoidRadius;
	private Expression<Number> avoidSpeed;
	private Expression<Number> avoidSpeedNear;
	private Expression<Number> breedSpeed;
	private Expression<Number> fleeSunSpeed;
	private Expression<Number> followOwnerSpeed;
	private Expression<Number> followMinDist;
	private Expression<Number> followMaxDist;
	private Expression<Number> followAdultsSpeed;
	private Expression<? extends EntityData<?>> typesToFightBack;
	private Expression<Boolean> callForHelp;
	private Expression<Number> jumpOnBlockSpeed;
	private Expression<Number> leapHeight;
	private Expression<? extends EntityData<?>> lookType;
	private Expression<Number> lookRadius;
	private Expression<? extends EntityData<?>> meleeTarget;
	private Expression<Number> meleeSpeed;
	private Expression<Boolean> meleeMemorize;
	private Expression<Number> moveTargetSpeed;
	private Expression<Number> moveTargetRadius;
	private Expression<? extends EntityData<?>> nearTarget;
	private Expression<Boolean> checkSight;
	private Expression<Number> panicSpeed;
	private Expression<Number> randomWalkSpeed;
	private Expression<Timespan> randomWalkInterval;
	private Expression<ItemStack> temptItem;
	private Expression<Number> temptSpeed;
	private Expression<Boolean> temptScared;
	private Expression<? extends EntityData<?>> nonTamedTarget;
	private Expression<? extends EntityData<?>> followedTypes;
	private Expression<Number> followedRadius;
	private Expression<Number> followedSpeed;
	private Expression<String> followedName;
	private Expression<Number> bowShootMoveSpeed;
	private Expression<Number> bowShootUnkParam;
	private Expression<Number> bowShootFollowRange;
	private Expression<LivingEntity> entities;

	private int mark;

	private Class<?> craftItemClass = ReflectionUtils.getOBCClass("inventory.CraftItemStack");
	private Class<?> craftLivEnt = ReflectionUtils.getOBCClass("entity.CraftLivingEntity");
	private Class<?> entAnimal = ReflectionUtils.getNMSClass("EntityAnimal");
	private Class<?> entCreature = ReflectionUtils.getNMSClass("EntityCreature");
	private Class<?> entInsent = ReflectionUtils.getNMSClass("EntityInsentient");
	private Class<?> entTameable = ReflectionUtils.getNMSClass("EntityTameableAnimal");
	private Class<?> nmsItemClass = ReflectionUtils.getNMSClass("Item");

	@SuppressWarnings("unchecked")
	@Override
	public final boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		goalPriority = (Expression<Integer>) expr[0];
		mark = result.mark;
		if (mark == 0) {
			typeToAvoid = (Expression<EntityData<?>>) expr[1];
			avoidRadius = (Expression<Number>) expr[2];
			avoidSpeed = (Expression<Number>) expr[3];
			avoidSpeedNear = (Expression<Number>) expr[4];
		} else if (mark == 2) {
			breedSpeed = (Expression<Number>) expr[5];
		} else if (mark == 4) {
			fleeSunSpeed = (Expression<Number>) expr[6];
		} else if (mark == 6) {
			followOwnerSpeed = (Expression<Number>) expr[7];
			followMinDist = (Expression<Number>) expr[8];
			followMaxDist = (Expression<Number>) expr[9];
		} else if (mark == 7) {
			followAdultsSpeed = (Expression<Number>) expr[10];
		} else if (mark == 8) {
			typesToFightBack = (Expression<EntityData<?>>) expr[11];
			callForHelp = (Expression<Boolean>) expr[12];
		} else if (mark == 9) {
			jumpOnBlockSpeed = (Expression<Number>) expr[13];
		} else if (mark == 10) {
			leapHeight = (Expression<Number>) expr[14];
		} else if (mark == 11) {
			lookType = (Expression<EntityData<?>>) expr[15];
			lookRadius = (Expression<Number>) expr[16];
		} else if (mark == 12) {
			meleeTarget = (Expression<EntityData<?>>) expr[17];
			meleeSpeed = (Expression<Number>) expr[18];
			meleeMemorize = (Expression<Boolean>) expr[19];
		} else if (mark == 13) {
			moveTargetSpeed = (Expression<Number>) expr[20];
			moveTargetRadius = (Expression<Number>) expr[21];
		} else if (mark == 14) {
			nearTarget = (Expression<EntityData<?>>) expr[22];
			checkSight = (Expression<Boolean>) expr[23];
		} else if (mark == 17) {
			panicSpeed = (Expression<Number>) expr[24];
		} else if (mark == 19) {
			randomWalkSpeed = (Expression<Number>) expr[25];
			randomWalkInterval = (Expression<Timespan>) expr[26];
		} else if (mark == 31) {
			temptItem = (Expression<ItemStack>) expr[27];
			temptSpeed = (Expression<Number>) expr[28];
			temptScared = (Expression<Boolean>) expr[29];
		} else if (mark == 32) {
			nonTamedTarget = (Expression<EntityData<?>>) expr[30];
		} else if (mark == 41) {
			followedTypes = (Expression<EntityData<?>>) expr[31];
			followedRadius = (Expression<Number>) expr[32];
			followedSpeed = (Expression<Number>) expr[33];
			followedName = (Expression<String>) expr[34];
		} else if (mark == 42) {
			bowShootMoveSpeed = (Expression<Number>) expr[35];
			bowShootUnkParam = (Expression<Number>) expr[36];
			bowShootFollowRange = (Expression<Number>) expr[37];
		}
		entities = (Expression<LivingEntity>) expr[38];
		/* Debug stuff, just in case I need it
		int i = 0;
		for (Expression<?> expression : expr) {
			if (expression == null) {
				Skript.info("Expression index " + i + " is null.");
			} else {
				Skript.info("Expression index " + i + " is " + expression.toString());
			}
			i++;
		}
		*/
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "add pathfinder goal to" + entities.toString(e, debug);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void execute(Event e) {
		LivingEntity[] ents = entities.getAll(e);	
		for (LivingEntity ent : ents) {
			if (ent == null || ent instanceof Player || ent instanceof ArmorStand)
				return;
			int priority = (goalPriority == null ? 4 : goalPriority.getSingle(e).intValue());

			if (priority < 0)
				priority = 0;
			else if (priority > 9)
				priority = 9;

			Object obcEnt = craftLivEnt.cast(ent);
			Object nmsEnt = null;
			boolean target = false;
			List<Object> newGoals = new ArrayList<Object>();
			try {
				nmsEnt = entInsent.cast(obcEnt.getClass().getMethod("getHandle").invoke(obcEnt));
				if (mark == 0) {
					float radius = (avoidRadius == null ? 6.0F : avoidRadius.getSingle(e).floatValue());
					double spd = (avoidSpeed == null ? 1.0D : avoidSpeed.getSingle(e).doubleValue());
					double nearSpd = (avoidSpeedNear == null ? 1.2D : avoidSpeedNear.getSingle(e).doubleValue());
					EntityData<?>[] types = typeToAvoid.getAll(e);
					if (ent instanceof Rabbit) {
						Class<?> goalRabbitAvoid = ReflectionUtils.getNMSClass("EntityRabbit$PathfinderGoalRabbitAvoidTarget");
						for (EntityData<?> entData : types) {
							if (!LivingEntity.class.isAssignableFrom(entData.getType()))
								continue;
							String className = entData.getType().getSimpleName();
							if (className.equals("HumanEntity")) {
								className = "Human";
							} else if (className.equals("EntityLiving")) {
								className = "Living";
							}
							Class<?> nmsClass = ReflectionUtils.getNMSClass("Entity" + className);
							newGoals.add(goalRabbitAvoid.getDeclaredConstructor(nmsEnt.getClass(), Class.class, float.class, double.class, double.class).newInstance(nmsEnt, nmsClass, radius, spd, nearSpd));
						}
					} else {
						Class<?> goalAvoid = ReflectionUtils.getNMSClass("PathfinderGoalAvoidTarget");
						for (EntityData<?> entData : types) {
							if (!LivingEntity.class.isAssignableFrom(entData.getType()))
								continue;
							String className = entData.getType().getSimpleName();
							if (className.equals("HumanEntity")) {
								className = "Human";
							} else if (className.equals("EntityLiving")) {
								className = "Living";
							}
							Class<?> nmsClass = ReflectionUtils.getNMSClass("Entity" + className);
							newGoals.add(goalAvoid.getConstructor(entCreature, Class.class, float.class, double.class, double.class).newInstance(nmsEnt, nmsClass, radius, spd, nearSpd));
						}
					}
				} else if (mark == 1) {
					Class<?> goalBreakDoor = ReflectionUtils.getNMSClass("PathfinderGoalBreakDoor");
					newGoals.add(goalBreakDoor.getConstructor(entInsent).newInstance(nmsEnt));
				} else if (mark == 2) {
					if (!(ent instanceof Animals))
						return;
					double spd = (breedSpeed == null ? 1.0D : breedSpeed.getSingle(e).doubleValue());
					Class<?> goalBreed = ReflectionUtils.getNMSClass("PathfinderGoalBreed");
					newGoals.add(goalBreed.getConstructor(entAnimal, double.class).newInstance(nmsEnt, spd));
				} else if (mark == 3) {
					Class<?> goalEatGrass = ReflectionUtils.getNMSClass("PathfinderGoalEatTile");
					newGoals.add(goalEatGrass.getConstructor(entInsent).newInstance(nmsEnt));
				} else if (mark == 4) {
					double spd = (fleeSunSpeed == null ? 1.0D : fleeSunSpeed.getSingle(e).doubleValue());
					Class<?> goalFleeSun = ReflectionUtils.getNMSClass("PathfinderGoalFleeSun");
					newGoals.add(goalFleeSun.getConstructor(entCreature, double.class).newInstance(nmsEnt, spd));
				} else if (mark == 5) {
					Class<?> goalFloat = ReflectionUtils.getNMSClass("PathfinderGoalFloat");
					newGoals.add(goalFloat.getConstructor(entInsent).newInstance(nmsEnt));
				} else if (mark == 6) {
					if (!(ent instanceof Tameable))
						return;
					double spd = (followOwnerSpeed == null ? 1.0D : followOwnerSpeed.getSingle(e).doubleValue());
					float minDist = (followMinDist == null ? 3.0F : followMinDist.getSingle(e).floatValue());
					float maxDist = (followMaxDist == null ? 10.0F : followMaxDist.getSingle(e).floatValue());
					Class<?> goalFollowOwner = ReflectionUtils.getNMSClass("PathfinderGoalFollowOwner");
					newGoals.add(goalFollowOwner.getConstructor(entTameable, double.class, float.class, float.class).newInstance(nmsEnt, spd, maxDist, minDist));
				} else if (mark == 7) {
					if (!(ent instanceof Animals))
						return;
					double spd = (followAdultsSpeed == null ? 1.0D : followAdultsSpeed.getSingle(e).doubleValue());
					Class<?> goalFollowAdults = ReflectionUtils.getNMSClass("PathfinderGoalFollowParent");
					newGoals.add(goalFollowAdults.getConstructor(entAnimal, double.class).newInstance(nmsEnt, spd));
				} else if (mark == 8) {
					target = true;
					boolean callHelp = (callForHelp == null ? false : callForHelp.getSingle(e));
					EntityData<?>[] types = typesToFightBack.getAll(e);
					List<Class<?>> typesClasses = new ArrayList<Class<?>>();
					for (EntityData<?> entData : types) {
						if (!LivingEntity.class.isAssignableFrom(entData.getType()))
							continue;
						String className = entData.getType().getSimpleName();
						if (className.equals("HumanEntity")) {
							className = "Human";
						} else if (className.equals("EntityLiving")) {
							className = "Living";
						}
						Class<?> nmsClass = ReflectionUtils.getNMSClass("Entity" + className);
						typesClasses.add(nmsClass);
					}
					Class<?>[] finalTypes = Arrays.copyOf(typesClasses.toArray(), typesClasses.size(), Class[].class);
					Class<?> goalReactAttack = ReflectionUtils.getNMSClass("PathfinderGoalHurtByTarget");
					newGoals.add(goalReactAttack.getConstructor(entCreature, boolean.class, Class[].class).newInstance(nmsEnt, callHelp, finalTypes));
				} else if (mark == 9) {
					if (!(ent instanceof Ocelot))
						return;
					double spd = (jumpOnBlockSpeed == null ? 1.0D : jumpOnBlockSpeed.getSingle(e).doubleValue());
					Class<?> goalJumpOnBlock = ReflectionUtils.getNMSClass("PathfinderGoalJumpOnBlock");
					newGoals.add(goalJumpOnBlock.getConstructor(nmsEnt.getClass(), double.class).newInstance(nmsEnt, spd));
				} else if (mark == 10) {
					float height = (leapHeight == null ? 0.4F : leapHeight.getSingle(e).floatValue());
					Class<?> goalLeapTarget = ReflectionUtils.getNMSClass("PathfinderGoalLeapAtTarget");
					newGoals.add(goalLeapTarget.getConstructor(entInsent, float.class).newInstance(nmsEnt, height));
				} else if (mark == 11) {
					float radius = (lookRadius == null ? 1.0F : lookRadius.getSingle(e).floatValue());
					EntityData<?>[] types = lookType.getAll(e);
					Class<?> goalLookEntities = ReflectionUtils.getNMSClass("PathfinderGoalLookAtPlayer");
					for (EntityData<?> entData : types) {
						if (!LivingEntity.class.isAssignableFrom(entData.getType()))
							continue;
						String className = entData.getType().getSimpleName();
						if (className.equals("HumanEntity")) {
							className = "Human";
						} else if (className.equals("EntityLiving")) {
							className = "Living";
						}
						Class<?> nmsClass = ReflectionUtils.getNMSClass("Entity" + className);
						newGoals.add(goalLookEntities.getConstructor(entInsent, Class.class, float.class).newInstance(nmsEnt, nmsClass, radius));
					}
				} else if (mark == 12) {
					EntityData<?>[] types = meleeTarget.getAll(e);
					if (ent instanceof Spider) {
						Class<?> goalSpiderMelee = ReflectionUtils.getNMSClass("EntitySpider$PathfinderGoalSpiderMeleeAttack");
						for (EntityData<?> entData : types) {
							if (!LivingEntity.class.isAssignableFrom(entData.getType()))
								continue;
							String className = entData.getType().getSimpleName();
							if (className.equals("HumanEntity")) {
								className = "Human";
							} else if (className.equals("EntityLiving")) {
								className = "Living";
							}
							Class<?> nmsClass = ReflectionUtils.getNMSClass("Entity" + className);
							newGoals.add(ReflectionUtils.getConstructor(goalSpiderMelee, nmsEnt.getClass(), Class.class).newInstance(nmsEnt, nmsClass));
						}
					} else {
						double spd = (meleeSpeed == null ? 1.0D : meleeSpeed.getSingle(e).doubleValue());
						boolean memorize = (meleeMemorize == null ? false : meleeMemorize.getSingle(e));
						Class<?> goalMeleeAttack = ReflectionUtils.getNMSClass("PathfinderGoalMeleeAttack");
						newGoals.add(goalMeleeAttack.getConstructor(entCreature, double.class, boolean.class).newInstance(nmsEnt, spd, memorize));
					}
				} else if (mark == 13) {
					double spd = (moveTargetSpeed == null ? 1.0D : moveTargetSpeed.getSingle(e).doubleValue());
					float radius = (moveTargetRadius == null ? 32.0F : moveTargetRadius.getSingle(e).floatValue());
					Class<?> goalGotoTarget = ReflectionUtils.getNMSClass("PathfinderGoalMoveTowardsTarget");
					newGoals.add(goalGotoTarget.getConstructor(entCreature, double.class, float.class).newInstance(nmsEnt, spd, radius));
				} else if (mark == 14) {
					target = true;
					EntityData<?>[] types = nearTarget.getAll(e);
					if (ent instanceof Spider) {
						Class<?> goalSpiderNearTarget = ReflectionUtils.getNMSClass("EntitySpider$PathfinderGoalSpiderNearestAttackableTarget");
						for (EntityData<?> entData : types) {
							if (!LivingEntity.class.isAssignableFrom(entData.getType()))
								continue;
							String className = entData.getType().getSimpleName();
							if (className.equals("HumanEntity")) {
								className = "Human";
							} else if (className.equals("EntityLiving")) {
								className = "Living";
							}
							Class<?> nmsClass = ReflectionUtils.getNMSClass("Entity" + className);
							newGoals.add(ReflectionUtils.getConstructor(goalSpiderNearTarget, nmsEnt.getClass(), Class.class).newInstance(nmsEnt, nmsClass));
						}
					} else {
						boolean checkView = (checkSight == null ? true : checkSight.getSingle(e));
						Class<?> goalNearTarget = ReflectionUtils.getNMSClass("PathfinderGoalNearestAttackableTarget");
						for (EntityData<?> entData : types) {
							if (!LivingEntity.class.isAssignableFrom(entData.getType()))
								continue;
							String className = entData.getType().getSimpleName();
							if (className.equals("HumanEntity")) {
								className = "Human";
							} else if (className.equals("EntityLiving")) {
								className = "Living";
							}
							Class<?> nmsClass = ReflectionUtils.getNMSClass("Entity" + className);
							newGoals.add(goalNearTarget.getConstructor(entCreature, Class.class, boolean.class).newInstance(nmsEnt, nmsClass, checkView));
						}
					}
				} else if (mark == 15) {
					Class<?> goalOcelotAttack = ReflectionUtils.getNMSClass("PathfinderGoalOcelotAttack");
					newGoals.add(goalOcelotAttack.getConstructor(entInsent).newInstance(nmsEnt));
				} else if (mark == 16) {
					Class<?> goalOpenDoors = ReflectionUtils.getNMSClass("PathfinderGoalOpenDoor");
					newGoals.add(goalOpenDoors.getConstructor(entInsent, boolean.class).newInstance(nmsEnt, false));
				} else if (mark == 17) {
					double spd = (panicSpeed == null ? 1.25D : panicSpeed.getSingle(e).doubleValue());
					if (ent instanceof Rabbit) {
						Class<?> goalRabbitPanic = ReflectionUtils.getNMSClass("EntityRabbit$PathfinderGoalRabbitPanic");
						newGoals.add(goalRabbitPanic.getDeclaredConstructor(nmsEnt.getClass(), double.class).newInstance(nmsEnt, spd));
					} else {
						Class<?> goalPanic = ReflectionUtils.getNMSClass("PathfinderGoalPanic");
						newGoals.add(goalPanic.getConstructor(entCreature, double.class).newInstance(nmsEnt, spd));
					}
				} else if (mark == 18) {
					Class<?> goalRandomLook = ReflectionUtils.getNMSClass("PathfinderGoalRandomLookaround");
					newGoals.add(goalRandomLook.getConstructor(entInsent).newInstance(nmsEnt));
				} else if (mark == 19) {
					double spd = (randomWalkSpeed == null ? 1.0D : randomWalkSpeed.getSingle(e).doubleValue());
					int interval = (randomWalkInterval == null ? 120 : randomWalkInterval.getSingle(e).getTicks());
					Class<?> goalWander = ReflectionUtils.getNMSClass("PathfinderGoalRandomStroll");
					newGoals.add(goalWander.getConstructor(entCreature, double.class, int.class).newInstance(nmsEnt, spd, interval));
				} else if (mark == 20) {
					if (!(ent instanceof Tameable))
						return;
					Class<?> goalSit = ReflectionUtils.getNMSClass("PathfinderGoalSit");
					newGoals.add(goalSit.getConstructor(entTameable).newInstance(nmsEnt));
				} else if (mark == 21) {
					if (!(ent instanceof Creeper))
						return;
					Class<?> goalSwell = ReflectionUtils.getNMSClass("PathfinderGoalSwell");
					newGoals.add(goalSwell.getConstructor(nmsEnt.getClass()).newInstance(nmsEnt));
				} else if (mark == 22) {
					if (!(ent instanceof Squid))
						return;
					Class<?> goalSquid = ReflectionUtils.getNMSClass("EntitySquid$PathfinderGoalSquid");
					newGoals.add(ReflectionUtils.getConstructor(goalSquid, nmsEnt.getClass()).newInstance(nmsEnt));
				} else if (mark == 23) {
					if (ent instanceof Blaze) {
						Class<?> goalBlazeFireball = ReflectionUtils.getNMSClass("EntityBlaze$PathfinderGoalBlazeFireball");
						newGoals.add(ReflectionUtils.getConstructor(goalBlazeFireball, nmsEnt.getClass()).newInstance(nmsEnt));
					} else if (ent instanceof Ghast) {
						Class<?> goalGhastFireball = ReflectionUtils.getNMSClass("EntityGhast$PathfinderGoalGhastAttackTarget");
						newGoals.add(ReflectionUtils.getConstructor(goalGhastFireball, nmsEnt.getClass()).newInstance(nmsEnt));
					}
				} else if (mark == 24) {
					if (!(ent instanceof Silverfish))
						return;
					Class<?> goalHideInBlock = ReflectionUtils.getNMSClass("EntitySilverfish$PathfinderGoalSilverfishHideInBlock");
					newGoals.add(ReflectionUtils.getConstructor(goalHideInBlock, nmsEnt.getClass()).newInstance(nmsEnt));
				} else if (mark == 25) {
					if (!(ent instanceof Silverfish))
						return;
					Class<?> goalWakeSilverfish = ReflectionUtils.getNMSClass("EntitySilverfish$PathfinderGoalSilverfishWakeOthers");
					newGoals.add(ReflectionUtils.getConstructor(goalWakeSilverfish, nmsEnt.getClass()).newInstance(nmsEnt));
				} else if (mark == 26) {
					if (!(ent instanceof Enderman))
						return;
					Class<?> goalPickBlocks = ReflectionUtils.getNMSClass("EntityEnderman$PathfinderGoalEndermanPickupBlock");
					newGoals.add(ReflectionUtils.getConstructor(goalPickBlocks, nmsEnt.getClass()).newInstance(nmsEnt));
				} else if (mark == 27) {
					if (!(ent instanceof Enderman))
						return;
					Class<?> goalPlaceBlocks = ReflectionUtils.getNMSClass("EntityEnderman$PathfinderGoalEndermanPlaceBlock");
					newGoals.add(ReflectionUtils.getConstructor(goalPlaceBlocks, nmsEnt.getClass()).newInstance(nmsEnt));
				} else if (mark == 28) {
					if (!(ent instanceof Enderman))
						return;
					target = true;
					Class<?> goalAttackLooker = ReflectionUtils.getNMSClass("EntityEnderman$PathfinderGoalPlayerWhoLookedAtTarget");
					newGoals.add(ReflectionUtils.getConstructor(goalAttackLooker, nmsEnt.getClass()).newInstance(nmsEnt));
				} else if (mark == 29) {
					if (!(ent instanceof Ghast))
						return;
					Class<?> goalGhastMoveTarget = ReflectionUtils.getNMSClass("EntityGhast$PathfinderGoalGhastMoveTowardsTarget");
					newGoals.add(ReflectionUtils.getConstructor(goalGhastMoveTarget, nmsEnt.getClass()).newInstance(nmsEnt));
				} else if (mark == 30) {
					if (!(ent instanceof Ghast))
						return;
					Class<?> goalGhastIdleMove = ReflectionUtils.getNMSClass("EntityGhast$PathfinderGoalGhastIdleMove");
					newGoals.add(ReflectionUtils.getConstructor(goalGhastIdleMove, nmsEnt.getClass()).newInstance(nmsEnt));
				} else if (mark == 31) {
					ItemStack itemStack = temptItem.getSingle(e);
					if (itemStack.getType() == Material.AIR || itemStack == null)
						return;
					Object nmsItemStack = craftItemClass.getMethod("asNMSCopy", ItemStack.class).invoke(itemStack, itemStack);
					Object nmsItem = nmsItemStack.getClass().getMethod("getItem").invoke(nmsItemStack);
					double spd = (temptSpeed == null ? 1.0D : temptSpeed.getSingle(e).doubleValue());
					boolean scared = (temptScared == null ? false : temptScared.getSingle(e));
					Class<?> goalTempt = ReflectionUtils.getNMSClass("PathfinderGoalTempt");
					newGoals.add(goalTempt.getConstructor(entCreature, double.class, nmsItemClass, boolean.class).newInstance(nmsEnt, spd, nmsItem, scared));
				} else if (mark == 32) {
					if (!(ent instanceof Tameable))
						return;
					target = true;
					EntityData<?>[] types = nonTamedTarget.getAll(e);
					Class<?> goalTargetNonTamed = ReflectionUtils.getNMSClass("PathfinderGoalRandomTargetNonTamed");
					for (EntityData<?> entData : types) {
						if (!LivingEntity.class.isAssignableFrom(entData.getType()))
							continue;
						String className = entData.getType().getSimpleName();
						if (className.equals("HumanEntity")) {
							className = "Human";
						} else if (className.equals("EntityLiving")) {
							className = "Living";
						}
						Class<?> nmsClass = ReflectionUtils.getNMSClass("Entity" + className);
						newGoals.add(goalTargetNonTamed.getConstructor(entTameable, Class.class, boolean.class, Predicate.class).newInstance(nmsEnt, nmsClass, false, Predicates.alwaysTrue()));
					}
				} else if (mark == 33) {
					if (!(ent instanceof Guardian))
						return;
					Class<?> goalGuardianAttack = ReflectionUtils.getNMSClass("EntityGuardian$PathfinderGoalGuardianAttack");
					newGoals.add(ReflectionUtils.getConstructor(goalGuardianAttack, nmsEnt.getClass()).newInstance(nmsEnt));
				} else if (mark == 34) {
					if (!(ent instanceof PigZombie))
						return;
					target = true;
					Class<?> goalAnger = ReflectionUtils.getNMSClass("EntityPigZombie$PathfinderGoalAnger");
					newGoals.add(ReflectionUtils.getConstructor(goalAnger, nmsEnt.getClass()).newInstance(nmsEnt));
				} else if (mark == 35) {
					if (!(ent instanceof PigZombie))
						return;
					target = true;
					Class<?> goalAngerOther = ReflectionUtils.getNMSClass("EntityPigZombie$PathfinderGoalAngerOther");
					newGoals.add(ReflectionUtils.getConstructor(goalAngerOther, nmsEnt.getClass()).newInstance(nmsEnt));
				} else if (mark == 36) {
					if (!(ent instanceof Rabbit))
						return;
					Class<?> goalEatCarrots = ReflectionUtils.getNMSClass("EntityRabbit$PathfinderGoalEatCarrots");
					newGoals.add(ReflectionUtils.getConstructor(goalEatCarrots, nmsEnt.getClass()).newInstance(nmsEnt));
				} else if (mark == 37) {
					if (!(ent instanceof Rabbit))
						return;
					Class<?> goalRabbitAttack = ReflectionUtils.getNMSClass("EntityRabbit$PathfinderGoalKillerRabbitMeleeAttack");
					newGoals.add(ReflectionUtils.getConstructor(goalRabbitAttack, nmsEnt.getClass()).newInstance(nmsEnt));
				} else if (mark == 38) {
					if (!(ent instanceof Slime))
						return;
					Class<?> goalJump = ReflectionUtils.getNMSClass("EntitySlime$PathfinderGoalSlimeRandomJump");
					newGoals.add(ReflectionUtils.getConstructor(goalJump, nmsEnt.getClass()).newInstance(nmsEnt));
				} else if (mark == 39) {
					if (!(ent instanceof Slime))
						return;
					Class<?> goalRandomDir = ReflectionUtils.getNMSClass("EntitySlime$PathfinderGoalSlimeRandomDirection");
					newGoals.add(ReflectionUtils.getConstructor(goalRandomDir, nmsEnt.getClass()).newInstance(nmsEnt));
				} else if (mark == 40) {
					if (!(ent instanceof Slime))
						return;
					Class<?> goalSlimeWander = ReflectionUtils.getNMSClass("EntitySlime$PathfinderGoalSlimeIdle");
					newGoals.add(ReflectionUtils.getConstructor(goalSlimeWander, nmsEnt.getClass()).newInstance(nmsEnt));
				} else if (mark == 41) {
					String version = ReflectionUtils.getVersion();
					EntityData<?>[] types = followedTypes.getAll(e);
					float radius = (followedRadius == null ? 32 : followedRadius.getSingle(e).floatValue());
					double spd = (followedSpeed == null ? 1 : followedSpeed.getSingle(e).doubleValue());
					boolean usesName = followedName != null;
					String customName = (usesName ? followedName.getSingle(e) : null);
					for (EntityData<?> entData : types) {
						if (!LivingEntity.class.isAssignableFrom(entData.getType()))
							continue;
						String className = entData.getType().getSimpleName();
						if (className.equals("HumanEntity")) {
							className = "Human";
						} else if (className.equals("EntityLiving")) {
							className = "Living";
						}
						Class<?> nmsClass = ReflectionUtils.getNMSClass("Entity" + className);
						if (version.equals("v1_13_R2.")) {
							newGoals.add(new PathfinderGoalFollow_v1_13_R2((net.minecraft.server.v1_13_R2.EntityCreature) nmsEnt, nmsClass, radius, spd, usesName, customName));
						} else if (version.equals("v1_14_R1.")) {
							newGoals.add(new PathfinderGoalFollow_v1_14_R1((net.minecraft.server.v1_14_R1.EntityCreature) nmsEnt, nmsClass, radius, spd, usesName, customName));
						}
					}
				} else if (mark == 42) {
					if (!(ent instanceof Skeleton)) {
						Skript.warning("The pathfinder goal \"Bow Shoot\" can only be applied to skeletons!");
						return;
					}
					double moveSpd = (bowShootMoveSpeed == null ? 1.0D : avoidSpeed.getSingle(e).doubleValue());
					double unkParam = (bowShootUnkParam == null ? 20 : avoidSpeedNear.getSingle(e).intValue());
					double followRange = (bowShootFollowRange == null ? 15.0F : avoidSpeedNear.getSingle(e).floatValue());
					Class<?> goalBowShoot = ReflectionUtils.getNMSClass("PathfinderGoalBowShoot");
					newGoals.add(ReflectionUtils.getConstructor(goalBowShoot, nmsEnt.getClass(), double.class, int.class, float.class).newInstance(nmsEnt, moveSpd, unkParam, followRange));
				}

				if (newGoals.size() == 0)
					return;
				for (Object goal : newGoals) {
					SkStuff.getNMSMethods().addPathfinderGoal(nmsEnt, priority, goal, target);
				}
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
				ex.printStackTrace();
			}
		}
	}
}