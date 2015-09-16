package com.phoneoverheard.phone;

import com.phoneoverheard.interfaces.Constant;
import com.phoneoverheard.util.StringUtils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *
 * 
 * @author liulei
 * @date 2015-7-14 2:16:28
 * @version 1.0
 */
public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		String action = intent.getAction();
		if (null != action && action.equals(Constant.INTENT_ACTION_TIMER_SYSTEM)) {
			context.sendBroadcast(new Intent(Constant.INTENT_ACTION_TIMER_CHECK_TO_SUBMIT));
		}
	}

	/**
	 * 
	 * 
	 * @author liulei
	 * @date 2015-7-14
	 * @param intertal
	 *            ��Сʱһ�� void
	 */
	public static void startRepeatAlarmer(Context context, int intertalHours) {

		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.setAction(Constant.INTENT_ACTION_TIMER_SYSTEM);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);

		am.setRepeating(AlarmManager.RTC_WAKEUP, StringUtils.getTargetTime(intertalHours).getTimeInMillis(),
				intertalHours * 60 * 60 * 1000, pendingIntent);
		// am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
		// 10 * 1000, pendingIntent);
	}
}
