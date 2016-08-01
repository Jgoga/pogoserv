package pm.cat.pogoserv.core;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import pm.cat.pogoserv.Log;

public class PSThreadPoolExecutor extends ScheduledThreadPoolExecutor implements ThreadFactory, UncaughtExceptionHandler {

	public PSThreadPoolExecutor(int corePoolSize) {
		super(corePoolSize);
		setThreadFactory(this);
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		Log.e("Executor", "Uncaught exception in thread " + t);
		Log.e("Executor", e);
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread ret = new Thread(r, "PSThread-" + r.toString());
		Log.d("ThreadPool", "Creating new worker: %s", ret.toString());
		ret.setUncaughtExceptionHandler(this);
		return ret;
	}

}
