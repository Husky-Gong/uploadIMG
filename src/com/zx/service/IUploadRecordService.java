package com.zx.service;

import java.util.List;
import java.util.Map;

import com.zx.pojo.UploadRecord;

public interface IUploadRecordService {
	public List<UploadRecord> queryList(Map<String, Object> param);
	public boolean add(String name, String url,String path);
	public UploadRecord queryOne(String id);
}
