package pw.lemmmy.jrogue.dungeon.entities;

public interface Quaffable {
	void quaff(EntityLiving quaffer);
	
	boolean canQuaff(EntityLiving quaffer);
	
	String getQuaffConfirmationMessage(EntityLiving quaffer);
}
