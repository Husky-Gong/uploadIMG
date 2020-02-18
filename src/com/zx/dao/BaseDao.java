package com.zx.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import com.zx.util.JDBCUtil;

public class BaseDao {
	public  int insert(String sql,Object... param) {
		Connection conn = JDBCUtil.getConn();
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			//获取指令对象，且支持获取数据库生成主键值
			prep = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			//设置参数
			//设置参数 根据循环设置参数
			for (int i = 0; i < param.length; i++) {
				//PreparedStatement 设置参数是 第一个? 下标为  1 第二个是2
				prep.setObject(i+1, param[i]);
			}
			prep.executeUpdate();
			//获取生成主键值
			rs = prep.getGeneratedKeys();
			//偏移结果集的指针
			rs.next();
			return rs.getInt(1);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(conn, prep, rs);
		}
		return 0;
	}
	
	
	

	public  boolean update(String sql,Object... param) {
		int m = 0;
		Connection conn = JDBCUtil.getConn();
		PreparedStatement prep = null;
		try {
			prep = conn.prepareStatement(sql);
			//设置参数 根据循环设置参数
			for (int i = 0; i < param.length; i++) {
				//PreparedStatement 设置参数是 第一个? 下标为  1 第二个是2
				prep.setObject(i+1, param[i]);
			}
			m = prep.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally {
			JDBCUtil.close(conn, prep);
		}
		return m > 0?true:false;
	}
	
	
	

	public  <T> List<T>  selectList(String sql,Class<T> cls,Object... param) {
		Connection conn = JDBCUtil.getConn();
		List<T> list = new ArrayList<T>();
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			prep = conn.prepareStatement(sql);
			//设置查询参数
			for (int i = 0; i < param.length; i++) {
				prep.setObject(i+1, param[i]);
			}
			//获取查询结果
			rs = prep.executeQuery();
			//获取查询结果的元信息
			ResultSetMetaData metaData = rs.getMetaData();
			//获取列的个数
			int columnCount = metaData.getColumnCount();
			while(rs.next()) {
				//创建对象
				T t = cls.getConstructor().newInstance();
				//循环获取列的值  且为对象赋值
				for (int i = 1; i <= columnCount; i++) {
					//获取列的别名
					String columnLabel = metaData.getColumnLabel(i);
					//根据columnLabel获取其对应的值
					Object object = rs.getObject(columnLabel);
					//为对象赋值  为对象的属性赋值  
					//1.根据列的别名找到类中的属性
					Field field = cls.getDeclaredField(columnLabel);
					//2.为该属性赋值
					field.setAccessible(true);
					field.set(t, object);
					field.setAccessible(false);
				}
				list.add(t);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(conn, prep, rs);
		}
		return list;
	}
	


	public  <T> T selectOne(String sql,Class<T> cls,Object... param) {
		List<T> list = selectList(sql, cls, param);
		if(list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}


	public  <T> List<T> selectPage(String sql,Class<T> cls,Integer pageNo,Integer pageSize,Object... param){
		/*
		 	如果我们一共7条数据   且 每页2条    4 页    pageNo值为  5    传进来的pageNo  不能大于最大页码值
		 	如果16条数据  每页2条 一共多少页
		 	
		 	一共多少页 ?
		 	最大的页码值：
		 	
		 	计算最大页码值 ----> 获取符合条件的行数
		 */
		//获取总行数
		int total = selectCount(sql, param);
		//计算最大页码 总页数  若此时 total 的值为  0  则 totalPage值为0 
		int totalPage =  total % pageSize == 0?total/pageSize:total/pageSize+1;
		//pageNo   页码的范围:  1  -----  totalPage
		//如果页码比最大页码大，则默认为最大页码  若totalPage 为0   那么pageNo 一定大于  0   则最终 pageNo = 0 pageNo 最小应该为 1
		if (pageNo > totalPage) {
			pageNo = totalPage;
		}
		// 如果页码比1小  则 默认为  1
		if(pageNo < 1) {
			pageNo = 1;
		}
		//计算分页的偏移量
		int offset = (pageNo - 1) * pageSize;
		StringBuffer sb = new StringBuffer(sql);
		sb.append("  limit ");
		sb.append(offset);
		sb.append(",");
		sb.append(pageSize);
		sql = sb.toString();
		System.out.println("sql为:"+sql);
		
		List<T> list = selectList(sql,cls,param);
		
		return list;
	}
	
	private  int selectCount(String sql,Object...param) {
		StringBuffer sb = new StringBuffer("select count(1) from (");
		sb.append(sql);
		sb.append(") rs");
		sql = sb.toString();
		PreparedStatement prep = null;
		Connection conn = JDBCUtil.getConn();
		ResultSet rs = null;
		try {
			prep = conn.prepareStatement(sql);
			//设置查询参数
			for (int i = 0; i < param.length; i++) {
				prep.setObject(i+1, param[i]);
			}
			rs = prep.executeQuery();
			rs.next();
			//获取符合条件总行数
			return rs.getInt(1);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(conn, prep, rs);
		}
		return 0;
	}
}
