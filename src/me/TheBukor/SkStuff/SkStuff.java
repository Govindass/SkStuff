package me.TheBukor.SkStuff;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import me.TheBukor.SkStuff.bstats.Metrics;
import me.TheBukor.SkStuff.effects.*;
import me.TheBukor.SkStuff.expressions.*;
import me.TheBukor.SkStuff.util.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.IOException;

public class SkStuff extends JavaPlugin {

	private static NMSInterface nmsMethods;

	@SuppressWarnings("rawtypes")
	public void onEnable() {
		if (Bukkit.getPluginManager().getPlugin("Skript") != null && Skript.isAcceptRegistrations()) {
			Skript.registerAddon(this);
			getLogger().info("SkStuff " + this.getDescription().getVersion() + " has been successfully enabled!");
			getLogger().info("Registering general non version specific stuff...");
			Skript.registerEffect(EffShowEntityEffect.class, "(display|play|show) entity effect (0¦firework[s] explo(de|sion)|1¦hurt|2¦[[iron] golem] (give|offer) (rose|poppy)|3¦[sheep] eat grass|4¦wolf shake|5¦squid rotate|6¦totem resurrect|7¦rabbit jump) (at|on) %entity%");
			if (setupNMSVersion()) {
				getLogger().info("Trying to register version specific stuff...");
				Skript.registerExpression(ExprStepLength.class, Number.class, ExpressionType.PROPERTY, "[the] step length of %entity%", "%entity%'s step length");
				Skript.registerEffect(EffClearPathGoals.class, "(clear|delete) [all] pathfind[er] goals (of|from) %livingentities%");
				Skript.registerEffect(EffRemovePathGoal.class, "remove pathfind[er] goal (0¦(avoid|run away from) entit(y|ies)|1¦break door[s]|2¦breed|3¦eat grass|4¦(flee from the sun|seek shad(e|ow))|5¦float (in[side]|on) water|6¦follow (owner|tamer)|7¦follow (adult|parent)[s]|8¦(fight back|react to|target) (damager|attacker)|9¦o(c|z)elot jump on blocks|10¦leap at target|11¦look at entit(y|ies)|12¦melee attack entit(y|ies)|13¦move to[wards] target|14¦target nearest entity|15¦o(c|z)elot attack [chicken[s]]|16¦open door[s]|17¦(panic|flee)|18¦look around randomly|19¦(walk around randomly|wander)|20¦sit|21¦[creeper] (explode|inflate|swell)|22¦squid (swim|wander)|23¦shoot fireball[s]|24¦[silverfish] hide (in[side]|on) block[s]|25¦(wake other silverfish[es]|[silverfish] call (help|reinforcement|other [hidden] silverfish[es]))|26¦[enderm(a|e)n] pick[[ ]up] block[s]|27¦[enderm(a|e)n] place block[s]|28¦[enderman] attack player (staring|looking) [at eye[s]]|29¦ghast move to[wards] target|30¦ghast (idle move[ment]|wander|random fl(ight|y[ing]))|31¦(tempt to|follow players (holding|with)) [a[n]] item|32¦target [random] entity (if|when) (not tamed|untamed)|33¦guardian attack [entity]|34¦[z[ombie[ ]]pig[man]] attack [player[s]] (if|when) angry|35¦[z[ombie[ ]]pig[man]] (react to|fight back|target) (attacker|damager) (if|when) angry|36¦[rabbit] eat carrot crops|37¦[killer] rabbit [melee] attack|38¦slime [random] jump|39¦slime change (direction|facing) randomly|40¦slime (idle move[ment]|wander)|41¦follow [entity]|42¦bow shoot) from %livingentities%");
				// Note to self: whenever adding a new pathfinder goal, increase the expression index for 'entities' in EffSetPathGoal 
				Skript.registerEffect(EffSetPathGoal.class, "add pathfind[er] goal [[with] priority %-integer%] (0¦(avoid|run away from) %*entitydatas%[, radius %-number%[, speed %-number%[, speed (if|when) (close|near) %-number%]]]|1¦break door[s]|2¦breed[,[move[ment]] speed %-number%]|3¦eat grass|4¦(flee from the sun|seek shad(e|ow))[, [move[ment]] speed %-number%]|5¦(float (in[side]|on) water|swim)|6¦follow (owner|tamer)[, speed %-number%[, min[imum] distance %-number%[, max[imum] distance %-number%]]]|7¦follow (adult|parent)[s][, [move[ment]] speed %-number%]|8¦(fight back|react to|target) (damager|attacker) [[of] type] %*entitydatas%[, call ([for] help|reinforcement) %-boolean%]|9¦o(c|z)elot jump on blocks[, [move[ment]] speed %-number%]|10¦leap at target[, [leap] height %-number%]|11¦look at %*entitydatas%[, (radius|max[imum] distance) %-number%]|12¦melee attack %*entitydatas%[, [move[ment]] speed %-number%[, (memorize|do('nt| not) forget) target [for [a] long[er] time] %-boolean%]]|13¦move to[wards] target[, [move[ment]] speed %-number%[, (radius|max[imum] distance) %-number%]]|14¦target nearest [entity [of] type] %*entitydatas%[, check sight %-boolean%]|15¦o(c|z)elot attack|16¦open door[s]|17¦(panic|flee)[, [move[ment]] speed %-number%]|18¦look around randomly|19¦(walk around randomly|wander)[, [move[ment]] speed %-number%[, min[imum] [of] %-timespan% between mov(e[ment][s]|ing)]]|20¦sit|21¦[creeper] (explode|inflate|swell)|22¦squid (swim around|wander)|23¦shoot fireball[s]|24¦[silverfish] hide (in[side]|on) block[s]|25¦((call|summon|wake) [other] [hidden] silverfish[es])|26¦[enderman] pick[[ ]up] block[s]|27¦[enderman] place block[s]|28¦[enderman] attack player (staring|looking) at [their] eye[s]]|29¦ghast move to[wards] target|30¦ghast (idle move[ment]|wander|random fl(ight|y[ing]))|31¦(tempt to|follow players (holding|with)) %-itemstack%[, [move[ment]] speed %number%[, scared of player movement %-boolean%]]|32¦target [random] %*entitydatas% (if|when) (not |un)tamed|33¦guardian attack [entities]|34¦[z[ombie[ ]]pig[man]] attack [player[s]] (if|when) angry|35¦[z[ombie[ ]]pig[man]] (react to|fight back|target) (attacker|damager) (if|when) angry|36¦[rabbit] eat carrot crops|37¦[killer] rabbit [melee] attack|38¦slime [random] jump|39¦slime change (direction|facing) randomly|40¦slime (idle move[ment]|wander)|41¦follow %*entitydatas%[, radius %-number%[, speed %-number%[, [custom[ ]]name[d] %-string%]]]|42¦bow shoot[, [move[ment]] speed %-number%[, unk[nown] param[eter] %-number%[, follow range %-number%]]])) to %livingentities%");
				Skript.registerEffect(EffMakeJump.class, "make %livingentities% jump", "force %livingentities% to jump");
				Skript.registerExpression(ExprNoClip.class, Boolean.class, ExpressionType.PROPERTY, "no[( |-)]clip (state|mode) of %entities%", "%entities%'s no[( |-)]clip (state|mode)");
				Skript.registerExpression(ExprFireProof.class, Boolean.class, ExpressionType.PROPERTY, "fire[ ]proof (state|mode) of %entities%", "%entities%'s fire[ ]proof (state|mode)");
			}
			Metrics metrics = new Metrics(this, 1);
			metrics.startSubmitting();
			getLogger().info("Hooked into Metrics! Woohoo!!");
			getLogger().info("Everything's ready!");
		} else {
			getLogger().info("Unable to find Skript or Skript isn't accepting registrations, disabling SkStuff...");
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}

	private boolean setupNMSVersion() {
		String version = ReflectionUtils.getVersion();

		switch (version) {
			case "v1_8_R3.":
				nmsMethods = new NMS_v1_8_R3();
				getLogger().info("It looks like you're running 1.8.8!");
				break;
			case "v1_9_R2.":
				nmsMethods = new NMS_v1_9_R2();
				getLogger().info("It looks like you're running 1.9.4!");
				break;
			case "v1_10_R1.":
				nmsMethods = new NMS_v1_10_R1();
				getLogger().info("It looks like you're running 1.10.2!");
				break;
			case "v1_11_R1.":
				nmsMethods = new NMS_v1_11_R1();
				getLogger().info("It looks like you're running 1.11.2!");
				break;
			case "v1_12_R1.":
				nmsMethods = new NMS_v1_12_R1();
				getLogger().info("It looks like you're running 1.12.2!");
				break;
			case "v1_13_R2.":
				nmsMethods = new NMS_v1_13_R2();
				getLogger().info("It looks like you're running 1.13.2!");
				break;
			case "v1_14_R1.":
				nmsMethods = new NMS_v1_14_R1();
				getLogger().info("It looks like you're running 1.14.4!");
				break;
			case "v1_15_R1.":
				nmsMethods = new NMS_v1_15_R1();
				getLogger().info("It looks like you're running 1.15.2!");
				break;
			case "v1_16_R2.":
				nmsMethods = new NMS_v1_16_R2();
				getLogger().info("It looks like you're running 1.16.3!");
				break;
			default:
				getLogger().warning("It looks like you're running an unsupported server version, some features will not be available :(");
				break;
		}
		return nmsMethods != null;
	}

	public static NMSInterface getNMSMethods() {
		return nmsMethods;
	}

	public void onDisable() {
		getLogger().info("SkStuff " + this.getDescription().getVersion() + " has been successfully disabled.");
	}
}