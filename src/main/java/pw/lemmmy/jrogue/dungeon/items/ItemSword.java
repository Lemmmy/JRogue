package pw.lemmmy.jrogue.dungeon.items;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.utils.RandomUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ItemSword extends ItemWeaponMelee implements HasMaterial {
	private Material material;
	
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
	public String getName(boolean requiresCapitalisation, boolean plural) {
		String s = plural ? "s" : "";
		String material = this.material.getName(requiresCapitalisation);
		
		return material + " " + getSwordName() + s;
	}
	
	public abstract String getSwordName();
	
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
	public void zap(LivingEntity attacker, LivingEntity victim) {}
	
	@Override
	public void fire(LivingEntity attacker, LivingEntity victim) {}
	
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
	public void onHit(LivingEntity attacker, LivingEntity victim) {
		hitLog("You hit the %s!", "The %s hits you!", "The %s hits the %s!", attacker, victim);
	}
	
	@Override
	public int getSmallDamage() {
		return getMaterial().getBaseDamage(); // TODO
	}
	
	@Override
	public int getLargeDamage() {
		return getMaterial().getBaseDamage(); // TODO
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("material", getMaterial().name());
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		material = Material.valueOf(obj.getString("material"));
	}
}
