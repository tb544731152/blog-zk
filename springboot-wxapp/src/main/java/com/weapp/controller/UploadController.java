package com.weapp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.apache.commons.fileupload.FileItem;  
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;  

import com.weapp.common.annotation.Api;
import com.weapp.common.constant.ApiConstant;
import com.weapp.entity.app.TUser;
import com.weapp.entity.auth.AppKey;
import com.weapp.entity.file.TFile;
import com.weapp.entity.result.CodeMsg;
import com.weapp.entity.result.Result;
import com.weapp.entity.vm.WxSendMsg;
import com.weapp.entity.wxarticle.Article;
import com.weapp.redis.RedisService;
import com.weapp.redis.SessionKey;
import com.weapp.repository.AppKeyRepository;
import com.weapp.repository.UserRepository;
import com.weapp.repository.WxArticleRepository;
import com.weapp.service.FileService;
import com.weapp.service.WxArticleService;
import com.weapp.utils.WxMessageUtils;


@RestController
public class UploadController extends BaseController{
	
	@Autowired
	private FileService fileService;
	/**
	 * 上传文件
	 * @param file
	 * @return
	 */
	@Api(name = ApiConstant.UPLOAD_IMAGE)
	@RequestMapping(value = "/api/file/upload/image", method = RequestMethod.POST, produces = "application/json")
	public Result<?> uploadImage(@RequestParam(required=true,value="file")MultipartFile file){
		if(null == file){
			return Result.msg(CodeMsg.UPLOAD_FILE_FAIL);
		}
		String random = RandomStringUtils.randomAlphabetic(64);
		try {
            TFile f = new TFile(file.getOriginalFilename(), file.getContentType(), file.getSize(),
                    new Binary(file.getBytes()));
            f.setImgId(random);
            fileService.save(f);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
		return Result.msg(CodeMsg.UPLOAD_FILE_SUCCESS.fillArgsToken(random));
	}
	
	/**
	 * 获取文件
	 * @param file
	 * @return
	 */
	@Api(name = ApiConstant.GET_IMAGE)
	@RequestMapping(value = "/api/file/get/image/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object>  uploadImage(HttpResponse response,@PathVariable String id)throws UnsupportedEncodingException {
		TFile file = fileService.getFile(id);
		if(file!=null){
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline; fileName=" + new String(file.getName().getBytes("utf-8"),"ISO-8859-1"))
					.header(HttpHeaders.CONTENT_TYPE,file.getContentType())
					.header(HttpHeaders.CONTENT_LENGTH, file.getSize() + "").header("Connection", "close")
					.body(file.getContent().getData());
		}
		return new ResponseEntity<>(
		          "404", 
		          HttpStatus.BAD_REQUEST);
	}
	
	
	/**
	 * 上传文件
	 * @param file
	 * @return
	 */
	@Api(name = ApiConstant.UPLOAD_IMAGE)
	@RequestMapping(value = "/api/file/upload/test", method = RequestMethod.GET, produces = "application/json")
	public Result<?> uploadImageTest(){
		String picPath ="D://2.png";  
        MultipartFile file = getMulFileByPath(picPath); 
        String random = RandomStringUtils.randomAlphabetic(64);
		try {
            TFile mf = new TFile(file.getOriginalFilename(), file.getContentType(), file.getSize(),
                    new Binary(file.getBytes()));
            mf.setImgId(random);
            fileService.save(mf);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
		return Result.msg(CodeMsg.UPLOAD_FILE_SUCCESS.fillArgsToken(random));
	}
	
	
	private static MultipartFile getMulFileByPath(String picPath) {  
        FileItem fileItem = createFileItem(picPath);  
        MultipartFile mfile = new CommonsMultipartFile(fileItem);  
        return mfile;  
    }  
  
    private static FileItem createFileItem(String filePath)  
    {  
        FileItemFactory factory = new DiskFileItemFactory(16, null);  
        String textFieldName = "textField";  
        int num = filePath.lastIndexOf(".");  
        String extFile = filePath.substring(num);  
        FileItem item = factory.createItem(textFieldName, "text/plain", true,  
            "MyFileName" + extFile);  
        File newfile = new File(filePath);  
        int bytesRead = 0;  
        byte[] buffer = new byte[8192];  
        try  
        {  
            FileInputStream fis = new FileInputStream(newfile);  
            OutputStream os = item.getOutputStream();  
            while ((bytesRead = fis.read(buffer, 0, 8192))  
                != -1)  
            {  
                os.write(buffer, 0, bytesRead);  
            }  
            os.close();  
            fis.close();  
        }  
        catch (IOException e)  
        {  
            e.printStackTrace();  
        }  
        return item;  
    }  
  
    @Autowired
	private RedisService redisService;
    
    @Autowired
	private UserRepository userRepository;
    
    @Autowired
	private WxArticleRepository articleRepository;
    @Autowired
	private AppKeyRepository appKeyRepository;
	@RequestMapping(value = "/api/file/{form_id}", method = RequestMethod.GET, produces = "application/json")
	public Result<?> sendMsg(@PathVariable String form_id){
		List<Article> list = articleRepository.findAll();
		List<AppKey> zyzsSides  = appKeyRepository.findAll();
		AppKey ak = zyzsSides.get(0);
		for(Article art : list){
			WxSendMsg msg = new WxSendMsg();
			msg.setOpenId(art.getOpenId());
			msg.setTitle("创意大赛");
			msg.setResult("中烟追溯-创意大赛文章审核【审核通过】");
			msg.setDate("2019-03-22 15:35:00");
			msg.setForm_id(form_id);
			WxMessageUtils.sendMsg(msg , ak);
		}
	    return Result.msg(CodeMsg.SUCCESS);
	}

}
