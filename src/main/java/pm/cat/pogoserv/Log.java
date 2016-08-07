package pm.cat.pogoserv;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	
	private static final DateFormat LOG_TIMEFMT = new SimpleDateFormat("dd.MM. HH:mm:ss");
	private static final boolean DEBUG = true;
	private static final boolean COLOR = System.console() != null &&
			!System.getProperty("os.name").contains("Windows");
	private static final String COLOR_ERR = "\u001b[1;31m";
	private static final String COLOR_WARN = "\u001b[1;33m";
	private static final String COLOR_TIME = "\u001b[1;30m";
	private static final String COLOR_TAG = "\u001b[1;35m";
	private static final String COLOR_RESET = "\u001b[0m";
	private static PrintStream out = System.out;
	private static PrintStream err = System.err;
	
	public static void setOut(PrintStream out){
		Log.out = out;
	}
	
	public static void setErr(PrintStream err){
		Log.err = err;
	}
	
	public static void d(String tag, String fmt, Object... p){
		if(DEBUG)
			log(out, null, tag, fmt, p);
	}
	
	public static void i(String tag, String fmt, Object... p){
		log(out, null, tag, fmt, p);
	}
	
	public static void e(String tag, String fmt, Object... p){
		log(err, COLOR_ERR, tag, fmt, p);
	}
	
	public static void w(String tag, String fmt, Object... p){
		log(out, COLOR_WARN, tag, fmt, p);
	}
	
	public static void e(String tag, Throwable e){
		log(err, COLOR_ERR, tag, "%s", e.getClass().getSimpleName());
		e.printStackTrace(err);
	}
	
	private static synchronized void log(PrintStream s, String color, String tag, String fmt, Object... p){
		String date = LOG_TIMEFMT.format(new Date());
		if(COLOR){
			date = COLOR_TIME + date;
			tag = COLOR_TAG + tag;
			fmt = (color == null ? COLOR_RESET : color) + fmt;
			if(color != null)
				fmt += COLOR_RESET;
		}
		
		out.print(date);
		out.printf(" %s: ", tag);
		out.printf(fmt, p);
		out.println();
	}
	
}
