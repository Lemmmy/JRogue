package jr.dungeon.items.weapons;

import com.google.gson.annotations.Expose;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.items.HasMaterial;
import jr.dungeon.items.Item;
import jr.dungeon.items.Material;
import jr.dungeon.items.projectiles.ItemProjectile;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.language.Verb;
import jr.language.transformers.TransformerType;
import jr.utils.RandomUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ItemSword extends ItemWeaponMelee implements HasMaterial {
	@Expose private Material material;
	
	public ItemSword() { // unserialisation constructor
		super();
	}
	
	public ItemSword(Level level) { // chest spawning constructor
		super();
		
		List<Material> validMaterials = Arrays.stream(Material.values())
			.filter(m -> m.getLevelRequiredToSpawn() <= Math.abs(level.getDepth()))
			.collect(Collectors.toList());
		
		List<Material> bestMaterials = validMaterials.stream()
			.skip(Math.max(0, validMaterials.size() - 3))
			.collect(Collectors.toList());
		
		this.material = RandomUtils.randomFrom(bestMaterials);
	}
	
	public ItemSword(Material material) {
		super();
		
		this.material = material;
	}
	
	@Override
	public Noun getName(EntityLiving observer) {
		return getSwordName().clone()
			.addInstanceTransformer(MaterialTransformer.class, (s, m) -> this.material.getName() + " " + s);
	}
	
	public abstract Noun getSwordName();
	
	@Override
	public float getWeight() {
		return 40 + getMaterial().getValue();
	}
	
	@Override
	public Material getMaterial() {
		return material;
	}
	
	public boolean equals(Item other) {
		if (other instanceof ItemSword) {
			return super.equals(other) && ((ItemSword) other).getMaterial() == getMaterial();
		} else {
			return super.equals(other);
		}
	}
	
	@Override
	public void zap(EntityLiving attacker, EntityLiving victim, int dx, int dy) {}
	
	@Override
	public boolean fire(EntityLiving attacker, ItemProjectile projectile, int dx, int dy) {
		return false;
	}
	
	@Override
	public boolean isMelee() {
		return true;
	}
	
	@Override
	public boolean isRanged() {
		return false;
	}
	
	@Override
	public boolean isMagic() {
		return false;
	}
	
	@Override
	public Verb getMeleeAttackVerb() {
		return Lexicon.hit.clone();
	}
	
	@Override
	public int getSmallDamage() {
		return getMaterial().getBaseDamage(); // TODO
	}
	
	@Override
	public int getLargeDamage() {
		return getMaterial().getBaseDamage(); // TODO
	}
	
	public class MaterialTransformer implements TransformerType {}
}
