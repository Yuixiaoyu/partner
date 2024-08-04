package com.xiaoy.partner.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName: TeamAddRequest
 * Description:
 *
 * @Author: fy
 * @create: 2024-08-03 13:52
 * @version: 1.0
 */
@Data
public class TeamUpdateRequest implements Serializable {

    private static final long serialVersionUID = 8116492950403094402L;

    /**
     * 队伍id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;


    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;
}
