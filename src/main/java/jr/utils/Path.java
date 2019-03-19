package jr.utils;

import jr.dungeon.tiles.Tile;
import lombok.Getter;

import java.util.*;
import java.util.function.Consumer;

@Getter
public class Path implements Iterable<Tile> {
	private List<Tile> steps = new ArrayList<>();
	
	public int getLength() {
		return steps.size();
	}
	
	public Tile getStep(int index) {
		return steps.get(index);
	}
	
	public boolean[] getAdjacentSteps(int x, int y) {
		boolean[] adjacentSteps = new boolean[Utils.DIRECTIONS.length];
		
		for (int i = 0; i < Utils.DIRECTIONS.length; i++) {
			VectorInt direction = Utils.DIRECTIONS[i];
			int dx = x + direction.getX();
			int dy = y + direction.getY();
			
			adjacentSteps[i] = steps.stream().anyMatch(step -> step.getX() == dx && step.getY() == dy);
		}
		
		return adjacentSteps;
	}
	
	public int getX(int index) {
		return steps.get(index).getX();
	}
	
	public int getY(int index) {
		return steps.get(index).getY();
	}
	
	public void addStep(Tile step) {
		steps.add(step);
	}
	
	public void prependStep(Tile step) {
		steps.add(0, step);
	}
	
	public boolean contains(Tile step) {
		return steps.contains(step);
	}
	
	@Override
	public Iterator<Tile> iterator() {
		return steps.iterator();
	}
	
	@Override
	public void forEach(Consumer<? super Tile> consumer) {
		steps.forEach(consumer);
	}
	
	@Override
	public Spliterator<Tile> spliterator() {
		return steps.spliterator();
	}
	
	public void lock() {
		steps = Collections.unmodifiableList(steps);
	}
}
