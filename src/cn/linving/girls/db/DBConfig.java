package cn.linving.girls.db;


public class DBConfig {
	/** 数据库设置 **/
	public static final class DATABASE {
		public static final int VERSION = 4;// 版本

		/*
		 * public static final String PATH = android.os.Environment
		 * .getExternalStorageDirectory().getAbsolutePath() +
		 * "/data/data/com.sl/";// 路径
		 */
		// public static final String PATH = "/data/data/cn.linving.girls/";
		public static final String PATH = "DB.db";
	}

	/*
	 * public static boolean isDBFileExist() { String path =
	 * DBConfig.DATABASE.PATH + "databases" + File.separator; String DB_NAME =
	 * path + "DB.db"; File file = new File(DB_NAME); long length =
	 * file.length(); if (file.exists()) { if (length < 10240) { file.delete();
	 * return false; } return true; } else { return false; } }
	 * 
	 * public static String getFilePath() { String path = DBConfig.DATABASE.PATH
	 * + "databases" + File.separator; String DB_NAME = path + "DB.db"; File dir
	 * = new File(path); if (!dir.exists()) { dir.mkdirs(); } return DB_NAME; }
	 */
}
