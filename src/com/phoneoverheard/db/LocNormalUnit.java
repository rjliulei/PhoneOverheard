package com.phoneoverheard.db;

import android.content.Context;

import com.phoneoverheard.bean.LocNormal;

import cn.linving.girls.db.DaoHelper;

/** 
 * 定位数据操作类
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

}
