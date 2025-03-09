package cn.edu.gxu.gxucpcsystem.controller.entity;

import cn.edu.gxu.gxucpcsystem.controller.Code;
import cn.edu.gxu.gxucpcsystem.dao.mysql.ContestDao;
import cn.edu.gxu.gxucpcsystem.domain.ContestRevised;
import cn.edu.gxu.gxucpcsystem.domain.Medal;
import cn.edu.gxu.gxucpcsystem.domain.RegisterForm;
import cn.edu.gxu.gxucpcsystem.domain.UserRegistration;
import cn.edu.gxu.gxucpcsystem.service.ContestRevisedService;
import cn.edu.gxu.gxucpcsystem.service.MedalService;
import cn.edu.gxu.gxucpcsystem.service.RegistrationFormService;
import cn.edu.gxu.gxucpcsystem.service.UserRegistrationService;
import cn.edu.gxu.gxucpcsystem.utils.LogsUtil;
import cn.edu.gxu.gxucpcsystem.utils.MD5Utils;
import cn.edu.gxu.gxucpcsystem.utils.Re;
import com.alibaba.fastjson.JSONException;
import com.mongodb.MongoCommandException;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Shape;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.AuthenticationFailedException;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;
import java.util.List;

@RestController
@RequestMapping({"/{pictureId}"})
@CrossOrigin
public class uploadpicture {

    @Autowired
    private MongoTemplate mongoTemplate;

    public class Picture{
        private String id;
        private String base64Image;
        public Picture(){

        }
        public String getId() {return id;}
        public void setId(String id) {this.id = id;}
        public String getBase64Image() {return base64Image;}
        public void setBase64Image(String base64Image) {this.base64Image = base64Image;}
    }

    @Value("${buffSize}")
    private long buffer;

    @Autowired
    MedalService medalService;

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;

    @PostMapping("/picture/upload")
    public Re upload(@RequestParam int pictureId, MultipartFile file) throws IOException {

        if (file.getSize() > MAX_IMAGE_SIZE) {
            return new Re(Code.STATUS_ERROR, null, "图片大小超过限制");
        }


        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        Picture picture = new Picture();
        picture.setBase64Image(base64Image);
        mongoTemplate.save(picture);

        return new Re(Code.STATUS_OK, picture, "上传成功");
    }

    @GetMapping("/picture/{id}")
    public Re askform(HttpServletRequest request, @PathVariable String pictureId) {
        Picture picture = null;
        try{
            picture = mongoTemplate.findById(pictureId, Picture.class);
        }catch (Exception e){
            return new Re(Code.DATABASE_ERROR, null, "数据库异常：" + e.getMessage());
        }
        if (picture == null ) return new Re(Code.RESOURCE_DISABLE, null, "该表单不存在或已被删除：" + pictureId);
        return new Re(Code.STATUS_OK, picture, "查询成功");
    }
}