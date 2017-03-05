package net.sf.ardengine.io;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formatter for logging. This one is for logging average activities before crash happened.
 */
public class AverageRunFormatter extends Formatter{
	@Override
	public String format(LogRecord record) {
		return record.getSourceClassName()+" -> "+record.getSourceMethodName()+": "+record.getMessage()+"\n";
	}
}
