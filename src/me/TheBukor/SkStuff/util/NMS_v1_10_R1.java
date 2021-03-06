package me.TheBukor.SkStuff.util;

import net.minecraft.server.v1_10_R1.EntityInsentient;
import net.minecraft.server.v1_10_R1.PathfinderGoal;
import net.minecraft.server.v1_10_R1.PathfinderGoalSelector;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

import java.util.Iterator;
import java.util.LinkedHashSet;

public class NMS_v1_10_R1 implements NMSInterface {
	@Override
	public void clearPathfinderGoals(Entity entity) {
		EntityInsentient nmsEnt = (EntityInsentient) ((CraftEntity) entity).getHandle();
		((LinkedHashSet<?>) ReflectionUtils.getField("b", PathfinderGoalSelector.class, nmsEnt.goalSelector)).clear();
		((LinkedHashSet<?>) ReflectionUtils.getField("c", PathfinderGoalSelector.class, nmsEnt.goalSelector)).clear();
		((LinkedHashSet<?>) ReflectionUtils.getField("b", PathfinderGoalSelector.class, nmsEnt.targetSelector)).clear();
		((LinkedHashSet<?>) ReflectionUtils.getField("c", PathfinderGoalSelector.class, nmsEnt.targetSelector)).clear();
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
		net.minecraft.server.v1_10_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		return nmsEntity.noclip;
	}

	@Override
	public void setNoClip(Entity entity, boolean noclip) {
		net.minecraft.server.v1_10_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		nmsEntity.noclip = noclip;
	}

	@Override
	public boolean getFireProof(Entity entity) {
		net.minecraft.server.v1_10_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		return nmsEntity.isFireProof();
	}

	@Override
	public void setFireProof(Entity entity, boolean fireProof) {
		net.minecraft.server.v1_10_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		ReflectionUtils.setField("fireProof", nmsEntity.getClass(), nmsEntity, fireProof);
	}
	@Override
	public float getEntityStepLength(Entity entity) {
		net.minecraft.server.v1_10_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		return nmsEntity.P;
	}

	@Override
	public void setEntityStepLength(Entity entity, float length) {
		net.minecraft.server.v1_10_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		nmsEntity.P = length;
	}
}