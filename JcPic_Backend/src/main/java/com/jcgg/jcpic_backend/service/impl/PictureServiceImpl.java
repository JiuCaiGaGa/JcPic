package com.jcgg.jcpic_backend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcgg.jcpic_backend.exception.ErrorCode;
import com.jcgg.jcpic_backend.exception.ThrowUtils;
import com.jcgg.jcpic_backend.manager.FileManager;
import com.jcgg.jcpic_backend.mapper.PictureMapper;
import com.jcgg.jcpic_backend.model.dto.file.UploadPictureResult;
import com.jcgg.jcpic_backend.model.dto.picture.PictureQueryRequest;
import com.jcgg.jcpic_backend.model.dto.picture.PictureUploadRequest;
import com.jcgg.jcpic_backend.model.entity.Picture;
import com.jcgg.jcpic_backend.model.entity.User;
import com.jcgg.jcpic_backend.model.vo.PictureVO;
import com.jcgg.jcpic_backend.model.vo.UserVO;
import com.jcgg.jcpic_backend.service.PictureService;
import com.jcgg.jcpic_backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author GaG
 * @description 针对表【picture(图片)】的数据库操作Service实现
 * @createDate 2025-02-21 02:32:42
 */
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
        implements PictureService {

    @Resource
    private FileManager fileManager;

    @Resource
    UserService userService;

    @Override
    public PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest request, User loginuser) {
        // 校验参数
        ThrowUtils.throwIf(
                loginuser == null,
                ErrorCode.NO_AUTH_ERROR
        );
        // 判断 新增/更新
        Long picID = null;
        if (request != null) { // 更新图片
            picID = request.getId();
        }
        if (picID != null) {
            // 查询数据库中是否有对应的记录
            boolean exists = this.lambdaQuery()
                    .eq(Picture::getId, picID)
                    .exists();
            ThrowUtils.throwIf(
                    !exists, // 数据库中没找到记录
                    ErrorCode.NOT_FOUND_ERROR
            );
        }

        // 上传图片
        String uploadPathPrefix = String.format("public/%s", loginuser.getId());
        UploadPictureResult uploadPictureResult = fileManager.uploadPicture(multipartFile, uploadPathPrefix);

        // 构造照片入库信息
        Picture pic = new Picture();
        pic.setUrl(uploadPictureResult.getUrl());
        pic.setName(uploadPictureResult.getPicName());
        pic.setPicSize(uploadPictureResult.getPicSize());
        pic.setPicWidth(uploadPictureResult.getPicWidth());
        pic.setPicHeight(uploadPictureResult.getPicHeight());
        pic.setPicScale(uploadPictureResult.getPicScale());
        pic.setPicFormat(uploadPictureResult.getPicFormat());
        pic.setUserId(loginuser.getId());

        if (picID != null) {
            pic.setUpdateTime(new Date());
            pic.setId(picID);
        }
        // 操作数据库

        boolean result = this.saveOrUpdate(pic);
        ThrowUtils.throwIf(
                !result,
                ErrorCode.OPERATION_ERROR,
                "图片上传失败"
        );
        return PictureVO.objToVo(pic);
    }

    /**
     * 获取查询条件
     *
     * @param pictureQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {

        ThrowUtils.throwIf(
                pictureQueryRequest == null,
                ErrorCode.PARAMS_ERROR, "请求参数为空"
        );
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();

        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if(StrUtil.isNotBlank(searchText)){
            queryWrapper.and(qw -> qw.like("name",searchText)
                    .or()
                    .like("introduction",searchText)
            );
        }

        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);

        // JSON 数组查询
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        // 对象转封装类
        PictureVO pictureVO = PictureVO.objToVo(picture);
        // 关联查询用户信息
        Long userId = picture.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            pictureVO.setUser(userVO);
        }
        return pictureVO;
    }
    /**
     * 分页获取图片封装
     */
    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVOPage;
        }
        // 对象列表 => 封装对象列表
        List<PictureVO> pictureVOList = pictureList.stream().map(PictureVO::objToVo).collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(userService.getUserVO(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }

    @Override
    public void validPicture(Picture picture) {
        ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();
        // 修改数据时，id 不能为空，有参数则校验
        ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCode.PARAMS_ERROR, "id 不能为空");
        if (StrUtil.isNotBlank(url)) {
            ThrowUtils.throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR, "url 过长");
        }
        if (StrUtil.isNotBlank(introduction)) {
            ThrowUtils.throwIf(introduction.length() > 800, ErrorCode.PARAMS_ERROR, "简介过长");
        }
    }


}




