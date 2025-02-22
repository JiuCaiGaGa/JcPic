package com.jcgg.jcpic_backend.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.jcgg.jcpic_backend.common.ResultUtils;
import com.jcgg.jcpic_backend.config.CosClientConfig;
import com.jcgg.jcpic_backend.exception.ErrorCode;
import com.jcgg.jcpic_backend.exception.ThrowUtils;
import com.jcgg.jcpic_backend.model.dto.file.UploadPictureResult;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class FileManager {

    @Resource
    private CosClientConfig cosClientConfig;
    @Resource
    private COSClient cosClient;

    @Resource
    private CosManager cosManager;

    /**
     * 通用上传照片文件
     *
     * @param multipartFile 上传文件
     * @param uploadPrefix  路径前缀
     */
    public UploadPictureResult uploadPicture(MultipartFile multipartFile, String uploadPrefix) {

        // 图片校验
        validPic(multipartFile);

        // 图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originalFilename = multipartFile.getOriginalFilename();
        String uploadFilename = String.format("%s_%s.%s",
                DateUtil.formatDate(new Date()),
                uuid,
                FileUtil.getSuffix(originalFilename)
        );
        String uploadPath = String.format("/%s/%s", uploadPrefix, uploadFilename);

        File tempFile = null;
        try {
            // 创建空的临时文件
            tempFile = File.createTempFile(uploadPath, null);
            // 写入 临时文件
            multipartFile.transferTo(tempFile);
            // 上传文件到存储
            PutObjectResult uploadResult = cosManager.putPictureObject(uploadPath, tempFile);
            // 获取图片信息对象
            ImageInfo imageInfo = uploadResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            UploadPictureResult uploadPictureResult = new UploadPictureResult();

            // 宽高、宽高比
            int picWidth = imageInfo.getWidth();
            int picHeight = imageInfo.getHeight();
            double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();

            // 封装返回结果
            uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
            uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
            uploadPictureResult.setPicSize(FileUtil.size(tempFile));
            uploadPictureResult.setPicWidth(picWidth);
            uploadPictureResult.setPicHeight(picHeight);
            uploadPictureResult.setPicScale(picScale);
            uploadPictureResult.setPicFormat(imageInfo.getFormat());

            return uploadPictureResult;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("图像上传到存储失败", e);
            throw new RuntimeException(e);
        } finally {
            deleteTempFile(tempFile);
        }
    }


    /**
     * 校验图片
     *
     * @param multipartFile
     */
    private void validPic(MultipartFile multipartFile) {
        ThrowUtils.throwIf(
                multipartFile == null,
                ErrorCode.PARAMS_ERROR, "未发现上传图片文件"
        );
        // 校验文件大小
        long fileSize = multipartFile.getSize();
        final long One_M = 1024 * 1024;

        ThrowUtils.throwIf(
                fileSize > 2 * One_M,
                ErrorCode.OPERATION_ERROR,
                "上传图片不得大于2M"
        );

        // 校验文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "png", "jpg", "webp");

        ThrowUtils.throwIf(
                !ALLOW_FORMAT_LIST.contains(fileSuffix),
                ErrorCode.PARAMS_ERROR, "文件类型不允许"
        );

    }

    /**
     * 清理临时文件
     *
     * @param tempFile
     */
    public static void deleteTempFile(File tempFile) {
        if (tempFile != null) {
            boolean deleteFlag = tempFile.delete();
            if (!deleteFlag)
                log.error("Deleted tempFile failed, filePath : {}", tempFile.getAbsolutePath());
        }
    }

}
