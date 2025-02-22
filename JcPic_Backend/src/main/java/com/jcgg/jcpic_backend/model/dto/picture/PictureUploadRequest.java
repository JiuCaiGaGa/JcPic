package com.jcgg.jcpic_backend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

@Data
public class PictureUploadRequest implements Serializable {

    /**
     * 图片 id
     */

    private Long id;

    private static final long serialVersionUID = -7691513873066285887L;
}
