package com.jcgg.jcpic_backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jcgg.jcpic_backend.model.dto.picture.PictureQueryRequest;
import com.jcgg.jcpic_backend.model.dto.picture.PictureUploadRequest;
import com.jcgg.jcpic_backend.model.entity.Picture;
import com.jcgg.jcpic_backend.model.entity.User;
import com.jcgg.jcpic_backend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author GaG
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-02-21 02:32:42
*/
public interface PictureService extends IService<Picture> {

    /**
     * 上传图片
     */

    PictureVO uploadPicture (MultipartFile multipartFile, PictureUploadRequest request, User loginuser);

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
}
