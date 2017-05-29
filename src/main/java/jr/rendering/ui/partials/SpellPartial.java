package jr.rendering.ui.partials;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import jr.dungeon.entities.player.Player;
import jr.language.transformers.Capitalise;
import jr.rendering.ui.windows.Window;

public class SpellPartial extends Table {
	private Window parentWindow;
	private Player player;
	
	public SpellPartial(Skin skin, Window parentWindow, Player player) {
		super(skin);
		
		this.parentWindow = parentWindow;
		this.player = player;
		
		update();
	}
	
	private void update() {
		clearListeners();
		clearChildren();
		showSpells();
		top();
		addKeyListener();
	}
	
	private void showSpells() {
		player.getKnownSpells().forEach((letter, spell) -> {
			Button spellButton = new Button(getSkin(), "containerEntry");
			Table spellTable = new Table();
			
			Label letterLabel = new Label(
				"[WHITE]" + letter.toString(),
				getSkin(),
				"windowStyleRaisedMarkup"
			);
			letterLabel.setAlignment(Align.center);
			spellTable.add(letterLabel).left().width(15).height(16).padRight(6);
			spellTable.add(new Label(
				String.format("[P_CYAN_1]level %,d", spell.getLevel()),
				getSkin(),
				"windowStyleMarkup"
			)).left().padRight(6);
			spellTable.add(new Label(
				"[WHITE]" + spell.getName().build(Capitalise.first),
				getSkin(),
				"windowStyleMarkup"
			)).growX().left();
			spellTable.add(new Label(
				"[P_CYAN_1]" + spell.getMagicalSchool().name().toLowerCase(),
				getSkin(),
				"windowStyleMarkup"
			)).right().padLeft(6);
			spellTable.add(new Label(
				String.format("[P_GREY_4]%.1f%% success", spell.getSuccessChance(player)),
				getSkin(),
				"windowStyleMarkup"
			)).right().padLeft(6);
			spellTable.add(new Label(
				String.format(
					"[%s]%,d energy",
					spell.getCastingCost() > player.getEnergy() ? "P_RED" : "P_GREEN_2",
					spell.getCastingCost()
				),
				getSkin(),
				"windowStyleMarkup"
			)).right().padLeft(6);
			spellTable.row();
			
			spellButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if (event.getButton() == Input.Buttons.LEFT) {
						player.defaultVisitors.castSpell(spell);
						parentWindow.hide();
					}
				}
			});
			
			spellButton.add(spellTable).left().width(374).pad(2);
			
			add(spellButton).left().top().width(380).row();
		});
	}
	
	private void addKeyListener() {
		parentWindow.addListener(new InputListener() {
			@Override
			public boolean keyTyped(InputEvent event, char character) {
				if (player.getKnownSpells().containsKey(character)) {
					player.defaultVisitors.castSpell(player.getKnownSpells().get(character));
					parentWindow.hide();
					return true;
				}
				
				return false;
			}
		});
	}
}
