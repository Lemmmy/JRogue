package jr.dungeon.entities.monsters.ai.stateful;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.EntityReference;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.AI;
import jr.dungeon.entities.monsters.ai.stateful.generic.TraitBewareTarget;
import jr.dungeon.entities.monsters.ai.stateful.humanoid.TraitExtrinsicFear;
import jr.dungeon.entities.monsters.ai.stateful.humanoid.TraitIntrinsicFear;
import jr.dungeon.events.EventListener;
import jr.dungeon.serialisation.DungeonRegistries;
import jr.dungeon.serialisation.DungeonRegistry;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.TileType;
import jr.utils.Point;
import jr.utils.Utils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
	
	@Expose private Map<String, AITrait> traits = new HashMap<>();
	
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
	public void afterDeserialise() {
		super.afterDeserialise();
		
		if (currentTarget == null) currentTarget = new EntityReference<>();
		
		if (defaultState != null) defaultState.setAI(this);
		if (currentState != null) currentState.setAI(this);
		
		traits.values().forEach(t -> t.setAI(this));
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
	
	public static DungeonRegistry<AITrait> getAITraitRegistry() {
		return DungeonRegistries.findRegistryForClass(AITrait.class)
			.orElseThrow(() -> new RuntimeException("Couldn't find AITrait registry in StatefulAI"));
	}
	
	public static String getAITraitID(Class<? extends AITrait> traitClass) {
		return getAITraitRegistry().getID(traitClass)
			.orElseThrow(() -> new RuntimeException(String.format("Couldn't find ID for AITrait `%s` in StatefulAI", traitClass.getName())));
	}
	
	public void addTrait(AITrait trait) {
		traits.put(getAITraitID(trait.getClass()), trait);
	}
	
	public void removeTrait(Class<? extends AITrait> traitClass) {
		traits.remove(getAITraitID(traitClass));
	}
	
	public AITrait getTrait(Class<? extends AITrait> traitClass) {
		return traits.get(getAITraitID(traitClass));
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
