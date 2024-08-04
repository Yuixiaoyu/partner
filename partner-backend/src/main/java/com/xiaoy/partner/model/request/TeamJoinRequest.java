package com.xiaoy.partner.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName: TeamJoinRequest
 * Description:
 *
 * @Author: fy
 * @create: 2024-08-03 13:52
 * @version: 1.0
 */
@Data
public class TeamJoinRequest implements Serializable {


    private static final long serialVersionUID = -3790319359064842090L;
    /**
     * id
     */
    private Long teamId;


    /**
     * 密码
     */
    private String password;
}
