package com.github.VlxtIykg.logger;

import java.io.PrintStream;

public class Logger {
	private static final PrintStream output;
	private static final LogColor lc = new LogColor();

	private static void render(PrintStream out, Object message) {
		if (message.getClass().isArray()) {
			Object[] array = (Object[]) message;
			out.print("[");

			for (int i = 0; i < array.length; ++i) {
				out.print(array[i]);
				if (i + 1 < array.length) {
					out.print(",");
				}
			}

			out.print("]" + lc.ANSI_RESET);
		} else {
			out.print(message + lc.ANSI_RESET);
		}
	}

	// if using log() depth is 2, if using any other function depth is 3 or more.
	public static void log(Logger.Level level, int depth, Object... messages) {
		String color = lc.ANSI_WHITE;
		if (level == Level.TRACE) color = lc.ANSI_LIGHT_RED;
		else if (level == Level.INFO) color = lc.ANSI_WHITE;
		else if (level == Level.DEBUG) color = lc.ANSI_CYAN;
		else if (level == Level.WARN) color = lc.ANSI_YELLOW;
		else if (level == Level.ERROR) color = lc.ANSI_RED;
		synchronized (output) {
			output.format(lc.ANSI_RESET + color + "[" + getClassName(depth) + " | %s] ", level);
			for (int i = 0; i < messages.length; ++i) {
				if (i + 1 == messages.length && messages[i] instanceof Throwable) {
					output.println();
					((Throwable) messages[i]).printStackTrace(output);
				} else {
					render(output, messages[i]);
				}
			}
			output.println();
			output.flush();
		}
	}

	public static void trace(Object... messages) {
		log(Level.TRACE, 3, (Object[]) new Throwable().getStackTrace());
	}

	public static void info(Object... messages) {
		log(Level.INFO, 3, messages);
	}

	public static void debug(Object... messages) {
		log(Level.DEBUG, 3, messages);
	}

	public static void warn(Object... messages) {
		log(Level.WARN, 3, messages);
	}

	public static void error(Object... messages) {
		log(Level.ERROR, 3, messages);

	}

	private static String getClassName(final int depth) {
		return new Throwable().getStackTrace()[depth].getClassName().replace("com.golem.golemstonks.", "");
	}

	static {
		output = System.out;
	}

	public static enum Level {
		TRACE,
		DEBUG,
		INFO,
		WARN,
		ERROR;

		private Level() {
		}
	}
}
