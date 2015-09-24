package cn.linving.girls.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

public class DaoHelper<T> {
	protected final String TAG = this.getClass().getSimpleName();
	protected static SqliteOpenHelper db;
	protected Dao<T, Long> dao;

	@SuppressWarnings("unchecked")
	public DaoHelper(Context context, Class<T> clazz) {
		db = new SqliteOpenHelper(context);
		try {
			dao = (Dao<T, Long>) db.getDao(clazz);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Dao<T, Long> getDao() {
		return dao;
	}

	// ===================新增记录开始===================
	/** 新增一条记录 */
	public boolean create(T entity) {
		try {
			int status = dao.create(entity);
			if (status > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	/** 新增一条记录 */
	public boolean createOrUpdate(T entity) {
		try {
			CreateOrUpdateStatus couStatus = dao.createOrUpdate(entity);
			int status = couStatus.getNumLinesChanged();
			if (status > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** 新增多条记录 */
	public boolean create(List<T> entities) {
		for (T entity : entities) {
			create(entity);
		}
		return false;

	}

	/** 新增多条记录 */
	public boolean createOrUpdate(List<T> entities) {
		boolean isSave = false;
		for (T entity : entities) {
			isSave = createOrUpdate(entity);
		}
		return isSave;

	}

	// ===================新增记录结束===================

	// ===================删除记录开始===================
	/** 删除一条记录 */
	public boolean delete(T entity) {
		try {
			int status = dao.delete(entity);
			if (status > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** 删除多条记录 */
	public boolean delete(List<T> entities) {
		try {
			int status = dao.delete(entities);
			if (status > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** 删除所有记录 */
	public boolean deleteAll() {
		try {
			int status = dao.delete(queryForAll());
			if (status > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** 通过id删除一条记录 */
	public boolean deleteById(Long id) {
		try {
			int status = dao.deleteById(id);
			if (status > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	/** 通过多个id删除多条记录 */
	public boolean deleteIds(List<Long> ids) {
		try {
			int status = dao.deleteIds(ids);
			if (status > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	// ===================删除记录结束===================

	// ===================修改记录开始===================
	/** 修改一条记录 */
	public boolean update(T entity) {
		try {
			int status = dao.update(entity);
			if (status > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** 修改多条记录 */
	public boolean update(List<T> entities) {
		for (T entity : entities) {
			update(entity);
		}
		return false;

	}

	/** 通过id修改记录 */
	public boolean updateId(T entity, Long id) {
		try {
			int status = dao.updateId(entity, id);
			if (status > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	// ===================修改记录结束===================

	// ===================查询记录开始===================
	/** 查询所有记录 */
	public List<T> queryForAll() {
		List<T> all = null;
		try {
			all = dao.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null != all ? all : null;

	}

	/** id查询一条记录 */
	public T queryForId(Long id) {
		T entity = null;
		try {
			entity = dao.queryForId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null != entity ? entity : null;

	}

	/** 查询多条记录 */
	public List<T> queryForEq(String fieldName, String value) {
		List<T> entities = null;
		try {
			entities = dao.queryForEq(fieldName, value);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return entities;

	}

	public List<T> query(PreparedQuery<T> preparedQuery) {
		List<T> entities = null;
		try {
			entities = dao.query(preparedQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null != entities ? entities : null;

	}

	/** 分页查询记录 */
	public List<T> queryPaging(long page, long pageSize) {
		List<T> entities = null;
		try {
			entities = query(dao.queryBuilder().limit(pageSize).offset(page * pageSize).prepare());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null != entities ? entities : null;
	}

	public List<T> getQueryList(long start, long end, String content1, String content2, String content3, String flag) {
		List<T> list = new ArrayList<T>();
		try {
			if ("workoderfo".equalsIgnoreCase(flag)) {
				Where<T, Long> where = dao.queryBuilder().limit(end).offset(start * end).orderBy("wonum", false)
						.where().eq("assetnum", content1).and().eq("wonum", content2).and().eq("udparent_f", content3);
				list = dao.query(where.prepare());
			}

			return list;
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	public List<T> getQueryList(long start, long end, String content1, String content2, String flag) {
		List<T> list = new ArrayList<T>();
		try {

			if ("fospec".equalsIgnoreCase(flag)) {
				Where<T, Long> where = dao.queryBuilder().orderBy("wonum", false).where().eq("wonum", content1).and()
						.eq("parentwonum", content2);
				list = dao.query(where.prepare());

			} else if ("workoderfo".equalsIgnoreCase(flag)) {
				Where<T, Long> where = dao.queryBuilder().limit(end).offset(start * end).orderBy("wonum", false)
						.where().eq("assetnum", content1).and().eq("udparent_f", content2);
				list = dao.query(where.prepare());
			}

			return list;
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	public List<T> getQueryList(long start, long end, String content, String flag) {
		List<T> list = new ArrayList<T>();
		try {
			if ("asset".equalsIgnoreCase(flag)) {
				Where<T, Long> where = dao.queryBuilder().limit(end).offset(start * end).orderBy("assetnum", true)
						.where().eq("location", content);
				list = dao.query(where.prepare());
			} else if ("asset_orderbyall".equalsIgnoreCase(flag)) {
				QueryBuilder<T, Long> where = dao.queryBuilder().limit(end).offset(start * end)
						.orderBy("assetnum", true);
				list = dao.query(where.prepare());
			} else if ("asset_like".equalsIgnoreCase(flag)) {
				Where<T, Long> where = dao.queryBuilder().limit(end).offset(start * end).orderBy("assetnum", true)
						.where().like("location", content + "%");
				list = dao.query(where.prepare());
			} else if ("failureclass".equalsIgnoreCase(flag)) {
				QueryBuilder<T, Long> where = dao.queryBuilder().limit(end).offset(start * end)
						.orderBy("failurecode", true);
				list = dao.query(where.prepare());
			} else if ("sfailureclass".equalsIgnoreCase(flag)) {
				Where<T, Long> where = dao.queryBuilder().limit(end).offset(start * end).orderBy("failurecode", true)
						.where().eq("actype", content).or().eq("actype", "");
				list = dao.query(where.prepare());
			} else if ("workordermo".equalsIgnoreCase(flag)) {
				QueryBuilder<T, Long> where = dao.queryBuilder().limit(end).offset(start * end)
						.orderBy("workorderid", true);
				list = dao.query(where.prepare());
			} else if ("assetsepc".equalsIgnoreCase(flag)) {
				Where<T, Long> where = dao.queryBuilder().limit(end).offset(start * end).orderBy("assetnum", true)
						.where().eq("assetnum", content);
				list = dao.query(where.prepare());
			} else if ("assetscan".equalsIgnoreCase(flag)) {
				Where<T, Long> where = dao.queryBuilder().limit(end).offset(start * end).orderBy("assetnum", true)
						.where().eq("assetnum", content);
				list = dao.query(where.prepare());
			} else if ("tasklocation".equalsIgnoreCase(flag)) {
				Where<T, Long> where = dao.queryBuilder().orderBy("location", true).where().eq("wonum", content);
				list = dao.query(where.prepare());
			} else if ("workoderfo".equalsIgnoreCase(flag)) {
				Where<T, Long> where = dao.queryBuilder().limit(end).offset(start * end).orderBy("wonum", false)
						.where().eq("assetnum", content);
				list = dao.query(where.prepare());
			} else if ("fospec".equalsIgnoreCase(flag)) {
				Where<T, Long> where = dao.queryBuilder().orderBy("wonum", false).where().eq("wonum", content);
				list = dao.query(where.prepare());
			} else if ("workorderfail".equalsIgnoreCase(flag)) {
				QueryBuilder<T, Long> where = dao.queryBuilder().limit(end).offset(start * end).orderBy("wonum", false);
				list = dao.query(where.prepare());
			} else if ("location_orderby".equalsIgnoreCase(flag)) {
				QueryBuilder<T, Long> where = dao.queryBuilder().orderBy("location", true);
				list = dao.query(where.prepare());
			} else if ("workfoup".equalsIgnoreCase(flag)) {
				Where<T, Long> where = dao.queryBuilder().where().eq("udparent_f", content);
				list = dao.query(where.prepare());
			} else if ("fospecup".equalsIgnoreCase(flag)) {
				Where<T, Long> where = dao.queryBuilder().where().eq("parentwonum", content);
				list = dao.query(where.prepare());
			}

			return list;
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	// ===================查询记录结束===================

	// =================自定义SQL执行语句开始=================

	public boolean executeRawNoArgs(String sql) {
		int rawResult;
		try {
			rawResult = dao.executeRawNoArgs(sql);
			if (rawResult > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean executeRaw(String sql, String... str) {
		int rawResult;
		try {
			rawResult = dao.executeRaw(sql, str);
			if (rawResult > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// =================自定义SQL执行语句结束=================
}
