package pm.cat.pogoserv.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Util {
	
	private static final DateFormat HTTP_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
	
	public static byte[] readFile(String file) throws IOException {
		return readFile(new File(file));
	}
	
	public static byte[] readFile(File f) throws IOException {
		byte[] ret = new byte[(int) f.length()];
		InputStream in = new FileInputStream(f);
		int r, idx = 0;
		while((r = in.read()) != -1){
			ret[idx++] = (byte) r;
		}
		in.close();
		return ret;
	}
	
	public static String readFileString(String file) throws IOException {
		return new String(readFile(file));
	}
	
	public static int[] toIntArray(Collection<Integer> a){
		int[] ret = new int[a.size()];
		int i = 0;
		for(int j : a)
			ret[i++] = j;
		return ret;
	}
	
	public static float[] toFloatArray(Collection<Float> a){
		float[] ret = new float[a.size()];
		int i = 0;
		for(float j : a)
			ret[i++] = j;
		return ret;
	}
	
	public static String formatString(String s){
		return Character.toUpperCase(s.charAt(0)) + 
				s.toLowerCase().substring(1);
	}
	
	public static <T> void randomAccessSet(ArrayList<T> ts, int idx, T t){
		while(ts.size() <= idx)
			ts.add(null);
		ts.set(idx, t);
	}
	
	public static int insertionPoint(int[] is, int i){
		return Math.abs(Arrays.binarySearch(is, i)) + 1;
	}
	
	public static String httpDate(long ts){
		return HTTP_DATE_FORMAT.format(new Date(ts));
	}
	
	public static long usedMemory(){
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}
	
	public static String base64encode(String s){
		return Base64.getEncoder().encodeToString(s.getBytes());
	}
	
	public static String base64decode(String s){
		return new String(Base64.getDecoder().decode(s));
	}
	
	
	
	static {
		HTTP_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
}
