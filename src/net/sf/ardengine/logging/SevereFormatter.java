package net.sf.ardengine.logging;

import net.sf.ardengine.input.console.ConsoleUI;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Formatter for logging. Before letting handler write log file, adds additional information to message.
 */
public class SevereFormatter extends Formatter{

	@Override
	public String getHead(Handler h) {
		return "ArdEngine error log \n"
				+loadRandomHint()+"\n\n"
				+new Date().toString()+"\n"
				+loadSystemInfo()+"\n";
	}
	
	@Override
	public String format(LogRecord record) {
        StringBuilder message =new StringBuilder(record.getSourceClassName()+" : "+record.getSourceMethodName()+"():\n");

        Throwable thrown = record.getThrown();

        if(thrown != null){
            thrown.printStackTrace();
            for(StackTraceElement el : thrown.getStackTrace()){
                message.append(el.getClassName())
					.append(": ")
					.append(el.getMethodName())
					.append(": ")
					.append(el.getLineNumber())
					.append("\n");
            }
        }

        message.append(record.getMessage()+"\n\n");

		ConsoleUI.printError(thrown);

		return message.toString();
	}
	
	public static final String quotesPath = "error-quotes.txt";
	
	/**
	 * @return random hint for crash reports
	 */
	public static String loadRandomHint(){
	     String result;
	     
	     InputStream quotesStream = null;
	     BufferedReader br = null;
	     try{
		     quotesStream = SevereFormatter.class.getResourceAsStream(quotesPath);
		     br = new BufferedReader(new InputStreamReader(quotesStream));

		     ArrayList<String> lines = new ArrayList<>();
		     
		     String line;
		     while((line = br.readLine()) != null){
		   	  lines.add(line);
		     }

		     int randomLine = (int)Math.round(Math.random()*(lines.size()-1));
		     
		     result = lines.get(randomLine);
	     }catch(Exception e){
	   	  throw new RuntimeException("Logger initialization: "+e);
	     }finally{
	   	  try{
	   		  if(quotesStream != null) quotesStream.close();
		   	  if(br != null) br.close();
	   	  }catch(Exception e){
              result = "So screwed, that even logging has its fails!";
          }
	     }
	     
	     return result;
	   }
	
	/**
	 * @return Information about OS and Java version
	 */
	private String loadSystemInfo(){
		return System.getProperty("os.name")+" "+System.getProperty("os.version")+" "+System.getProperty("os.arch")+"\n"
				+System.getProperty("java.vendor")+" "+System.getProperty("java.version")+"\n";
	}
	
}
