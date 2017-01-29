package jr.dungeon.entities.monsters;

import lombok.Getter;
import org.apache.commons.lang3.Range;

@Getter
public class MonsterSpawn {
	private Range<Integer> levelRange;
	private Range<Integer> rangePerLevel;
	
	private Range<Integer> packRange;
	private boolean isPack = false;
	
	private Class<? extends Monster> monsterClass;
	
	public MonsterSpawn(Range<Integer> levelRange,
						Range<Integer> rangePerLevel,
						Class<? extends Monster> monsterClass) {
		this.levelRange = levelRange;
		this.rangePerLevel = rangePerLevel;
		
		this.monsterClass = monsterClass;
	}
	
	public MonsterSpawn(Range<Integer> levelRange,
						Range<Integer> rangePerLevel,
						Range<Integer> packRange,
						Class<? extends Monster> monsterClass) {
		this.levelRange = levelRange;
		this.rangePerLevel = rangePerLevel;
		
		this.packRange = packRange;
		this.isPack = true;
		
		this.monsterClass = monsterClass;
	}
}
