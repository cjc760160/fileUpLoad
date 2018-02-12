package com.cjc.servlet;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * @author cjc
 * @date 2018-02-07
 */

public class DownLoadServlet extends HttpServlet {
    private Logger logger = Logger.getLogger(DownLoadServlet.class);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileName = request.getParameter("filename");
        fileName = new String(fileName.getBytes("ISO8859-1"), "UTF-8");
        //上传文件保存的目录/WEB-INF/upload目录下的子目录中
        String fileSaveRootPath = this.getServletContext().getRealPath("/WEB-INF/upload");
        //通过文件名和保存文件的目录找出文件所在的目录
        String path = findFileSavePathByFileName(fileName, fileSaveRootPath);
        //得到要下载的文件
        File file = new File(path + "\\" + fileName);
        //判断文件是否存在
        if (!file.exists()) {
            request.setAttribute("message", "文件不存在");
            request.getRequestDispatcher("/message.jsp").forward(request, response);
            return;
        }
         //处理文件名
        String realFileName = fileName.substring(fileName.indexOf("_") + 1);
        //设置响应头，控制浏览器下载该文件
        response.setHeader("content-disposition", "attachment; fileName=" + URLEncoder.encode(realFileName, "UTF-8"));
        //读取要下载的文件，保存到文件输入流
        FileInputStream in = new FileInputStream(path + "\\" + fileName);
        //创建输出流
        OutputStream out = response.getOutputStream();
        //创建缓冲区
        byte[] buffer = new byte[1024];
        int len;
        //循环将输入流中的内容读取到缓冲流区当中
        while ((len = in.read(buffer)) > 0) {
            //输出缓冲区的内容到浏览器，实现文件下载
            out.write(buffer, 0, len);
        }
        in.close();
        out.close();

    }

    /**
     * @Method: findFileSavePathByFileName
     * @Description: 通过文件名和存储上传文件根目录找出要下载的文件的路径
     * @param fileName
     * @param saveRootPath
     * @return 要下载的文件的存储目录
     */
    private String findFileSavePathByFileName(String fileName, String saveRootPath) {
        int hashcode = fileName.hashCode();
        int dir1 = hashcode & 0xf;
        int dir2 = (hashcode & 0xf0) >> 4;
        String dir = saveRootPath + "\\" + dir1 + "\\" + dir2;
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return dir;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
