package cn.pwrd.ipulltorefreshrecyclersample.sample;

import cn.pwrd.ipulltorefreshrecyclersample.sample.util.SimpleCrashHandler;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.*;

public class Application extends android.app.Application {
	@Override
	public void onCreate() {
		super.onCreate();
//		LeakCanary.install(this);

		Iconify.with(new FontAwesomeModule()).with(new EntypoModule()).with(new TypiconsModule())
				.with(new MaterialModule()).with(new MeteoconsModule()).with(new WeathericonsModule())
				.with(new SimpleLineIconsModule()).with(new IoniconsModule());

		SimpleCrashHandler.INSTANCE.init();
	}
}
