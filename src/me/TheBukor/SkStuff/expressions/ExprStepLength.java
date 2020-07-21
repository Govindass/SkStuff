package me.TheBukor.SkStuff.expressions;

import javax.annotation.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.TheBukor.SkStuff.SkStuff;

public class ExprStepLength extends SimpleExpression<Number> {
	private Expression<Entity> entity;

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		entity = (Expression<Entity>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "step length of " + entity.toString(e, debug);
	}

	@Override
	@Nullable
	protected Number[] get(Event e) {
		Entity ent = entity.getSingle(e);
		if (ent == null) {
			return null;
		}
		return new Number[] { SkStuff.getNMSMethods().getEntityStepLength(ent) };
	}

	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		Entity ent = entity.getSingle(e);
		if (ent == null) {
			return;
		}
		if (mode == ChangeMode.ADD) {
			float toAdd = ((Number) delta[0]).floatValue();
			float currentLength = SkStuff.getNMSMethods().getEntityStepLength(ent);
			SkStuff.getNMSMethods().setEntityStepLength(ent, (currentLength + toAdd));
		} else if (mode == ChangeMode.REMOVE) {
			float toRemove = ((Number) delta[0]).floatValue();
			float currentLength = SkStuff.getNMSMethods().getEntityStepLength(ent);
			SkStuff.getNMSMethods().setEntityStepLength(ent, (currentLength - toRemove));
		} else if (mode == ChangeMode.SET) {
			float toSet = ((Number) delta[0]).floatValue();
			SkStuff.getNMSMethods().setEntityStepLength(ent, toSet);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.SET) {
			return CollectionUtils.array(Number.class);
		}
		return null;
	}
}
