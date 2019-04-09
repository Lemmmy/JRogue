package jr.dungeon.entities.projectiles;

import com.google.gson.annotations.Expose;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityTurnBased;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.Shatterable;
import jr.dungeon.items.projectiles.ItemProjectile;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.Solidity;
import jr.dungeon.tiles.Tile;
import jr.language.LanguageUtils;
import jr.language.transformers.Capitalise;
import jr.utils.Point;
import jr.utils.VectorInt;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Registered(id="projectile")
public abstract class EntityProjectile extends EntityTurnBased {
	@Expose @Getter private VectorInt direction;
	@Expose @Getter private int range = Integer.MAX_VALUE;
	@Expose @Getter private int distanceTravelled = 0;
	
	@Getter @Setter private Entity source = null;
	
	@Getter @Setter private ItemProjectile originalItem;
	
	public EntityProjectile(Dungeon dungeon, Level level, Point position) {
		super(dungeon, level, position);
		
		setMovementPoints(getMovementSpeed());
	}
	
	protected EntityProjectile() { super(); }
	
	public void setDirection(VectorInt direction) {
		this.direction = direction;
	}
	
	public void setTravelRange(int range) {
		this.range = range;
	}
	
	@Override
	public int getMovementSpeed() {
		return Dungeon.NORMAL_SPEED;
	}
	
	@Override
	public void move() {
		Point newPosition = getPosition().add(direction);
		
		if (getLevel().tileStore.getTile(newPosition).getType().getSolidity() != Solidity.SOLID) {
			setPosition(newPosition);
			distanceTravelled++;
			
			getLevel().entityStore.getEntitiesAt(newPosition)
				.filter(e -> e != this)
				.forEach(this::onHitEntity);
			
			if (distanceTravelled > range) {
				killProjectile();
			}
		} else {
			onHitTile(getLevel().tileStore.getTile(newPosition));
			killProjectile();
		}
	}
	
	@Override
	public void applyMovementPoints() {
		setMovementPoints(getMovementPoints() + getMovementSpeed());
	}
	
	public void onHitTile(Tile tile) {
		
	}
	
	public void onHitEntity(Entity victim) {
		if (victim instanceof EntityItem && ((EntityItem) victim).getItem() instanceof Shatterable) {
			getDungeon().log(
				"%s shatters into a thousand pieces!",
				LanguageUtils.object(getDungeon().getPlayer(), ((EntityItem) victim).getItem())
					.build(Capitalise.first)
			);
			victim.remove();
		}
	}
	
	public void dropItems() {
		if (originalItem == null) {
			return;
		}
		
		Optional<EntityItem> existingItem = getLevel().entityStore.getItemsAt(getPosition())
			.filter(e -> e.getItem().equals(originalItem))
			.findFirst();
		
		if (existingItem.isPresent()) {
			existingItem.get().getItemStack().addCount(1);
		} else {
			EntityItem droppedItem = new EntityItem(
				getDungeon(),
				getLevel(),
				getPosition(),
				new ItemStack(originalItem, 1)
			);
			
			getLevel().entityStore.addEntity(droppedItem);
		}
	}
	
	public void killProjectile() {
		remove();
	}
	
	@Override
	public boolean isStatic() {
		return false;
	}
	
	@Override
	public boolean canBeWalkedOn() {
		return true;
	}
}
