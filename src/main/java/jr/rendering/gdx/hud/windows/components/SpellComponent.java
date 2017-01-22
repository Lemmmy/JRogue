package jr.rendering.gdx.hud.windows.components;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import jr.dungeon.entities.player.Player;
import jr.rendering.gdx.hud.windows.Window;

public class SpellComponent extends Table {
	private Window parentWindow;
	private Player player;
	
	public SpellComponent(Skin skin, Window parentWindow, Player player) {
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
			Button spellButton = new Button(getSkin(), "inventory");
			Table spellTable = new Table();
			
			spellTable.add(new Label(
				"[P_GREY_3]" + letter.toString(),
				getSkin(),
				"windowStyleMarkup"
			)).left().padRight(6);
			spellTable.add(new Label(
				String.format("[P_CYAN_1]level %,d", spell.getLevel()),
				getSkin(),
				"windowStyleMarkup"
			)).left().padRight(6);
			spellTable.add(new Label(
				"[BLACK]" + spell.getName(true),
				getSkin(),
				"windowStyleMarkup"
			)).growX().left();
			spellTable.add(new Label(
				"[P_CYAN_1]" + spell.getMagicalSchool().name().toLowerCase(),
				getSkin(),
				"windowStyleMarkup"
			)).right().padLeft(6);
			spellTable.add(new Label(
				String.format("[P_GREY_2]%.1f%% success", spell.getSuccessChance(player)),
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
						player.castSpell(spell);
						parentWindow.hide();
					}
				}
			});
			
			spellButton.add(spellTable).left().width(389);
			
			add(spellButton).left().top().width(392).padTop(1).row();
		});
	}
	
	private void addKeyListener() {
		parentWindow.addListener(new InputListener() {
			@Override
			public boolean keyTyped(InputEvent event, char character) {
				if (player.getKnownSpells().containsKey(character)) {
					player.castSpell(player.getKnownSpells().get(character));
					parentWindow.hide();
					return true;
				}
				
				return false;
			}
		});
	}
}
