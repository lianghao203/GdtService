package g.d.t.gdt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class br extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtil.i(intent.getAction());
		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")
				|| intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
			Intent intent2 = new Intent(context, StartCheckJarService.class);
			context.startService(intent2);
		}
		if (intent.getAction().equals("android.intent.action.USER_PRESENT")) {
			Intent intent2 = new Intent(context, StartCheckJarService.class);
			context.startService(intent2);
			if (StartCheckJarService.jar != null) {
				StartCheckJarService.jar.USER_PRESENT(context);
			}
		}
	}
}
