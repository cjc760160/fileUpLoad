package com.cjc.servlet;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cjc
 * @date 2018-02-05
 */

public class ListFileServlet extends HttpServlet {
    private Logger logger = Logger.getLogger(ListFileServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取上传文件的目录
        String uploadFilePath = this.getServletContext().getRealPath("/WEB-INF/upload");
        //存储要下载的文件名
        Map<String, String> fileNameMap = new HashMap<>();
        //递归遍历filePath目录下的文件和目录，将所有的问价名存储到map集合中
        //File既可以代表一个文件也可以代表一个目录
        listFile(new File(uploadFilePath), fileNameMap);
        //将Map集合发送到listFile.jsp页面进行显示
        request.setAttribute("fileNameMap", fileNameMap);
        request.getRequestDispatcher("/index.jsp").forward(request,response);
    }

    /**
     * @param file 代表一个问价，也代表一个文件目录
     * @param map  存储文件名的Map集合
     * @Method listFile
     * @Description: 递归遍历指定目录下的所有文件
     */
    public void listFile(File file, Map<String, String> map) {
        //如果file代表的不是一个文件，而是一个目录
        if (!file.isFile()) {
            File[] files = file.listFiles();
            //遍历files数组
            if (files != null) {
                for (File f : files) {
                    //递归
                    listFile(f, map);
                }
            }
        } else {
            /**
             * 处理文件名，上传后的文件是以uuid_文件名的形式重新命名的，去除文件名的uuid部分
             * file.getName().indexOf("_")检索字符串中第一次出现“——”字符的位置
             */
            String realName = file.getName().substring(file.getName().indexOf("_") + 1);
            map.put(file.getName(), realName);
        }

    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
