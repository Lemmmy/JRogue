package pw.lemmmy.jrogue.rendering.swing;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.rendering.Renderer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;

public class SwingRenderer extends JFrame implements KeyListener, Renderer {
	private static final String WINDOW_TITLE = "JRogue";

	public Dungeon dungeon;

	public SwingRenderer(Dungeon dungeon) {
		super(WINDOW_TITLE);

		this.dungeon = dungeon;

		initialiseWindow();
	}

	private void initialiseWindow() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		initialiseLayout();
		updateWindowTitle();

		addKeyListener(this);

		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void initialiseLayout() {
		setLayout(new BorderLayout());
		setMinimumSize(new Dimension(480, 240));

		pack();
	}

	private void updateWindowTitle() {
		setTitle(WINDOW_TITLE + " - " + dungeon.getName());
	}

	@Override
	public void keyTyped(KeyEvent keyEvent) {

	}

	@Override
	public void keyPressed(KeyEvent keyEvent) {

	}

	@Override
	public void keyReleased(KeyEvent keyEvent) {

	}
}
