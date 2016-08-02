package pm.cat.pogoserv.util;

public class Random {
	
	private static final java.util.Random random = new java.util.Random();
	
	public static int nextInt(int max){
		return random.nextInt(max);
	}
	
	// [min, max[
	public static int nextInt(int min, int max){
		return min + nextInt(max);
	}
	
	public static double nextGaussian(){
		return random.nextGaussian();
	}
	
	// [min, max[
	public static float nextFloat(float min, float max){
		return (float) (min + random.nextDouble() * (max - min));
	}
	
	public static <T> T nextElement(T[] ts){
		return ts[nextInt(ts.length)];
	}
	
	public static int nextElement(int[] is){
		return is[nextInt(is.length)];
	}
	
	public static float nextElement(float[] fs){
		return fs[nextInt(fs.length)];
	}
	
}
