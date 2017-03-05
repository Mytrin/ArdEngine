package net.sf.ardengine.io;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArdLogger {

	public static final String CRASH_LOG = "ardengine_crash_report.log";
	public static final String LAST_LOG = "ardengine_last_run_report.log";
	
	/**
	 * Initializes global logger so it can record to specified files.
	 * 
	 * @param level logging level of messages, which will logger handle
	 */
	public static void initLogger(Level level) {
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		logger.setLevel(level);

		Handler[] oldHandlers = logger.getHandlers();
		for(Handler handler: oldHandlers){
			logger.removeHandler(handler);
		}
		
		FileHandler severeHandler;
		FileHandler averageRunHandler;

		try {
			// remove old logs
			File oldCrashLog = new File(CRASH_LOG);
			if (oldCrashLog.exists()) {
				oldCrashLog.delete();
			}
			File oldRunLog = new File(LAST_LOG);
			if (oldRunLog.exists()) {
				oldRunLog.delete();
			}
			// create new handlers, first for errors and second for everything
			severeHandler = new FileHandler(CRASH_LOG, true);
			severeHandler.setFormatter(new SevereFormatter());
			severeHandler.setLevel(Level.SEVERE);

			averageRunHandler = new FileHandler(LAST_LOG, true);
			averageRunHandler.setFormatter(new AverageRunFormatter());
		} catch (Exception e) {
			System.err.println("Failed to create FileHandlers!");
			throw new RuntimeException("Failed to create FileHandlers!");
		}

		logger.addHandler(severeHandler);
		logger.addHandler(averageRunHandler);
	}
}
