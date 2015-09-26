package com.phoneoverheard.db;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import android.content.Context;
import android.text.TextUtils;
import cn.linving.girls.db.DaoHelper;

import com.j256.ormlite.misc.TransactionManager;
import com.phoneoverheard.bean.Sms;
import com.phoneoverheard.interfaces.ConstantDB;

/**
 * 短信数据操作类
 * 
 * @author liulei
 * @date 2015-9-15 下午4:14:39
 * @version 1.0
 */
public class SmsUnit extends DaoHelper<Sms> {

	public SmsUnit(Context context) {

		this(context, Sms.class);
	}

	private SmsUnit(Context context, Class<Sms> clazz) {
		super(context, clazz);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 根据收件人获取收件箱最新的一条记录id
	 * 
	 * @author liulei
	 * @date 2015-9-24
	 * @param receiveNum
	 * @return int
	 */
	public int getLatestReceiveIdLocal(String receiveNum) {

		int id = Integer.MAX_VALUE;

		if (TextUtils.isEmpty(receiveNum)) {
			return id;
		}
		
		try {
			Sms sms = dao.queryBuilder().orderBy("idLocal", false).limit(1l).where().eq("receivePhoneNum", receiveNum)
					.queryForFirst();
			if (null != sms) {
				id = sms.getIdLocal();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return id;
	}

	/**
	 * 根据发件人获取发件箱最新的一条记录id
	 * 
	 * @author liulei
	 * @date 2015-9-24
	 * @param receiveNum
	 * @return int
	 */
	public int getLatestSendIdLocal(String sendNum) {

		int id = Integer.MAX_VALUE;

		if (TextUtils.isEmpty(sendNum)) {
			return id;
		}

		try {
			Sms sms = dao.queryBuilder().orderBy("idLocal", false).limit(1l).where().eq("sendPhoneNum", sendNum)
					.queryForFirst();
			if (null != sms) {
				id = sms.getIdLocal();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return id;
	}

	/**
	 * 检查本地数据库是否已存，并保存
	 * 
	 * @author liulei
	 * @date 2015-9-24
	 * @param list
	 *            void
	 */
	public void checkToSave(final List<Sms> list) {

		if (list == null || list.size() == 0) {
			return;
		}

		try {
			TransactionManager.callInTransaction(db.getConnectionSource(), new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					// TODO Auto-generated method stub

					for (Sms tmp : list) {
						Sms data = dao.queryBuilder().where().eq("sendPhoneNum", tmp.getSendPhoneNum()).and()
								.eq("receivePhoneNum", tmp.getReceivePhoneNum()).and().eq("content", tmp.getContent())
								.and().eq("idLocal", tmp.getIdLocal()).queryForFirst();

						if (null == data) {
							// 保存
							dao.create(tmp);
						}
					}

					return null;
				}
			});

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	/**
	 * 获取未上传的记录，最大10条
	 * 
	 * @author liulei
	 * @date 2015-9-16
	 * @return List<Sms>
	 */
	public List<Sms> getUnuploadDatas() {

		List<Sms> list = null;

		try {
			list = dao.queryBuilder().orderBy("id", true).limit(10l).where().eq("state", ConstantDB.STATE_UNUPLOAD)
					.query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 更改上传状态为成功
	 * 
	 * @author liulei
	 * @date 2015-9-18
	 * @param list
	 * @return boolean
	 */
	public boolean updateUploadSuccess(final List<Sms> list) {

		boolean rsl = false;

		try {
			TransactionManager.callInTransaction(db.getConnectionSource(), new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					// TODO Auto-generated method stub

					for (Sms tmp : list) {
						tmp.setState(ConstantDB.STATE_UPLOADED);
						dao.update(tmp);
					}

					return null;
				}
			});

			rsl = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rsl = false;
		}
		return rsl;
	}

}
