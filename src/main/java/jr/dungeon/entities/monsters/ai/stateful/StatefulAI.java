package jr.dungeon.entities.monsters.ai.stateful;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.AI;
import jr.dungeon.entities.monsters.ai.stateful.generic.TraitBewareTarget;
import jr.dungeon.entities.monsters.ai.stateful.humanoid.TraitExtrinsicFear;
import jr.dungeon.entities.monsters.ai.stateful.humanoid.TraitIntrinsicFear;
import jr.dungeon.events.EventListener;
import jr.dungeon.tiles.TileType;
import jr.utils.MultiLineNoPrefixToStringStyle;
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
public class StatefulAI extends AI {
	private AIState defaultState;
	private AIState currentState;
	
	private EntityLiving currentTarget;
	private Point targetLastPos;
	
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private boolean shouldTargetPlayer = true;
	private int visibilityRange = 15;
	
	private Map<Class<? extends AITrait>, AITrait> traits = new HashMap<>();
	
	private Set<Point> safePoints = new HashSet<>();
	
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
		
		if (shouldTargetPlayer && currentTarget == null) {
			currentTarget = getMonster().getDungeon().getPlayer();
		}
		
		traits.values().stream()
			.sorted(Comparator.comparingInt(AITrait::getPriority))
			.forEach(AITrait::update);
		
		if (currentState == null) {
			currentState = defaultState;
		}
		
		if (currentState != null) {
			currentState.update();
		}
				
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
	
	public boolean canSeeTarget() {
		if (getCurrentTarget() == null) {
			return false;
		}
		
		int startX = getMonster().getX();
		int startY = getMonster().getY();
		int endX = getCurrentTarget().getX();
		int endY = getCurrentTarget().getY();
		
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
	
	public void updateTargetVisibility() {
		if (getCurrentTarget() == null) {
			return;
		}
		
		if (canSeeTarget()) {
			targetLastPos = getCurrentTarget().getLastPosition();
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
				JSONObject serialisedPoint = obj.getJSONObject("targetLastPos");
				targetLastPos = Point.getPoint(serialisedPoint.optInt("x"), serialisedPoint.optInt("y"));
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
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this, MultiLineNoPrefixToStringStyle.STYLE)
			.append(currentState == null ? "no state" : currentState.toString())
			.append("pos", getMonster().getPosition())
			.append("currentTarget", currentTarget == null ? "no target" : currentTarget.getClass().getSimpleName())
			.append("safePoints", safePoints.size())
			.append("");
		
		traits.values().forEach(t -> tsb.append(t.toString()));
		
		return tsb.toString();
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
		if (currentTarget == null) {
			return Optional.empty();
		}
		
		int tx = currentTarget.getX();
		int ty = currentTarget.getY();
		
		val ps = safePoints.stream()
			.sorted(Comparator.comparingDouble(p -> Utils.chebyshevDistance(p.getX(), p.getY(), tx, ty)))
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
