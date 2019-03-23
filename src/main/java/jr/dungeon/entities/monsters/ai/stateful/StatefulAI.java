package jr.dungeon.entities.monsters.ai.stateful;

import com.google.gson.annotations.Expose;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.EntityReference;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.AI;
import jr.dungeon.entities.monsters.ai.stateful.generic.TraitBewareTarget;
import jr.dungeon.entities.monsters.ai.stateful.humanoid.TraitExtrinsicFear;
import jr.dungeon.entities.monsters.ai.stateful.humanoid.TraitIntrinsicFear;
import jr.dungeon.events.EventListener;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.TileType;
import jr.utils.Point;
import jr.utils.Utils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Registered(id="aiStateful")
public class StatefulAI extends AI {
	@Expose private AIState defaultState;
	@Expose private AIState currentState;
	
	@Expose @Setter(AccessLevel.NONE)
	private EntityReference<EntityLiving> currentTarget = new EntityReference<>();
	@Expose private Point targetLastPos;
	
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@Expose
	private boolean shouldTargetPlayer = true;
	@Expose private int visibilityRange = 15;
	
	@Expose private Map<Class<? extends AITrait>, AITrait> traits = new HashMap<>();
	
	@Expose private Set<Point> safePoints = new HashSet<>();
	
	public StatefulAI(Monster monster) {
		super(monster);
		
		addTrait(new TraitBewareTarget(this));
		addTrait(new TraitIntrinsicFear(this));
		addTrait(new TraitExtrinsicFear(this));
	}
	
	@Override
	public void update() {
		if (suppressTurns > 0 && suppressTurns-- > 0) return;
		if (getMonster() == null) return;
		
		if (shouldTargetPlayer && !currentTarget.isSet())
			currentTarget.set(getMonster().getDungeon().getPlayer());
		
		if (currentTarget.isSet() && !currentTarget.get(getLevel()).isAlive())
			currentTarget.unset();
		
		traits.values().stream()
			.sorted(Comparator.comparingInt(AITrait::getPriority))
			.forEach(AITrait::update);
		
		if (currentState == null) currentState = defaultState;
		if (currentState != null) currentState.update();
				
		if (
			currentState != null &&
			currentState.getDuration() > 0 &&
			currentState.getTurnsTaken() >= currentState.getDuration()
		) {
			currentState = null;
		}
		
		updateTargetVisibility();
	}
	
	public boolean shouldTargetPlayer() {
		return shouldTargetPlayer;
	}
	
	public void setShouldTargetPlayer(boolean shouldTargetPlayer) {
		this.shouldTargetPlayer = shouldTargetPlayer;
	}
	
