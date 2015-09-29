/*
 *  Copyright 2012 Kobi Krasnoff
 * 
 * This file is part of Call recorder For Android.

    Call recorder For Android is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Call recorder For Android is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Call recorder For Android.  If not, see <http://www.gnu.org/licenses/>
 */
package com.phoneoverheard.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.phoneoverheard.interfaces.Constant;

public class PhoneStateReceiver extends BroadcastReceiver {

	private String phoneNumber;

	@Override
	public void onReceive(Context context, Intent intent) {
		phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		String extraState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		String action = intent.getAction();

		try {

			if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {

				Intent myIntent = new Intent(context, RecordService.class);
				myIntent.putExtra("commandType", Constant.STATE_OUTGOING_NUMBER);
				myIntent.putExtra("calledNum", phoneNumber);
				context.startService(myIntent);

			} else {
				if (extraState != null) {
					if (extraState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
						Intent myIntent = new Intent(context, RecordService.class);
						myIntent.putExtra("commandType", Constant.STATE_CALL_START);
						context.startService(myIntent);
					} else if (extraState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
						Intent myIntent = new Intent(context, RecordService.class);
						myIntent.putExtra("commandType", Constant.STATE_CALL_END);
						context.startService(myIntent);
					} else if (extraState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
						if (phoneNumber == null)
							phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
						Intent myIntent = new Intent(context, RecordService.class);
						myIntent.putExtra("commandType", Constant.STATE_INCOMING_NUMBER);
						myIntent.putExtra("callingNum", phoneNumber);
						context.startService(myIntent);
					}
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
