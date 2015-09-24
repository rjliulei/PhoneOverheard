package com.phoneoverheard.db;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import android.content.Context;
import cn.linving.girls.db.DaoHelper;

import com.j256.ormlite.misc.TransactionManager;
import com.phoneoverheard.bean.LocNormal;
import com.phoneoverheard.interfaces.ConstantDB;

/**
 * 定位数据操作类
 * 
 * @author liulei
 * @date 2015-9-15 下午4:14:39
 * @version 1.0
 */
public class LocNormalUnit extends DaoHelper<LocNormal> {

	public LocNormalUnit(Context context) {

		this(context, LocNormal.class);
	}

	private LocNormalUnit(Context context, Class<LocNormal> clazz) {
		super(context, clazz);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 获取未上传的记录，最大10条
	 * 
	 * @author liulei
	 * @date 2015-9-16
	 * @return List<LocNormal>
	 */
	public List<LocNormal> getUnuploadLocs() {

		List<LocNormal> list = null;

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
	public boolean updateUploadSuccess(final List<LocNormal> list) {

		boolean rsl = false;

		try {
			TransactionManager.callInTransaction(db.getConnectionSource(), new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					// TODO Auto-generated method stub

					for (LocNormal tmp : list) {
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
