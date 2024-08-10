package com.xiaoy.partner.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: TeamQuiteRequest
 * Description: 用户退出队伍请求体
 *
 * @Author: xiaoyu
 * @create: 2024-08-03 13:52
 * @version: 1.0
 */
@Data
public class TeamQuitRequest implements Serializable {



    private static final long serialVersionUID = -688343722276425886L;

    /**
     * id
     */
    private Long teamId;

}
