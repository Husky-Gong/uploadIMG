package com.zx.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.zx.pojo.UploadRecord;
import com.zx.service.IUploadRecordService;
import com.zx.service.impl.UploadRecordServiceImpl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

@WebServlet("/record.do")
@MultipartConfig
public class RecordController extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5511837630177604201L;
	
	IUploadRecordService uploadRecordService = new UploadRecordServiceImpl();

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String service = req.getParameter("service");
		if("list".equals(service)) {
			list(req,resp);
		}else if("add".equals(service)){
			add(req,resp);
		}else if("download".equals(service)) {
			download(req,resp);
		}
	}
	
	private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//原文件名称
		String name = req.getParameter("name");
		Map<String,Object> param = new HashMap<String,Object>();
		if(StrUtil.isNotBlank(name)) {
			param.put("name",name);
			req.setAttribute("name",name);
		}
		List<UploadRecord> record = uploadRecordService.queryList(param);
		req.setAttribute("record",record);
		//内部转发到记录列表页面
		req.getRequestDispatcher("list.jsp").forward(req, resp);
	}
	
	private void add(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		//获取文件上传的文件数据
		Part part = req.getPart("file");
		//img的物理路径
		String realPath = req.getServletContext().getRealPath("img");
		//生成文件路径
		String  fileName = getFileName(part);
		// xxx.格式后缀 新的文件名称  用于 物理保存  也用于url 访问
		String newFileName = DateUtil.format(new Date(), "yyyyMMddHHmmssSSS")+getSuffix(fileName);
		//文件保存的路径
		String filePath = realPath + File.separator +  newFileName;
		//将文件进行保存
		part.write(filePath);
		//url 路径
		String  url = "/img/"+newFileName;
		uploadRecordService.add(fileName, url, filePath);
		//跳转到列表页面
		resp.sendRedirect("record.do?service=list");
		
	}
	
	private String  getFileName(Part part) {
		// 由于文件的原名称 在请求头中
		String header = part.getHeader("Content-Disposition");
		//form-data; name="file"; filename="2.jpeg"
		String[] info = header.split(" ");
		//从数组中获取原名称相关信息 :filename="2.jpeg"
		String name = info[2].trim();
		name = name.substring(10 , name.length() - 1);
		return name;
	}
	
	private   String getSuffix(String fileName) {
		int index = fileName.lastIndexOf(".");
		return fileName.substring(index);
	}
	
	private void download(HttpServletRequest req, HttpServletResponse resp) throws IOException {
			
		//在文件下载中,由于浏览器能够直接识别图片流数据,所以在类似文件下载中,需要通知浏览器  不要开发下载数据流
		resp.setContentType("multipart/form-data"); 
		//1.根据文件的物理路径读取文件
		String id = req.getParameter("id");
		UploadRecord record = uploadRecordService.queryOne(id);
		//文件保存要有名称 由于汉字在下载会乱码 所以需要进行编码
		String name = URLEncoder.encode(record.getName(),"UTF-8");
		resp.setHeader("Content-Disposition", "attachment;fileName="+name);  
				
		String path = record.getPath();
		//根据路径将文件转化为输入流
		FileInputStream fis = new FileInputStream(path);
		byte[] b = new byte[1024];
		int len = -1;
		//2.将文件使用输出流 输出到客户端  此时的输出流  要使用response 对象获取一个与浏览器关联的输出流对象
		ServletOutputStream out = resp.getOutputStream();
		while((len = fis.read(b)) != -1) {
			out.write(b, 0, len);
			out.flush();
		}
		out.close();
		fis.close();
	}
}
