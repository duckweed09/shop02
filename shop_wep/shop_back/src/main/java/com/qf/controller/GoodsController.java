package com.qf.controller;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.qf.entity.Goods;
import com.qf.feign.GoodsFeign;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@Controller
@RequestMapping("/goodsManager")
public class GoodsController {

   @Autowired
    private GoodsFeign goodsFeign;

   @Autowired
   private FastFileStorageClient fastFileStorageClient;

    private String uploadPath = "D:\\imgs";

    /**
     * 获取商品列表
     * @return
     */
    @RequestMapping("/list")
    public String goodsList(Model model){
        //调用商品服务获得商品列表
        List<Goods> goods = goodsFeign.goodsList();
        System.out.println("调用商品服务，获得商品列表：" + goods);
        model.addAttribute("goodsList", goods);
        return "goodslist";
    }


    /**
     * 添加商品
     * @return
     */
    @RequestMapping("/insert")
    public String goodsInsert(Goods goods){

        System.out.println("添加商品：" + goods);
        //调用商品服务进行商品添加
        boolean flag = goodsFeign.goodsInsert(goods);
        System.out.println("返回结果：" + flag);

        return flag ? "redirect:http://localhost:16666/back/goodsManager/list" : "error";
    }


    /**
     * 图片上传
     * @return
     */
    @RequestMapping("/uploader")
    @ResponseBody
    public String imgUploader(MultipartFile file){



        File outfile = new File(uploadPath);
//        if(!outfile.exists()){
//            boolean mkdirs = outfile.mkdirs();
//            if(!mkdirs){
//                throw new RuntimeException("上传路径为空，并且无法创建！");
//            }
//        }

//        //处理文件名称
//        String filename = UUID.randomUUID().toString();
//        outfile = new File(outfile, filename);

        //开始上传（）fastdfs方式上传
        String uploadPath=null;
        try{
            StorePath storePath = fastFileStorageClient.uploadImageAndCrtThumbImage(
                    file.getInputStream(),
                    file.getSize(),
                    "JPG",
                    null);

            uploadPath = storePath.getFullPath();
        }catch (IOException e){
            e.printStackTrace();
        }





//        try (//原始上传方式
//                InputStream in = file.getInputStream();
//                OutputStream out = new FileOutputStream(outfile);
//        ) {
//            //文件的上传
//            IOUtils.copy(in,out);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        uploadPath="http://192.168.150.66:8080/"+uploadPath;
        System.out.println("上传路径"+uploadPath);

        return "{\"filename\":\"" + uploadPath + "\"}";
    }


    /**
     * 通过图片的名称进行图片的回显
     * @return
     */
    @RequestMapping("/showImg")
    public void showImage(String filename, HttpServletResponse response){

        //通过文件名获得图
        File imageFile = new File(uploadPath + "\\" + filename);

        try (
                InputStream in = new FileInputStream(imageFile);
                OutputStream out = response.getOutputStream();
        ){
            IOUtils.copy(in,out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
