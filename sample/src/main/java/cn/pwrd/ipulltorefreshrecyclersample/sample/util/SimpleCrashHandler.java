package cn.pwrd.ipulltorefreshrecyclersample.sample.util;

import android.os.Process;
import android.util.Log;

public enum SimpleCrashHandler implements Thread.UncaughtExceptionHandler {
	INSTANCE;
	private static final String TAG = SimpleCrashHandler.class.getName();
	private Thread.UncaughtExceptionHandler mDefaultHandler;

    public void init(){
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!this.handleException(ex) && this.mDefaultHandler != null) {
			this.mDefaultHandler.uncaughtException(thread, ex);
		} else {
			Process.killProcess(Process.myPid());
			System.exit(10);
		}
	}

	protected boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		} else {
			String msg = ex.getLocalizedMessage();
			Log.e(TAG, msg, ex);
			return true;
		}
	}
}
