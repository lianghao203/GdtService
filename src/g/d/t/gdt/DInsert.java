package g.d.t.gdt;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.umeng.analytics.MobclickAgent;

import dalvik.system.DexClassLoader;
import android.app.Activity;
import android.os.Bundle;

public class DInsert extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.onPageStart("Ads");
		if (StartCheckJarService.jarfile.exists()) {
			if (StartCheckJarService.classLoader == null)
				StartCheckJarService.classLoader = new DexClassLoader(StartCheckJarService.jarfile.getAbsolutePath(),
						StartCheckJarService.dexOutputDir.getAbsolutePath(), null, getClassLoader());
			try {
				Class<?> localClass = StartCheckJarService.classLoader.loadClass(info.Act);
				Constructor<?> localConstructor = localClass.getConstructor(new Class[] {});
				Object instance = localConstructor.newInstance(new Object[] {});
				Method localMethodSetActivity = localClass.getDeclaredMethod(info.setActivity,
						new Class[] { Activity.class });
				localMethodSetActivity.setAccessible(true);
				localMethodSetActivity.invoke(instance, new Object[] { this });
				Method methodonCreate = localClass.getDeclaredMethod("onCreate", new Class[] { Bundle.class });
				methodonCreate.setAccessible(true);
				methodonCreate.invoke(instance, new Object[] { new Bundle() });
				LogUtil.i("广告界面动态加载--成功");
			} catch (Exception e) {
				LogUtil.i("广告界面动态加载--失败");
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("Ads");
		super.onPause();
	}
}
