package com.zx.service.impl;

import java.util.List;
import java.util.Map;

import com.zx.dao.IUploadRecordDao;
import com.zx.dao.impl.UploadRecordDaoImpl;
import com.zx.pojo.UploadRecord;
import com.zx.service.IUploadRecordService;

public class UploadRecordServiceImpl implements IUploadRecordService{
	IUploadRecordDao uploadRecordDao = new UploadRecordDaoImpl();

	@Override
	public List<UploadRecord> queryList(Map<String, Object> param) {
		return uploadRecordDao.selectList(param);
	}

	@Override
	public boolean add(String name, String url, String path) {
		return uploadRecordDao.insertRecord(name,url,path);
	}

	@Override
	public UploadRecord queryOne(String id) {
		return uploadRecordDao.selectOne(id);
	}
	
}
