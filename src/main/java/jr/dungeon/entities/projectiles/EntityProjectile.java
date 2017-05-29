package jr.dungeon.entities.projectiles;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityTurnBased;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.Shatterable;
import jr.dungeon.items.projectiles.ItemProjectile;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.language.LanguageUtils;
import jr.language.transformers.Capitalise;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.Optional;

public abstract class EntityProjectile extends EntityTurnBased {
	private int dx = 0, dy = 0;
	@Getter private int range = Integer.MAX_VALUE;
	@Getter private int distanceTravelled = 0;
	
	@Getter @Setter private Entity source = null;
	
	@Getter @Setter private ItemProjectile originalItem;
	
	public EntityProjectile(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
		
		setMovementPoints(getMovementSpeed());
	}
	
	public int getDeltaX() {
		return dx;
	}
	
	public int getDeltaY() {
		return dy;
	}
	
	public void setTravelDirection(int dx, int dy) {
		dx = Math.max(-1, Math.min(1, dx));
		dy = Math.max(-1, Math.min(1, dy));
		
		this.dx = dx;
		this.dy = dy;
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
		int x = getX() + dx;
		int y = getY() + dy;
		
		if (getLevel().tileStore.getTile(x, y).getType().getSolidity() != TileType.Solidity.SOLID) {
			setPosition(x, y);
			distanceTravelled++;
			
			getLevel().entityStore.getEntitiesAt(x, y).stream()
				.filter(e -> !(e == this))
				.forEach(this::onHitEntity);
			
			if (distanceTravelled > range) {
				killProjectile();
			}
		} else {
			onHitTile(getLevel().tileStore.getTile(x, y));
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
			getLevel().entityStore.removeEntity(victim);
		}
	}
	
	public void dropItems() {
		if (originalItem == null) {
			return;
		}
		
		Optional<EntityItem> existingItem = getLevel().entityStore.getEntitiesAt(getX(), getY()).stream()
			.filter(EntityItem.class::isInstance)
			.map(e -> (EntityItem) e)
			.filter(e -> e.getItem().equals(originalItem))
			.findFirst();
		
		if (existingItem.isPresent()) {
			existingItem.get().getItemStack().addCount(1);
		} else {
			EntityItem droppedItem = new EntityItem(
				getDungeon(),
				getLevel(),
				getX(),
				getY(),
				new ItemStack(originalItem, 1)
			);
			
			getLevel().entityStore.addEntity(droppedItem);
		}
	}
	
	public void killProjectile() {
		if (isBeingRemoved()) {
			return;
		}
		
		getLevel().entityStore.removeEntity(this);
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("dx", dx);
		obj.put("dy", dy);
		obj.put("range", range);
		obj.put("distanceTravelled", distanceTravelled);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		dx = obj.optInt("dx", 0);
		dy = obj.optInt("dy", 0);
		range = obj.optInt("range", 0);
		distanceTravelled = obj.optInt("distanceTravelled", 0);
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
