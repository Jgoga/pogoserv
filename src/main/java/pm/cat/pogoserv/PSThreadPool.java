package pm.cat.pogoserv;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class PSThreadPool extends ScheduledThreadPoolExecutor implements ThreadFactory, UncaughtExceptionHandler {

	public PSThreadPool(int corePoolSize) {
		super(corePoolSize);
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread ret = new Thread(r);
		ret.setName("PSWorker-" + Long.toHexString(ret.hashCode()));
		ret.setUncaughtExceptionHandler(this);
		Log.d("ThreadPool", "Init worker %s", ret);
		return ret;
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		Log.e(t.getName(), "Uncaugh exception");
		Log.e(t.getName(), e);
	}

}