	public boolean canSee(Entity e) {
		int startX = getMonster().getX();
		int startY = getMonster().getY();
		int endX = e.getX();
		int endY = e.getY();
		
		int preDistance = Utils.distance(startX, startY, endX, endY);
		
		if (preDistance > getVisibilityRange()) {
			return false;
		}
		
		float diffX = endX - startX;
		float diffY = endY - startY;
		
		float dist = Math.abs(diffX + diffY);
		
		float dx = diffX / dist;
		float dy = diffY / dist;
		
		for (int i = 0; i <= Math.ceil(dist); i++) {
			int x = Math.round(startX + dx * i);
			int y = Math.round(startY + dy * i);
			
			if (getMonster().getLevel().tileStore.getTileType(x, y).getSolidity() == TileType.Solidity.SOLID) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean canSeeTarget() {
		return currentTarget.isSet() && canSee(currentTarget.get(getLevel()));
	}
	
	public void updateTargetVisibility() {
		if (!currentTarget.isSet()) return;
		
		if (canSeeTarget()) {
			targetLastPos = currentTarget.get(getLevel()).getLastPosition();
		}
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		if (getMonster() == null) {
			return;
		}
		
		if (defaultState != null) {
			JSONObject serialisedDefaultState = new JSONObject();
			defaultState.serialise(serialisedDefaultState);
			obj.put("defaultState", serialisedDefaultState);
		}
		
		if (currentState != null) {
			JSONObject serialisedCurrentState = new JSONObject();
			currentState.serialise(serialisedCurrentState);
			obj.put("currentState", serialisedCurrentState);
		}
		
		if (currentTarget != null) {
			obj.put("currentTarget", currentTarget.getUUID().toString());
			obj.put("targetLastPos", targetLastPos);
		}
		
		obj.put("shouldTargetPlayer", shouldTargetPlayer);
		obj.put("visibilityRange", visibilityRange);
		
		JSONObject serialisedTraits = new JSONObject();
		
		traits.forEach((c, t) -> {
			JSONObject serialisedTrait = new JSONObject();
			t.serialise(serialisedTrait);
			serialisedTraits.put(c.getName(), serialisedTrait);
		});
		
		obj.put("traits", serialisedTraits);
		
		obj.put("safePoints", new JSONArray(safePoints));
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		if (getMonster() == null || getMonster().getLevel() == null) {
			return;
		}
		
		defaultState = AIState.createFromJSON(obj.getJSONObject("defaultState"), this);
		currentState = AIState.createFromJSON(obj.getJSONObject("currentState"), this);
		
		if (obj.has("currentTarget")) {
			currentTarget = (EntityLiving) getMonster().getLevel().entityStore.getEntityByUUID(obj.optString("currentTarget"));

			if (obj.has("targetLastPos")) {
				targetLastPos = Point.unserialise(obj.getString("targetLastPos"));
			}
		}
		
		shouldTargetPlayer = obj.optBoolean("shouldTargetPlayer", true);
		visibilityRange = obj.optInt("visibilityRange");
		
		if (obj.has("traits")) {
			JSONObject serialisedTraits = obj.getJSONObject("traits");
			
			serialisedTraits.keySet().forEach(traitClassName -> {
				JSONObject serialisedTrait = serialisedTraits.getJSONObject(traitClassName);
				AITrait unserialisedTrait = AITrait.createFromJSON(traitClassName, serialisedTrait, this);
				assert unserialisedTrait != null;
				traits.put(unserialisedTrait.getClass(), unserialisedTrait);
			});
		}
		
		if (obj.has("safePoints")) {
			obj.getJSONArray("safePoints").forEach(safePointObj -> {
				JSONObject serialisedSafePoint = (JSONObject) safePointObj;
				Point point = Point.getPoint(serialisedSafePoint.getInt("x"), serialisedSafePoint.getInt("y"));
				safePoints.add(point);
			});
		}
	}
	
	@Override
	public ToStringBuilder toStringBuilder() {
		ToStringBuilder tsb = super.toStringBuilder()
			.append("defaultState", defaultState == null ? "no state" : defaultState.toStringBuilder())
			.append("currentState", currentState == null ? "no state" : currentState.toStringBuilder())
			.append("suppressTurns", suppressTurns)
			.append("pos", getMonster().getPosition())
			.append("currentTarget", !currentTarget.isSet() ? "no target" : currentTarget.get(getLevel()).getClass().getSimpleName())
			.append("safePoints", safePoints.size());
		
		traits.values().forEach(t -> tsb.append(t.toStringBuilder()));
		
		return tsb;
	}
	
	@Override
	public List<EventListener> getSubListeners() {
		val subListeners = super.getSubListeners();
		
		subListeners.add(currentState);
		subListeners.add(defaultState);
		subListeners.addAll(traits.values());
		
		return subListeners;
	}
	
	public void addSafePoint(Point p) {
		safePoints.add(p);
	}
	
	public Optional<Point> getSafePoint() {
		if (!currentTarget.isSet()) return Optional.empty();
		
		Point tp = currentTarget.get(getLevel()).getPosition();
		
		val ps = safePoints.stream()
			.sorted(Comparator.comparingDouble(p -> Utils.chebyshevDistance(p.getX(), p.getY(), tp.getX(), tp.getY())))
			.collect(Collectors.toList());
		
		Collections.reverse(ps);
		
		return Optional.ofNullable(ps.get(0));
	}
	
	public void addTrait(AITrait trait) {
		traits.put(trait.getClass(), trait);
	}
	
	public void removeTrait(Class<? extends AITrait> traitClass) {
		traits.remove(traitClass);
	}
	
	public AITrait getTrait(Class<? extends AITrait> traitClass) {
		return traits.get(traitClass);
	}
	
	public void setCurrentState(AIState currentState) {
		setCurrentState(currentState, false);
	}
	
	public void setCurrentState(AIState currentState, boolean force) {
		if (currentState == null) return;
		if (!force && this.currentState != null && this.currentState.getClass() == currentState.getClass()) return;
		
		this.currentState = currentState;
	}
}
