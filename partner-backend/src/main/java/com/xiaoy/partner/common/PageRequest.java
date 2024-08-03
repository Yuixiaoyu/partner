package com.xiaoy.partner.common;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: PageRequest
 * Description: 通用分页参数
 *
 * @Author: fy
 * @create: 2024-08-03 9:53
 * @version: 1.0
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = -755667666387604046L;
    /**
     * 页面大小
     */
    protected int pageSize =10;

    /**
     * 当前是第几页
     */
    protected int pageNum = 1;

}
