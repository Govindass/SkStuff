package me.TheBukor.SkStuff.util;


import net.minecraft.server.v1_15_R1.BehaviorController;
import net.minecraft.server.v1_15_R1.EntityInsentient;
import net.minecraft.server.v1_15_R1.PathfinderGoal;
import net.minecraft.server.v1_15_R1.PathfinderGoalSelector;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;
import java.util.*;

public class NMS_v1_15_R1 implements NMSInterface {



	@Override
	public void clearPathfinderGoals(Entity entity) {
		EntityInsentient nmsEntity = (EntityInsentient) ((CraftEntity) entity).getHandle();
		PathfinderGoalSelector goalSelector = nmsEntity.goalSelector;
		PathfinderGoalSelector targetSelector = nmsEntity.targetSelector;
		try {
			BehaviorController<?> controller = nmsEntity.getBehaviorController();

			Field memoriesField = BehaviorController.class.getDeclaredField("memories");
			memoriesField.setAccessible(true);
			memoriesField.set(controller, new HashMap<>());

			Field sensorsField = BehaviorController.class.getDeclaredField("sensors");
			sensorsField.setAccessible(true);
			sensorsField.set(controller, new LinkedHashMap<>());

		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}


		try {
			Field dField;
			dField = PathfinderGoalSelector.class.getDeclaredField("d");
			dField.setAccessible(true);
			dField.set(goalSelector, new LinkedHashSet<>());
			dField.set(targetSelector, new LinkedHashSet<>());

			Field cField;
			cField = PathfinderGoalSelector.class.getDeclaredField("c");
			cField.setAccessible(true);
			dField.set(goalSelector, new LinkedHashSet<>());
			cField.set(targetSelector, new EnumMap<>(PathfinderGoal.Type.class));

			Field fField;
			fField = PathfinderGoalSelector.class.getDeclaredField("f");
			fField.setAccessible(true);
			dField.set(goalSelector, new LinkedHashSet<>());
			fField.set(targetSelector, EnumSet.noneOf(PathfinderGoal.Type.class));
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removePathfinderGoal(Object entity, Class<?> goalClass, boolean isTargetSelector) {
		if (entity instanceof EntityInsentient) {
			((EntityInsentient) entity).setGoalTarget(null);
			if (isTargetSelector) {
				Iterator<?> goals = ((LinkedHashSet<?>) ReflectionUtils.getField("b", PathfinderGoalSelector.class, ((EntityInsentient) entity).targetSelector)).iterator();
				while (goals.hasNext()) {
					Object goal = goals.next();
					if (ReflectionUtils.getField("a", goal.getClass(), goal).getClass() == goalClass) {
						goals.remove();
					}
				}
			} else {
				Iterator<?> goals = ((LinkedHashSet<?>) ReflectionUtils.getField("b", PathfinderGoalSelector.class, ((EntityInsentient) entity).goalSelector)).iterator();
				while (goals.hasNext()) {
					Object goal = goals.next();
					if (ReflectionUtils.getField("a", goal.getClass(), goal).getClass() == goalClass) {
						goals.remove();
					}
				}
			}
		}
	}

	@Override
	public void addPathfinderGoal(Object entity, int priority, Object goal, boolean isTargetSelector) {
		if (entity instanceof EntityInsentient && goal instanceof PathfinderGoal) {
			if (isTargetSelector)
				((EntityInsentient) entity).targetSelector.a(priority, (PathfinderGoal) goal);
			else
				((EntityInsentient) entity).goalSelector.a(priority, (PathfinderGoal) goal);
		}
	}


	@Override
	public boolean getNoClip(Entity entity) {
		net.minecraft.server.v1_15_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		return nmsEntity.noclip;
	}

	@Override
	public void setNoClip(Entity entity, boolean noclip) {
		net.minecraft.server.v1_15_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		nmsEntity.noclip = noclip;
	}

	@Override
	public boolean getFireProof(Entity entity) {
		net.minecraft.server.v1_15_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		return nmsEntity.isFireProof();
	}

	@Override
	public void setFireProof(Entity entity, boolean fireProof) {
		net.minecraft.server.v1_15_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		ReflectionUtils.setField("fireProof", nmsEntity.getClass(), nmsEntity, fireProof);
	}
	public float getEntityStepLength(Entity entity) {
		net.minecraft.server.v1_15_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		return nmsEntity.H;
	}

	@Override
	public void setEntityStepLength(Entity entity, float length) {
		net.minecraft.server.v1_15_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		nmsEntity.H = length;
	}
}