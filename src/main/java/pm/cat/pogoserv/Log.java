package pm.cat.pogoserv;

import java.io.PrintStream;

public class Log {
	
	private static final boolean DEBUG = true;
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
			log(out, tag, fmt, p);
	}
	
	public static void i(String tag, String fmt, Object... p){
		log(out, tag, fmt, p);
	}
	
	public static void e(String tag, String fmt, Object... p){
		log(err, tag, fmt, p);
	}
	
	public static void w(String tag, String fmt, Object... p){
		log(out, tag, "W: " + fmt, p);
	}
	
	public static void e(String tag, Throwable e){
		log(err, tag, "%s", e.getClass().getSimpleName());
		e.printStackTrace(err);
	}
	
	private static synchronized void log(PrintStream s, String tag, String fmt, Object... p){
		out.printf("[%-10s] ", tag);
		out.printf(fmt, p);
		out.println();
	}
	
}
