package com.zx.dao;

import java.util.List;
import java.util.Map;

import com.zx.pojo.UploadRecord;

public interface IUploadRecordDao {
	public List<UploadRecord> selectList(Map<String,Object> param);
	public boolean insertRecord(String name, String url, String path);
	public UploadRecord selectOne(String id);
}
