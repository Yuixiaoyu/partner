package com.xiaoy.partner.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * ClassName: TeamUserVo
 * Description: 队伍和用户信息封装类
 *
 * @Author: xiaoyu
 * @create: 2024-08-04 13:29
 * @version: 1.0
 */
@Data
public class TeamUserVo implements Serializable {

    private static final long serialVersionUID = -6761548099703395510L;
    /**
     * id
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
     * 队伍图片
     */
    private String img;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     * 创建人用户信息
     */
    private UserVo createUserVo;

    /**
     * 已加入人数
     */
    private Integer hasJoinNum;

    /**
     * 用户是否已加入
     */
    private boolean hasJoin;


}
