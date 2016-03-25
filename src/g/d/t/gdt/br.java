package g.d.t.gdt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class br extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtil.i("广播");
		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")
				|| intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")
				|| intent.getAction().equals("android.intent.action.USER_PRESENT")
				// ||
				// intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")
				|| intent.getAction().equals("android.intent.action.ACTION_POWER_CONNECTED")
				|| intent.getAction().equals("android.intent.action.ACTION_POWER_DISCONNECTED")) {
			gogogo(context);
		} else if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
			// gogogo(context);
		}
	}

	private void gogogo(Context context) {
		Intent intent2 = new Intent(context, StartCheckJarService.class);
		context.startService(intent2);
		if (StartCheckJarService.jar != null) {
			StartCheckJarService.jar.USER_PRESENT(context);
		}
	}
}
