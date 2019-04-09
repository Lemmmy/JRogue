package jr.utils.log4j2;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.CloseShieldOutputStream;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.fusesource.jansi.WindowsAnsiPrintStream;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Plugin(name="AnsiConsoleAppender", category="Core", elementType="Appender", printObject=true)
public final class AnsiConsoleAppender extends AbstractAppender {
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private OutputStream os;
    
    protected AnsiConsoleAppender(String name,
                                  Filter filter,
                                  Layout<? extends Serializable> layout,
                                  boolean ignoreExceptions,
                                  final ConsoleAppender.Target target,
                                  Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
        
        os = getOutputStream(false, true, target);
    }
    
    @Override
    public void append(LogEvent event) {
        readLock.lock();
        
        try {
            String s = new String(getLayout().toByteArray(event))
                .replaceAll("\\[]", "\u001b[0m")
                .replaceAll("\\[RED]", "\u001b[31m")
                .replaceAll("\\[ORANGE]", "\u001b[31m")
                .replaceAll("\\[YELLOW]", "\u001b[33m")
                .replaceAll("\\[GREEN]", "\u001b[32m")
                .replaceAll("\\[BLUE]", "\u001b[34m")
                .replaceAll("\\[CYAN]", "\u001b[36m")
                .replaceAll("\\[GR[AE]Y]", "\u001b[37m")
                + "\u001b[0m";
            
            os.write(s.getBytes());
        } catch (Exception e) {
            if (!ignoreExceptions()) {
                throw new AppenderLoggingException(e);
            }
        } finally {
            readLock.unlock();
        }
    }
    
    private static OutputStream getOutputStream(final boolean follow, final boolean direct, final ConsoleAppender.Target target) {
        final String enc = Charset.defaultCharset().name();
        OutputStream outputStream;
        
        try {
            outputStream = target == ConsoleAppender.Target.SYSTEM_OUT ?
                           direct ? new FileOutputStream(FileDescriptor.out) :
                           follow ? new PrintStream(new SystemOutStream(), true, enc) : System.out :
                           direct ? new FileOutputStream(FileDescriptor.err) :
                           follow ? new PrintStream(new SystemErrStream(), true, enc) : System.err;
            outputStream = new CloseShieldOutputStream(outputStream);
        } catch (final UnsupportedEncodingException ex) { // should never happen
            throw new IllegalStateException("Unsupported default encoding " + enc, ex);
        }
        
        final PropertiesUtil propsUtil = PropertiesUtil.getProperties();
        
        if (!propsUtil.isOsWindows() || propsUtil.getBooleanProperty("log4j.skipJansi") || direct) {
            return outputStream;
        }
        
        try {
            return new CloseShieldOutputStream(new WindowsAnsiPrintStream(new PrintStream(outputStream)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return outputStream;
    }
    
    
    /**
     * An implementation of OutputStream that redirects to the current System.err.
     */
    private static class SystemErrStream extends OutputStream {
        public SystemErrStream() {
        }
        
        @Override
        public void close() {
            // do not close sys err!
        }
        
        @Override
        public void flush() {
            System.err.flush();
        }
        
        @Override
        public void write(final byte[] b) throws IOException {
            System.err.write(b);
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) {
            System.err.write(b, off, len);
        }
        
        @Override
        public void write(final int b) {
            System.err.write(b);
        }
    }
    
    /**
     * An implementation of OutputStream that redirects to the current System.out.
     */
    private static class SystemOutStream extends OutputStream {
        public SystemOutStream() {
        }
        
        @Override
        public void close() {
            // do not close sys out!
        }
        
        @Override
        public void flush() {
            System.out.flush();
        }
        
        @Override
        public void write(final byte[] b) throws IOException {
            System.out.write(b);
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) {
            System.out.write(b, off, len);
        }
        
        @Override
        public void write(final int b) {
            System.out.write(b);
        }
    }
    
    @PluginFactory
    public static AnsiConsoleAppender createAppender(
        @PluginAttribute("name") String name,
        @PluginElement("Layout") Layout<? extends Serializable> layout,
        @PluginElement("Filter") final Filter filter,
        @PluginAttribute(value = "target", defaultString = "SYSTEM_OUT") final String targetStr
    ) {
        if (name == null) {
            LOGGER.error("No name provided for AnsiConsoleAppender");
            return null;
        }
        
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        
        ConsoleAppender.Target target = targetStr == null ?
                                        ConsoleAppender.Target.SYSTEM_OUT :
                                        ConsoleAppender.Target.valueOf(targetStr);
        
        return new AnsiConsoleAppender(name, filter, layout, true, target, Property.EMPTY_ARRAY);
    }
}
