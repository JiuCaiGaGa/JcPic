package com.jcgg.jcpic_backend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

@Data
public class PictureUploadRequest implements Serializable {

    /**
     * 图片 id
     */
    private Long id;

    /**
     * 外部图片url
     */
    private String fileUrl;

    /**
     * 空间 id
     */
    private Long spaceId;

    /**
     * 名称
     */
    private String picName;

    private static final long serialVersionUID = -7691513873066285887L;
}
