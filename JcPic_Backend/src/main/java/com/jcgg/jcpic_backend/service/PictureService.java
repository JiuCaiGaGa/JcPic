package com.jcgg.jcpic_backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jcgg.jcpic_backend.api.aliyunai.model.CreateOutPaintingTaskResponse;
import com.jcgg.jcpic_backend.model.dto.picture.*;
import com.jcgg.jcpic_backend.model.entity.Picture;
import com.jcgg.jcpic_backend.model.entity.User;
import com.jcgg.jcpic_backend.model.vo.PictureVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author GaG
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-02-21 02:32:42
*/
public interface PictureService extends IService<Picture> {

    /**
     * 上传图片
     */

    PictureVO uploadPicture (Object inputSource, PictureUploadRequest request, User loginuser);

    /**
     * 获取查询对象
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 图片包装类 单条
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 获取图片封装 多条
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 校验图片
     */
    void validPicture(Picture picture);

    /**
     * 审核图片
     */
    void doPictureReview(PictureReviewRequest request,User loginuser);

    /**
     * 填充审核参数
     */
    void fillReviewParams(Picture picture, User loginUser);

    /**
     * 清除旧照片
     * @param oldPicture 旧照片
     */
    public void clearPictureFile(Picture oldPicture);
    /**
     * 批量抓取和创建图片
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return 成功创建的图片数
     */
    Integer uploadPictureByBatch(
            PictureUploadByBatchRequest pictureUploadByBatchRequest,
            User loginUser
    );

    /**
     * 校验空间图片权限
     *
     * @param loginUser
     * @param picture
     */
    void checkPictureAuth(User loginUser, Picture picture);

    void deletePicture(long pictureId, User loginUser);


    void editPicture(PictureEditRequest pictureEditRequest, User loginUser);

    /**
     * 根据颜色搜索图片
     *
     * @param spaceId
     * @param picColor
     * @param loginUser
     * @return
     */
    List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser);

    /**
     * 批量编辑图片
     *
     * @param pictureEditByBatchRequest
     * @param loginUser
     */
    void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser);

    /**
     * 创建扩图任务
     *
     * @param createPictureOutPaintingTaskRequest
     * @param loginUser
     */
    CreateOutPaintingTaskResponse createPictureOutPaintingTask(CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginUser);
}
