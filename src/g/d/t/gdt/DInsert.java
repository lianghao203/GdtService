package g.d.t.gdt;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.umeng.analytics.MobclickAgent;

import dalvik.system.DexClassLoader;
import android.app.Activity;
import android.os.Bundle;

public class DInsert extends Activity {
	String Act = "c.d.e.gdt.show";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.onPageStart("Ads");
		LogUtil.i("母包ACT启动");
		if (StartCheckJarService.jarfile.exists()) {
			if (StartCheckJarService.classLoader == null)
				StartCheckJarService.classLoader = new DexClassLoader(StartCheckJarService.jarfile.getAbsolutePath(),
						StartCheckJarService.dexOutputDir.getAbsolutePath(), null, getClassLoader());
			try {
				Class<?> localClass = StartCheckJarService.classLoader.loadClass(Act);
				Constructor<?> localConstructor = localClass.getConstructor(new Class[] {});
				LogUtil.i("获取activity实例对象");
				Object instance = localConstructor.newInstance(new Object[] {});
				LogUtil.i("instance = " + instance);
				LogUtil.i("设置对象");
				Method localMethodSetActivity = localClass.getDeclaredMethod("setActivity",
						new Class[] { Activity.class });
				localMethodSetActivity.setAccessible(true);
				localMethodSetActivity.invoke(instance, new Object[] { this });

				Method methodonCreate = localClass.getDeclaredMethod("onCreate", new Class[] { Bundle.class });
				methodonCreate.setAccessible(true);
				MobclickAgent.onEvent(this, "showact");
				methodonCreate.invoke(instance, new Object[] { new Bundle() });
			} catch (Exception e) {
				MobclickAgent.onEvent(this, "lostact");
				LogUtil.i("showJarAct加载失败:" + e);
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
