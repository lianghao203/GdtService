package g.d.t.gdt;

import android.util.Log;

public class LogUtil {
	public static void i(String msg) {
		Log.e("info", "GDT_se:" + msg);
	}

	public static void i(long msg) {
		i(String.valueOf(msg));
	}

	public static void i(String tag, int msg) {
		i(String.valueOf(msg));
	}

	public static void i(String tag, boolean msg) {
		i(String.valueOf(msg));
	}
}
