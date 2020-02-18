package com.zx.dao.impl;

import java.util.List;
import java.util.Map;

import com.zx.dao.BaseDao;
import com.zx.dao.IUploadRecordDao;
import com.zx.pojo.UploadRecord;

public class UploadRecordDaoImpl extends BaseDao implements IUploadRecordDao{

	@Override
	public List<UploadRecord> selectList(Map<String, Object> param) {
		String sql = "select id,name,url,path from upload_record where 1=1 ";
		if(param.containsKey("name")) {
			sql = sql + " and name like '%"+param.get("name")+"%'";
		}
		return super.selectList(sql,UploadRecord.class);
	}

	@Override
	public boolean insertRecord(String name, String url, String path) {
		String sql = "insert into upload_record value(0,?,?,?)";
		return super.update(sql, name,url,path);
	}

	@Override
	public UploadRecord selectOne(String id) {
		String sql = "select id,name,url,path from upload_record where id=? ";
		return super.selectOne(sql,UploadRecord.class,id);
	}

}
