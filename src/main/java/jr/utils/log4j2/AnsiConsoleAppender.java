package jr.utils.log4j2;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.fusesource.jansi.AnsiConsole;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Plugin(name="AnsiConsoleAppender", category="Core", elementType="Appender", printObject=true)
public final class AnsiConsoleAppender extends AbstractAppender {
	private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
	private final Lock readLock = rwLock.readLock();
	
	protected AnsiConsoleAppender(String name,
								  Filter filter,
								  Layout<? extends Serializable> layout,
								  boolean ignoreExceptions) {
		super(name, filter, layout, ignoreExceptions);
	}
	
	@Override
	public void append(LogEvent event) {
		readLock.lock();
		
		try {
			final byte[] bytes = getLayout().toByteArray(event);
			AnsiConsole.out().write(bytes);
		} catch (Exception e) {
			if (!ignoreExceptions()) {
				throw new AppenderLoggingException(e);
			}
		} finally {
			readLock.unlock();
		}
	}
	
	@PluginFactory
	public static AnsiConsoleAppender createAppender(
		@PluginAttribute("name") String name,
		@PluginElement("Layout") Layout<? extends Serializable> layout,
		@PluginElement("Filter") final Filter filter
	) {
		if (name == null) {
			LOGGER.error("No name provided for AnsiConsoleAppender");
			return null;
		}
		
		if (layout == null) {
			layout = PatternLayout.createDefaultLayout();
		}
		
		return new AnsiConsoleAppender(name, filter, layout, true);
	}
}
