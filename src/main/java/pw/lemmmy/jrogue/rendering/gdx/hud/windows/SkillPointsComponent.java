package pw.lemmmy.jrogue.rendering.gdx.hud.windows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import pw.lemmmy.jrogue.dungeon.entities.Player;
import pw.lemmmy.jrogue.rendering.gdx.utils.ImageLoader;

public class SkillPointsComponent extends Table {
	private Player player;

	private boolean strengthDisabled = false;
	private boolean agilityDisabled = false;
	private boolean dexterityDisabled = false;
	private boolean constitutionDisabled = false;
	private boolean intelligenceDisabled = false;
	private boolean wisdomDisabled = false;
	private boolean charismaDisabled = false;

	public SkillPointsComponent(Skin skin, Player player) {
		super(skin);

		this.player = player;

		update();
	}

	private void update() {
		clearChildren();
		checkDisabled();
		populate();
	}

	private void checkDisabled() {
		if (player.getSpendableSkillPoints() <= 0) {
			strengthDisabled = true;
			agilityDisabled = true;
			dexterityDisabled = true;
			constitutionDisabled = true;
			intelligenceDisabled = true;
			wisdomDisabled = true;
			charismaDisabled = true;

			return;
		}

		strengthDisabled = player.getStrength() >= 30;
		agilityDisabled = player.getAgility() >= 30;
		dexterityDisabled = player.getDexterity() >= 30;
		constitutionDisabled = player.getConstitution() >= 30;
		intelligenceDisabled = player.getIntelligence() >= 30;
		wisdomDisabled = player.getWisdom() >= 30;
		charismaDisabled = player.getCharisma() >= 30;
	}

	private void populate() {
		addAvailableLabel();

		addStrength();
		addAgility();
		addDexterity();
		addConstitution();
		addIntelligence();
		addWisdom();
		addCharisma();
	}

	private void addAvailableLabel() {
		String label = String.format(
			"[P_GREY_2]Available:  [%s]%,d[][]",
			player.getSpendableSkillPoints() == 0 ? "P_RED" : "P_GREEN_1",
			player.getSpendableSkillPoints()
		);

		add(new Label(label, getSkin(), "windowStyleMarkup")).colspan(3).padBottom(16);

		row().padBottom(4);
	}

	private void addStrength() {
		add(new Image(ImageLoader.getImageFromSheet("hud.png", 0, 2, 16, 16, false))).padRight(6);

		String label = String.format("[P_GREY_0]Strength:  [P_GREEN_1]%,d[][]", player.getStrength());
		add(new Label(label, getSkin(), "windowStyleMarkup")).width(180).left();

		Button button = new TextButton("Spend", getSkin());
		button.setDisabled(strengthDisabled);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);

				player.increaseStrength();
				update();
			}
		});

		add(button).width(60).right();

		row().padBottom(4);
	}

	private void addAgility() {
		add(new Image(ImageLoader.getImageFromSheet("hud.png", 1, 2, 16, 16, false))).padRight(6);

		String label = String.format("[P_GREY_0]Agility:  [P_GREEN_1]%,d[][]", player.getAgility());
		add(new Label(label, getSkin(), "windowStyleMarkup")).width(180).left();

		Button button = new TextButton("Spend", getSkin());
		button.setDisabled(agilityDisabled);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);

				player.increaseAgility();
				update();
			}
		});

		add(button).width(60).right();

		row().padBottom(4);
	}

	private void addDexterity() {
		add(new Image(ImageLoader.getImageFromSheet("hud.png", 2, 2, 16, 16, false))).padRight(6);

		String label = String.format("[P_GREY_0]Dexterity:  [P_GREEN_1]%,d[][]", player.getDexterity());
		add(new Label(label, getSkin(), "windowStyleMarkup")).width(180).left();

		Button button = new TextButton("Spend", getSkin());
		button.setDisabled(dexterityDisabled);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);

				player.increaseDexterity();
				update();
			}
		});

		add(button).width(60).right();

		row().padBottom(4);
	}

	private void addConstitution() {
		add(new Image(ImageLoader.getImageFromSheet("hud.png", 3, 2, 16, 16, false))).padRight(6);

		String label = String.format("[P_GREY_0]Constitution:  [P_GREEN_1]%,d[][]", player.getConstitution());
		add(new Label(label, getSkin(), "windowStyleMarkup")).width(180).left();

		Button button = new TextButton("Spend", getSkin());
		button.setDisabled(constitutionDisabled);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);

				player.increaseConstitution();
				update();
			}
		});

		add(button).width(60).right();

		row().padBottom(4);
	}

	private void addIntelligence() {
		add(new Image(ImageLoader.getImageFromSheet("hud.png", 4, 2, 16, 16, false))).padRight(6);

		String label = String.format("[P_GREY_0]Intelligence:  [P_GREEN_1]%,d[][]", player.getIntelligence());
		add(new Label(label, getSkin(), "windowStyleMarkup")).width(180).left();

		Button button = new TextButton("Spend", getSkin());
		button.setDisabled(intelligenceDisabled);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);

				player.increaseIntelligence();
				update();
			}
		});

		add(button).width(60).right();

		row().padBottom(4);
	}

	private void addWisdom() {
		add(new Image(ImageLoader.getImageFromSheet("hud.png", 5, 2, 16, 16, false))).padRight(6);

		String label = String.format("[P_GREY_0]Wisdom:  [P_GREEN_1]%,d[][]", player.getWisdom());
		add(new Label(label, getSkin(), "windowStyleMarkup")).width(180).left();

		Button button = new TextButton("Spend", getSkin());
		button.setDisabled(wisdomDisabled);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);

				player.increaseWisdom();
				update();
			}
		});

		add(button).width(60).right();

		row().padBottom(4);
	}

	private void addCharisma() {
		add(new Image(ImageLoader.getImageFromSheet("hud.png", 6, 2, 16, 16, false))).padRight(6);

		String label = String.format("[P_GREY_0]Charisma:  [P_GREEN_1]%,d[][]", player.getCharisma());
		add(new Label(label, getSkin(), "windowStyleMarkup")).width(180).left();

		Button button = new TextButton("Spend", getSkin());
		button.setDisabled(charismaDisabled);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);

				player.increaseCharisma();
				update();
			}
		});

		add(button).width(60).right();

		row().padBottom(4);
	}
}
