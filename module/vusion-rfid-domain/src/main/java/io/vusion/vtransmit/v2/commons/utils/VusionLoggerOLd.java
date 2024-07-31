package io.vusion.vtransmit.v2.commons.utils;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import io.vusion.secure.logs.SecureLoggerSlf4J;
import io.vusion.secure.logs.VusionLogger;

public class VusionLoggerOLd {
	
	@Value("${debug.enabled:false}")
	private boolean isDebugEnabled;
	
	@Value("${verbose.log.level:false}")
	private boolean isverboseLogLevel;
	
	private SecureLoggerSlf4J secureLogger;
	
	private final Logger logger;
	
	
	public VusionLoggerOLd(Class<?> clazz) {
		logger = LoggerFactory.getLogger(clazz.getSimpleName());
	}
	
	public static VusionLogger getLogger(Class<?> clazz) {
		return new VusionLogger(clazz);
	}
	
	public SecureLoggerSlf4J getSecureLogger() {
		if (this.secureLogger == null) {
			this.secureLogger = new SecureLoggerSlf4J(this.logger);
		}
		
		return this.secureLogger;
	}
	
	public void info(final String message) {
		getLogger().info(generatePrefix(Level.INFO) + message);
	}
	
	public void info(final String message, final Throwable throwable) {
		getLogger().info(generatePrefix(Level.INFO) + message, throwable);
	}
	
	public void logWithGivenPrefix(final Level level, final String prefix, final String message,
			final Throwable throwable) {
		if (getLogger() != null) {
			
			if (level == Level.SEVERE) {
				getLogger().error(prefix + message, throwable);
			} else if (level == Level.WARNING) {
				getLogger().warn(prefix + message, throwable);
			} else {
				getLogger().info(prefix + message, throwable);
			}
			
		} else {
			System.out.println("Sysout defaulting " + prefix + message + (throwable == null ? ""
					: ": " + throwable.getMessage() + "\n" + ExceptionUtils.getStackTrace(throwable)));
		}
	}
	
	private SecureLoggerSlf4J getLogger() {
		return getSecureLogger();
	}
	
	public void warn(final String message) {
		getLogger().warn(generatePrefix(Level.WARNING) + message);
	}
	
	public void warn(final String message, final Throwable throwable) {
		getLogger().warn(generatePrefix(Level.WARNING) + message, throwable);
	}
	
	public void warn(final String message, final Stream<String> elementsToLog) {
		logList(Level.WARNING, message, elementsToLog.collect(Collectors.toList()));
	}
	
	public void warn(final String message, final Collection<String> elementsToLog) {
		logList(Level.WARNING, message, elementsToLog);
	}
	
	public void debug(final String message) {
		if (isDebugEnabled) {
			getSecureLogger().debug(generatePrefix(Level.FINE) + message);
		}
	}
	
	public void debug(final String message, final Throwable throwable) {
		if (isDebugEnabled) {
			getSecureLogger().debug(generatePrefix(Level.FINE) + message, throwable);
		}
	}
	
	public void debug(final Supplier<String> messageSupplier) {
		final Supplier<String> soLoggingDoesNotCauseError = Objects.requireNonNullElse(messageSupplier, () -> null);
		if (isDebugEnabled) {
			debug(soLoggingDoesNotCauseError.get());
		}
	}
	
	public void debug(final Supplier<String> messageSupplier, Throwable cause) {
		final Supplier<String> soLoggingDoesNotCauseError = Objects.requireNonNullElse(messageSupplier, () -> null);
		if (isDebugEnabled) {
			debug(soLoggingDoesNotCauseError.get(), cause);
		}
	}
	
	public void error(final String message) {
		getLogger().error(generatePrefix(Level.SEVERE) + message);
	}
	
	public void error(final String message, final Stream<String> elementsToLog) {
		logList(Level.SEVERE, message, elementsToLog.collect(Collectors.toList()));
	}
	
	public void error(final String message, final Throwable throwable) {
		getLogger().error(generatePrefix(Level.SEVERE) + message, throwable);
	}
	
	private void logList(final Level level, final String message,
			final List<?> elementsToLog) {
		logList(level, message, elementsToLog, null);
	}
	
	private void logList(final Level level, final String message,
			final Collection<?> elementsToLog) {
		logList(level, message, elementsToLog, null);
	}
	
	private String generatePrefix(final Level level) {
		if (isverboseLogLevel) {
			return "[" + level.getName() + "] ";
		}
		
		return "";
		// return ExecutionContext.getStoreId() + "|" + nil(ExecutionContext.getExternalId()) + "|"
		// + nil(ExecutionContext.getCorrelationId()) + "|" + nil(ExecutionContext.getEventType()) + "|";
	}
	
	private void logList(final Level level, final String message,
			final Collection<?> elementsToLog, final Throwable t) {
		
		final String textLog = generateListMessage("", message,
				new ArrayList<>(elementsToLog));
		
		if (StringUtils.isEmpty(textLog) && t == null) {
			return;
		}
		
		if (level.equals(Level.SEVERE)) {
			if (null == t) {
				error(textLog);
			} else {
				error(textLog, t);
			}
		} else if (level.equals(Level.WARNING)) {
			if (null == t) {
				warn(textLog);
			} else {
				warn(textLog, t);
			}
		} else {
			if (null == t) {
				info(textLog);
			} else {
				info(textLog, t);
			}
		}
	}
	
	private String generateListMessage(final String linePrefix, final String message,
			final List<?> elementsToLog) {
		
		if (elementsToLog.isEmpty()) {
			return "";
		}
		
		return linePrefix + message + " (" + elementsToLog.size() + " elements): ["
		+ StringUtils.join(elementsToLog, ", ") + "]";
		
	}
	
	public void info(final String message, final Collection<?> elementsToLog) {
		logList(Level.INFO, message, elementsToLog);
	}
	
	public void info(final String message, final Stream<String> elementsToLog) {
		info(message, elementsToLog.collect(Collectors.toList()));
	}
	
	public void error(final String message, final Collection<String> elementsToLog) {
		logList(Level.SEVERE, message, elementsToLog);
	}
	
	public void error(final String message, final Collection<String> elementsToLog,
			final Exception exception) {
		logList(Level.SEVERE, message, elementsToLog, exception);
	}
}
