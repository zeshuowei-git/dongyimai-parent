package com.offcn.content.controller;

import com.offcn.entity.Result;
import com.offcn.utils.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * 文件上传
 * @author ：yz
 * @date ：Created in 2020/8/28 15:34
 * @version: 1.0
 */
@RestController
public class UploadController {


    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;


    @RequestMapping("/upload")
    public Result upload(MultipartFile file){

        //使用工具类型操作
        try {
            FastDFSClient fastDFSClient=new FastDFSClient("classpath:conf/fdfs_client.conf");

            //获取文件名称扩展名 10.jpg
            String filename = file.getOriginalFilename();
            int index = filename.indexOf(".");
            String extName = filename.substring(index + 1);

            //获取上传流
            byte[] bytes = file.getBytes();


            //文件上传
            String filePath = fastDFSClient.uploadFile(bytes, extName);

            String url=FILE_SERVER_URL+filePath;



           return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }


    }


}
