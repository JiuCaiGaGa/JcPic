package com.jcgg.jcpic_backend.manager.upload;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.jcgg.jcpic_backend.config.CosClientConfig;
import com.jcgg.jcpic_backend.manager.CosManager;
import com.jcgg.jcpic_backend.model.dto.file.UploadPictureResult;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.ProcessResults;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 图片上传模板
 */
@Slf4j
public abstract class PictureUploadTemplate {

    @Resource
    private CosClientConfig cosClientConfig;
    @Resource
    private COSClient cosClient;

    @Resource
    private CosManager cosManager;
    private Set<String> typeSet = new HashSet<>(Arrays.asList("jpeg", "png", "jpg", "webp"));

    /**
     * 通用上传照片文件
     *
     * @param inputSource 上传文件
     * @param uploadPrefix  路径前缀
     */
    public UploadPictureResult uploadPicture(Object inputSource, String uploadPrefix) {

        // 图片校验
        validPic(inputSource);

        // 图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originalFilename = getOriginFilename(inputSource);
        String endName = FileUtil.getSuffix(originalFilename);
        if(StrUtil.isBlank(endName) || !typeSet.contains(endName)){
            endName = "jpeg";
        }
        String uploadFilename = String.format("%s_%s.%s",
                DateUtil.formatDate(new Date()),
                uuid, endName);
        String uploadPath = String.format("/%s/%s", uploadPrefix, uploadFilename);

        File tempFile = null;
        try {
            // 创建空的临时文件
            tempFile = File.createTempFile(uploadPath, null);
            // 处理文件来源
            processFile(inputSource,tempFile);
            // 上传文件到存储
            PutObjectResult uploadResult = cosManager.putPictureObject(uploadPath, tempFile);
            // 获取图片信息对象
            ImageInfo imageInfo = uploadResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            UploadPictureResult uploadPictureResult = buildResult(originalFilename, tempFile, uploadPath,  imageInfo);

            // 获取图片处理结果
            ProcessResults processResults = uploadResult.getCiUploadResult().getProcessResults();
            List<CIObject> objectList = processResults.getObjectList();
            if(CollUtil.isNotEmpty(objectList)){
                CIObject compressedCiObject = objectList.get(0);
                CIObject thumbnailCiObject = compressedCiObject;
                if(objectList.size() > 1){
                    thumbnailCiObject = objectList.get(1);
                }
                // 封裝压缩图片的返回結果
                return buildResult(originalFilename, compressedCiObject,thumbnailCiObject);
            }
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
     * 获取文件名
     */
    protected abstract String getOriginFilename(Object inputSource);

    /**
     * 处理来源
     */
    protected abstract void processFile(Object inputSource, File tempFile) throws Exception;

    /**
     * 校验图片
     */
    protected abstract void validPic(Object inputSource);

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

    /**
     * 构造返回对象 封装返回结果
     * @param originalFilename
     * @param tempFile
     * @param uploadPath
     * @param imageInfo
     * @return
     */
    private UploadPictureResult buildResult(String originalFilename, File tempFile, String uploadPath, ImageInfo imageInfo) {
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        // 宽高、宽高比
        int picWidth = imageInfo.getWidth();
        int picHeight = imageInfo.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();

        // 封装返回结果
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
        // 影响存到数据库中的name
        uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
        uploadPictureResult.setPicSize(FileUtil.size(tempFile));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(imageInfo.getFormat());
        return uploadPictureResult;
    }

    /**
     * 封裝返回結果
     * @param originalFilename 原始文件名
     * @param compressedCiObject 压缩后图像
     * @param thumbnailCiObject 缩略图
     */
    private UploadPictureResult buildResult(String originalFilename, CIObject compressedCiObject , CIObject thumbnailCiObject) {
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        // 宽高、宽高比
        int picWidth = compressedCiObject.getWidth();
        int picHeight = compressedCiObject.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();

        // 封装返回结果
        // 设置原图地址
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + compressedCiObject.getKey());
        // 影响存到数据库中的name
        uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
        uploadPictureResult.setPicSize(compressedCiObject.getSize().longValue());
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(compressedCiObject.getFormat());
        // 设置缩略图地址
        uploadPictureResult.setThumbnailUrl(cosClientConfig.getHost() + '/' + thumbnailCiObject.getKey());
        return uploadPictureResult;
    }
}
