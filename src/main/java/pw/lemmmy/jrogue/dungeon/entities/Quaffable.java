package pw.lemmmy.jrogue.dungeon.entities;

public interface Quaffable {
	void quaff(LivingEntity quaffer);
	
	boolean canQuaff(LivingEntity quaffer);
	
	String getQuaffConfirmationMessage();
}
