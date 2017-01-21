package pw.lemmmy.jrogue.dungeon.entities.monsters.ai.stateful;

import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.stateful.StatefulAI;

public class AIState {
	private StatefulAI ai;
	
	private int duration = 0;
	private int turnsTaken = 0;
	
	public AIState(StatefulAI ai, int duration) {
		this.ai = ai;
		this.duration = duration;
	}
	
	public void update() {
		turnsTaken = Math.max(0, turnsTaken - 1);
	}
	
	public StatefulAI getAI() {
		return ai;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public int getTurnsTaken() {
		return turnsTaken;
	}
}
