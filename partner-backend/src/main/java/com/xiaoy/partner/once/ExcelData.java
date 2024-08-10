package com.xiaoy.partner.once;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;

import java.util.Date;

/**
 * ClassName: ExcelData
 * Description:
 *
 * @Author: xiaoyu
 * @create: 2024-07-29 10:35
 * @version: 1.0
 */
public class ExcelData {
    /**
     * id
     */
    @ExcelProperty("id")
    private Long id;

    /**
     * 用户昵称
     */
    @ExcelProperty("昵称")
    private String username;

    /**
     * 账号
     */
    @ExcelProperty("账号")
    private String userAccount;

    /**
     * 用户头像
     */
    @ExcelProperty("头像")
    private String avatarUrl;

    /**
     * 性别
     */
    @ExcelProperty("性别")
    private Integer gender;

    /**
     * 密码
     */
    @ExcelProperty("密码")
    private String userPassword;

    /**
     * 电话
     */
    @ExcelProperty("电话")
    private String phone;

    /**
     * 邮箱
     */
    @ExcelProperty("邮箱")
    private String email;

    /**
     * 状态 0 - 正常
     */
    @ExcelProperty("状态")
    private Integer userStatus;

    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    private Date createTime;

    /**
     * 修改时间
     */
    @ExcelProperty("修改时间")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @ExcelProperty("是否删除")
    private Integer isDelete;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    @ExcelProperty("用户角色")
    private Integer userRole;

    /**
     * 用户标签列表json
     */
    @ExcelProperty("用户标签列表json")
    private String tags;

    /**
     * 星球编号
     */
    @ExcelProperty("星球编号")
    private String planetCode;

}
