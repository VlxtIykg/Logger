package logger;

import java.io.PrintStream;

public class Logger {
	private static final PrintStream output;

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

			out.print("]" + ANSIColor.RESET.getCode());
		} else {
			out.print(message + ANSIColor.RESET.getCode());
		}
	}

	// if using log() depth is 2, if using any other function depth is 3 or more.
	public static void log(Logger.Level level, int depth, Object... messages) {
		String color = ANSIColor.WHITE.getCode();
		if (level == Level.TRACE) color = ANSIColor.LIGHT_RED.getCode();
		else if (level == Level.INFO) color = ANSIColor.WHITE.getCode();
		else if (level == Level.DEBUG) color = ANSIColor.CYAN.getCode();
		else if (level == Level.WARN) color = ANSIColor.YELLOW.getCode();
		else if (level == Level.ERROR) color = ANSIColor.RED.getCode();
		synchronized (output) {
			output.format(ANSIColor.RESET.getCode() + color + "[" + getClassName(depth) + " | %s] ", level);
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
		// Extract the package name
		String className = new Throwable().getStackTrace()[depth].getClassName();
		int start = className.indexOf('[') + 1; // Start index of the package name
		int end = className.lastIndexOf('.'); // End index of the package name
		return className.substring(end + 1);
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
