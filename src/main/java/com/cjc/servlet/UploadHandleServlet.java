package com.cjc.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.UUID;

/**
 * @author cjc
 * @date 2018-01-30
 */

public class UploadHandleServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(UploadHandleServlet.class);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //得到上传保存目录，将上传文件放在WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
        String savePath = this.getServletContext().getRealPath("/WEB-INF/upload");
        File file = new File(savePath);
        if (!file.exists() && !file.isDirectory()) {
            logger.info(savePath + "该目录不存在，需要创建");
            file.mkdir();
        }
        //文件上传时生成的临时目录
        String tmpPath = this.getServletContext().getRealPath("/WEB-INF/temp");
        File tempFile = new File(tmpPath);
        if (!tempFile.exists()) {
            tempFile.mkdir();
        }
        String message = "";
        //使用apache文件上传组件处理文件上传步骤
        try {
            //1、创建一个DiskFileItemFacotory工厂
            DiskFileItemFactory factory = new DiskFileItemFactory();
            //设置工厂缓冲区的大小,当上传问价超过缓冲区的大小时，就会生成一个临时文件存放到指定的临时目录中
            factory.setSizeThreshold(1024 * 100);
            //设置上传时生成的临时文件的保存目录
            factory.setRepository(tempFile);

            //2、创建一个文件解析器
            ServletFileUpload upload = new ServletFileUpload(factory);
            //监听文件的上传速度
            upload.setProgressListener(new ProgressListener() {
                @Override
                public void update(long pBytesRead, long pContentLength, int arg2) {
                    System.out.println("文件大小为：" + pContentLength + ",当前已处理：" + pBytesRead);
                }
            });
            //解决文件明的中文乱码问题
            upload.setHeaderEncoding("utf-8");
            //3、判断提交上来的数据是否是上传表单的数据
            if (!ServletFileUpload.isMultipartContent(request)) {
                return;
            }
            //设置的上传单个文件的最大值
            upload.setFileSizeMax(1024 * 1024);
            //设置上传文件总量的最大值，最大值=多个同时上传文件的值的总和
            upload.setSizeMax(1024 * 1024 * 10);

            //4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
            List<FileItem> list = upload.parseRequest(request);
            for (FileItem item : list) {
                //如果fileitem中封装的是普通输入项的数据
                if (item.isFormField()) {
                    String name = item.getFieldName();
                    //解决普通输入项的中文乱码问题
                    String value = item.getString("utf-8");
                    System.out.println(name + "=" + value);
                } else { //如果fileName里面封装的上传文件
                    String fileName = item.getName();
                    System.out.println(fileName);
                    if (fileName == null || "".equals(fileName.trim())) {
                        continue;
                    }
                    //注意点：不同的浏览器提交的文件名是不一样的，有些浏览器提交的文件名是带有路径的，如：c:\a\b\1.txt，而有些只是单纯的文件名，如1.txt
                    //处理获取到的上传文件的文件名的路径部分，只保留文件名部分
                    fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
                    //获取上传文件的扩展名
                    String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);
                    logger.debug("文件的扩展名是：" + fileExtName);
                    //获取item中上传文件的输入流
                    InputStream inputStream = item.getInputStream();
                    //得到文件的保存名称
                    String saveFileName = makeFileName(fileName);
                    //得到文件的保存目录
                    String realSavePath = makePath(saveFileName, savePath);
                    //创建一个文件输出流
                    FileOutputStream fileOutputStream = new FileOutputStream(realSavePath + "\\" + saveFileName);
                    //创建一个缓冲区
                    byte[] buffer = new byte[1024];
                    //判断输入流中的数据是否已经读完的标志
                    int len;
                    //循环将输入流度入到缓冲区中，（len = in.read(buffer)) > 0)就表示in里面还有数据
                    while ((len = inputStream.read(buffer)) > 0) {
                        //使用FileOuputStream输出流将缓冲区的数据写入到指定的目录（savePath + "\\" + fileName)中
                        fileOutputStream.write(buffer, 0, len);
                    }
                    //关闭输入流
                    inputStream.close();
                    //关闭输出流
                    fileOutputStream.close();
                    //处理文件上传时生成的临时文件
                    item.delete();
                    message = "文件上传成功";
                }
            }
        } catch (FileUploadBase.FileSizeLimitExceededException e) {
            e.printStackTrace();
            request.setAttribute("message", "单个文件超出最大值");
            request.getRequestDispatcher("/message.jsp").forward(request, response);
            return;
        } catch (FileUploadBase.SizeLimitExceededException e) {
            e.printStackTrace();
            request.setAttribute("message", "上传文件总大小超出限制的最大值！！！");
            request.getRequestDispatcher("/message.jsp").forward(request, response);
            return;
        } catch (Exception e) {
            message = "文件上传失败";
            e.printStackTrace();
        }
        request.setAttribute("message", message);
        request.getRequestDispatcher("/message.jsp").forward(request, response);
    }

    /**
     * @param fileName
     * @return 唯一的文件名称
     * @Method makeFileName
     * @Description 创建一个唯一的文件名称
     */
    private String makeFileName(String fileName) {
        //为防止文件覆盖现象的发生，要为上传文件产生一个唯一的文件名称
        return UUID.randomUUID().toString() + "_" + fileName;
    }

    /**
     * @param fileName 文件名，根据文件名生成存储目录
     * @param savePath 文件存储路径
     * @return 新的存储目录
     * @Method makePath
     * @Description 为了防止一个目录下面出现太多文件，要使用hash算法打散存储
     */
    private String makePath(String fileName, String savePath) {
        //得到文件名的hashcode值，得到的就是fileName的这个字符串对象在内存中的地址
        int hashcode = fileName.hashCode();
        int dir1 = hashcode & 0xf;
        int dir2 = (hashcode & 0xf0) >> 4;
        String dir = savePath + "\\" + dir1 + "\\" + dir2;
        //File既可以代表文件也可以表示目录
        File file = new File(dir);
        if (!file.exists()) {
            //创建目录
            file.mkdirs();
        }
        return dir;
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
