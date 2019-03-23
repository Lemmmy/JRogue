package jr.rendering.entities.animations;

import jr.Settings;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.containers.EntityChest;
import jr.dungeon.entities.events.EntityDamagedEvent;
import jr.dungeon.entities.events.EntityKickedEntityEvent;
import jr.dungeon.entities.events.EntityMovedEvent;
import jr.dungeon.entities.events.ItemDroppedEvent;
import jr.dungeon.events.*;
import jr.rendering.screens.GameScreen;
import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AnimationProvider implements EventListener {
	private GameScreen renderer;
	private Settings settings;
	
	@Getter private List<EntityAnimation> entityAnimations = new LinkedList<>();
	@Getter private Map<Entity, EntityAnimationData> animationValues = new HashMap<>();
	
	public AnimationProvider(GameScreen renderer, Settings settings) {
		this.renderer = renderer;
		this.settings = settings;
	}
	
	public void update(float dt) {
		if (settings.isShowTurnAnimations()) {
			if (renderer.isTurnLerping()) {
				float lerpTime = renderer.getTurnLerpTime();
				float lerpDuration = GameScreen.TURN_LERP_DURATION;
				
				float t = lerpTime / lerpDuration;
				
				animationValues.clear();
				
				entityAnimations.forEach(anim -> {
					Entity e = anim.getEntity();
					
					if (!animationValues.containsKey(e)) {
						animationValues.put(e, new EntityAnimationData());
					}
					
					anim.update(animationValues.get(e), t);
				});
			} else if (renderer.isWasTurnLerping()) {
				entityAnimations.forEach(anim -> {
					if (animationValues.containsKey(anim.getEntity())) {
						EntityAnimationData data = animationValues.get(anim.getEntity());
						data.offsetX = 0;
						data.offsetY = 0;
					}
					
					anim.onTurnLerpStop();
				});
				
				entityAnimations.clear();
				animationValues.clear();
			}
		}
	}
	
	public void addAnimation(EntityAnimation animation) {
		entityAnimations.add(animation);
	}
	
	public EntityAnimationData getEntityAnimationData(Entity entity) {
		return animationValues.get(entity);
	}
	
	@EventHandler
	private void onEntityMoved(EntityMovedEvent event) {
		if (settings.isShowTurnAnimations()) {
			entityBeginLerp(event);
		}
	}
	
	private void entityBeginLerp(EntityMovedEvent e) {
		int dx = e.getNewX() - e.getLastX();
		int dy = e.getNewY() - e.getLastY();
		
		addAnimation(new AnimationEntityMove(renderer, e.getEntity(), dx, dy));
	}
	
	@EventHandler
	private void onItemDropped(ItemDroppedEvent e) {
		addAnimation(new AnimationItemDrop(renderer, e.getItemEntity()));
	}
	
	@EventHandler
	private void onChestKicked(EntityKickedEntityEvent e) {
		if (e.getVictim() instanceof EntityChest) {
			addAnimation(new AnimationChestKick(renderer, e.getVictim()));
		}
	}
	
	@EventHandler
	private void onEntityDamaged(EntityDamagedEvent e) {
		if (e.getAttacker() == null) return;
		
		addAnimation(new AnimationEntityDamaged(renderer, e.getVictim(), e.getAttacker()));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onBeforeTurn(BeforeTurnEvent e) {
		entityAnimations.clear();
		animationValues.clear();
	}
	
	@EventHandler()
	private void onLevelChange(LevelChangeEvent event) {
		entityAnimations.clear();
		animationValues.clear();
	}
}
