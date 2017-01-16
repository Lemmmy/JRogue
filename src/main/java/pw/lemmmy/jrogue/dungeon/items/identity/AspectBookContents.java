package pw.lemmmy.jrogue.dungeon.items.identity;

public class AspectBookContents extends Aspect {
	@Override
	public String getName() {
		return "Book contents";
	}
	
	@Override
	public boolean isPersistent() {
		return false;
	}
	
}
