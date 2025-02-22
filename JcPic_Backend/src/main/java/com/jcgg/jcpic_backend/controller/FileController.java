package com.jcgg.jcpic_backend.controller;

import com.jcgg.jcpic_backend.annotation.AuthCheck;
import com.jcgg.jcpic_backend.common.BaseResponse;
import com.jcgg.jcpic_backend.common.ResultUtils;
import com.jcgg.jcpic_backend.constant.UserConstant;
import com.jcgg.jcpic_backend.exception.ErrorCode;
import com.jcgg.jcpic_backend.exception.ThrowUtils;
import com.jcgg.jcpic_backend.manager.CosManager;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

@Slf4j
@RestController // 文件类
@RequestMapping("/file")
public class FileController {
    @Resource
    private CosManager cosManager;

    /**
     * 测试文件上传
     * @param multipartFile 上传文件
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/test/upload")
    public BaseResponse<String> testUploadFile(@RequestPart("file") MultipartFile multipartFile) {
        ThrowUtils.throwIf(
                multipartFile == null,
                ErrorCode.OPERATION_ERROR
        );
        String fileName = multipartFile.getOriginalFilename();
        String filePath = String.format("/test/%s", fileName);
        File tempFile = null;
        try {
            // 创建空的临时文件
            tempFile = File.createTempFile(filePath, null);
            // 写入 临时文件
            multipartFile.transferTo(tempFile);
            // 上传文件到存储
            cosManager.putObject(filePath, tempFile);
            return ResultUtils.success(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("file upload failed. filePath : {}", filePath, e);
            throw new RuntimeException(e);
        } finally {
            if (tempFile != null) {
                boolean deleteFlag = tempFile.delete();
                if (!deleteFlag)
                    log.error("Deleted tempFile failed, filePath : {}", filePath);
            }
        }
    }

    /**
     * @param filePath 文件路径
     * @param resp     响应
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/test/download")
    public void testFileDownload(String filePath, HttpServletResponse resp) {
        COSObjectInputStream cosInput = null;
        try {
            COSObject cosObject = cosManager.getObject(filePath);
            cosInput = cosObject.getObjectContent();

            // 获取文件名

            String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
            // 设置响应头
            resp.setContentType("application/octet-stream;charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=\""
                    +  URLEncoder.encode(fileName,"utf8")+"\"");
            // 转换成数组
            byte[] byteArray = IOUtils.toByteArray(cosInput);

            // 写入响应
            resp.getOutputStream().write(byteArray);
            resp.getOutputStream().flush();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (cosInput != null)
                    cosInput.close();
            } catch (Exception e) {
                log.error("CosInputStream close error, ",e);
            }
        }
    }

/*

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/test/download/")
    public void testDownloadFile(String filepath, HttpServletResponse response) throws IOException {
        COSObjectInputStream cosObjectInput = null;
        try {
            COSObject cosObject = cosManager.getObject(filepath);
            cosObjectInput = cosObject.getObjectContent();
            // 处理下载到的流
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);

            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }
*/



}
