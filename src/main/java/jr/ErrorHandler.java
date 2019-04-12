package jr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import jr.utils.OperatingSystem;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.GL11;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Error reporter dialog.
 */
public class ErrorHandler {
    /**
     * URL to post issues to.
     */
    private static final String ISSUES_URL = "https://github.com/Lemmmy/JRogue/issues/new?title=%s&body=%s";
    
    /**
     * The textarea containing error details.
     */
    private static final JTextArea textArea = new JTextArea(15, 50);
    
    static {
        textArea.setEditable(false);
        textArea.setTabSize(2);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
    }
    
    /**
     * Scroll pane containing textarea.
     */
    private static final JScrollPane scrollPane = new JScrollPane(textArea);
    
    /**
     * Information about the user's GL version and vendor.
     */
    private static String glString;
    
    /**
     * Sets the {@link #glString}.
     */
    public static void setGLString() {
        try {
            String glVersion = GL11.glGetString(GL11.GL_VERSION);
            String glVendor = GL11.glGetString(GL11.GL_VENDOR);
            glString = String.format("%s (%s)", glVersion, glVendor);
        } catch (Exception ignored) {}
    }
    
    /**
     * Cleanly crash the game with an error dialog, with the option to report it.
     *
     * @param error A description of the error that occurred.
     * @param trace The stack trace related to the error.
     */
    public static void error(String error, Throwable trace) {
        if (error != null) {
            JRogue.getLogger().error("An error occurred: {}", error);
            
            textArea.setText(error);
            textArea.append("\n");
        }
        
        String traceString = "";
        
        if (trace != null) {
            JRogue.getLogger().error("Error caught:", trace);
            
            StringWriter sw = new StringWriter();
            trace.printStackTrace(new PrintWriter(sw));
            traceString = sw.toString();
            
            textArea.append(traceString);
        }
        
        try {
            Desktop desktop = Desktop.getDesktop();
            
            int n = JOptionPane.showOptionDialog(
                null,
                new Object[]{"An error has occurred in JRogue. Please report this.", scrollPane},
                "JRogue Error",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                new String[]{"Send Report", "Close"},
                "Close"
            );
            
            if (n == 0) {
                URI issueURI = getIssueURI(error, trace, traceString);
                
                if (issueURI != null) {
                    desktop.browse(issueURI);
                }
            }
        } catch (Exception e) {
            JRogue.getLogger().error("An error occurred in the crash popup.", e);
        }
        
        LogManager.shutdown();
        System.exit(1);
    }
    
    private static URI getIssueURI(String error, Throwable trace, String traceString) {
        String issueTitle = error != null ? error : trace.getMessage();
        
        if (issueTitle == null) {
            issueTitle = "Unknown error";
        }
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(String.format(
            "**Version:** %s (build %s)\n",
            JRogue.VERSION,
            JRogue.BUILD_DATE
        ));
        
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
    
    public static void glErrorCheck(String pos) {
        int error;
        while ((error = Gdx.gl.glGetError()) != GL20.GL_NO_ERROR) {
            JRogue.getLogger().error("Error at {}: {}", pos, Integer.toHexString(error));
        }
    }
}
