package pw.lemmmy.jrogue;

import org.apache.logging.log4j.core.util.IOUtils;
import org.lwjgl.opengl.GL11;
import pw.lemmmy.jrogue.utils.OperatingSystem;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ErrorHandler {
	private static final String ISSUES_URL = "https://github.com/Lemmmy/JRogue/issues/new?title=%s&body=%s";

	private static final JTextArea textArea = new JTextArea(7, 30);

	static {
		textArea.setEditable(false);
		textArea.setTabSize(2);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
	}

	private static final JScrollPane scrollPane = new JScrollPane(textArea);

	private static String glString;

	public static void setGLString() {
		try {
			String glVersion = GL11.glGetString(GL11.GL_VERSION);
			String glVendor = GL11.glGetString(GL11.GL_VENDOR);
			glString = String.format("%s (%s)", glVersion, glVendor);
		} catch (Exception ignored) {}
	}

	public static void error(String error, Throwable trace) {
		if (error != null) {
			JRogue.getLogger().error("An error occurred: {}", error);

			textArea.setText(error);
			textArea.append("\n");
		}

		String traceString = "";

		if (trace != null) {
			JRogue.getLogger().error(trace);

			StringWriter sw = new StringWriter();
			trace.printStackTrace(new PrintWriter(sw));
			traceString = sw.toString();

			textArea.append(traceString);
		}

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			Desktop desktop = Desktop.getDesktop();

			int n = JOptionPane.showOptionDialog(
				null,
				new Object[] { "An error has occurred in JRogue. Please report this.", scrollPane },
				"JRogue Error",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE,
				null,
				new String[] {"Send Report", "Close"},
				"Close"
			);

			if (n == 0) {
				desktop.browse(getIssueURI(error, trace, traceString));
			}
		} catch (Exception e) {
			JRogue.getLogger().error("An error occured in the crash popup.", e);
		}
	}

	private static URI getIssueURI(String error, Throwable trace, String traceString) {
		String issueTitle = error != null ? error : trace.getMessage();

		if (issueTitle == null) {
			issueTitle = "Unknown error";
		}

		StringBuilder sb = new StringBuilder();

		sb.append(String.format(
			"**OS:** %s (%s)\n",
			System.getProperty("os.name"),
			System.getProperty("os.arch")
		));

		sb.append(String.format(
			"**Java:** %s\n",
			System.getProperty("java.version")
		));

		if (glString != null) {
			sb.append(String.format(
				"**GL:** %s\n",
				glString
			));
		}

		if (error != null) {
			sb.append(String.format(
				"**Error:** %s\n",
				error
			));
		}

		if (traceString != null) {
			sb.append(String.format(
				"**Trace:**\n```\n%s\n```\n",
				traceString
			));
		}

		Path logFile = Paths.get(
			OperatingSystem.get().getAppDataDir().toString(),
			"jrogue",
			"logs",
			"jrogue.latest.log"
		);

		if (logFile.toFile().exists()) {
			try {
				String log = new String(Files.readAllBytes(logFile));

				sb.append(String.format(
					"**Whole Log:**\n```\n%s\n```\n",
					log
				));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			return URI.create(String.format(
				ISSUES_URL,
				URLEncoder.encode(issueTitle, "UTF-8"),
				URLEncoder.encode(sb.toString(), "UTF-8")
			));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
