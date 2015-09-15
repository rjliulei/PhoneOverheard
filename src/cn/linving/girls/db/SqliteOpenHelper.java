package cn.linving.girls.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import cn.linving.girls.bean.RowImage;
import cn.linving.girls.tools.MyLog;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.phoneoverheard.bean.LocNormal;

public class SqliteOpenHelper extends OrmLiteSqliteOpenHelper {
	private final String TAG = this.getClass().getSimpleName();
	private static SqliteOpenHelper instance;

	/* 在此声明实体类 */
	private Class<?>[] classes = { RowImage.class, LocNormal.class };

	public SqliteOpenHelper(Context context) {
		super(context, DBConfig.getFilePath(), null, DBConfig.DATABASE.VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database,
			ConnectionSource connectionSource) {
		try {
			for (Class<?> clazz : classes) {
				TableUtils.createTableIfNotExists(connectionSource, clazz);
			}
		} catch (SQLException e) {
			MyLog.i(TAG, " 建立数据库失败！" + e.toString());
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase database,
			ConnectionSource connectionSource, int oldVer, int newVer) {
		try {
			for (Class<?> clazz : classes) {
				TableUtils.dropTable(connectionSource, clazz, true);
			}
			onCreate(database, connectionSource);
		} catch (SQLException e) {
			MyLog.i(TAG, "Unable to upgrade database from version " + oldVer
					+ " to new " + newVer);
		}
	}

	// /**
	// * 单例获取该Helper
	// *
	// * @param context
	// * @return
	// */
	// public static synchronized SqliteOpenHelper getHelper(Context context) {
	// context = context.getApplicationContext();
	// if (instance == null) {
	// synchronized (SqliteOpenHelper.class) {
	// if (instance == null)
	// instance = new SqliteOpenHelper(context);
	// }
	// }
	//
	// return instance;
	// }
}
