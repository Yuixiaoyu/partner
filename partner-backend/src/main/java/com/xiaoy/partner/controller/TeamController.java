package com.xiaoy.partner.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaoy.partner.common.BaseResponse;
import com.xiaoy.partner.common.ErrorCode;
import com.xiaoy.partner.common.ResultUtils;
import com.xiaoy.partner.exception.BusinessException;
import com.xiaoy.partner.model.domain.Team;
import com.xiaoy.partner.model.domain.User;
import com.xiaoy.partner.model.domain.UserTeam;
import com.xiaoy.partner.model.dto.TeamQuery;
import com.xiaoy.partner.model.request.TeamAddRequest;
import com.xiaoy.partner.model.request.TeamJoinRequest;
import com.xiaoy.partner.model.request.TeamQuitRequest;
import com.xiaoy.partner.model.request.TeamUpdateRequest;
import com.xiaoy.partner.model.vo.TeamUserVo;
import com.xiaoy.partner.service.TeamService;
import com.xiaoy.partner.service.UserService;
import com.xiaoy.partner.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 组队接口
 *
 * @author
 * @from
 */
@RestController
@RequestMapping("/team")
//@CrossOrigin(origins = {"http://localhost:9801"})
@Slf4j
public class TeamController {

    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    @Resource
    private UserTeamService userTeamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        long result = teamService.addTeam(team, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(id, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/get/{id}")
    public BaseResponse<Team> getTeamByid(@PathVariable long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    /**
     * 获取我加入的队伍
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVo>> listMyJoinTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        long userId = loginUser.getId();
        queryWrapper.eq("userId", userId);
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        List<Long> teamIdList = userTeamList.stream()
                .filter(userTeam -> userTeam.getUserId() == userId)
                .map(UserTeam::getTeamId).distinct().collect(Collectors.toList());
        teamQuery.setIdList(teamIdList);
        List<TeamUserVo> teamList = teamService.listTeams(teamQuery, true);
        if (teamList == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(teamList);
    }

    /**
     * 获取我创建的队伍
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVo>> listMyCreateTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId());
        List<TeamUserVo> teamList = teamService.listTeams(teamQuery, true);
        if (teamList == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(teamList);
    }

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVo>> listTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        List<TeamUserVo> teamList = teamService.listTeams(teamQuery, isAdmin);
        if (teamList == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        //判断当前用户是否已加入队伍
        List<Long> teamIdList = teamList.stream().map(TeamUserVo::getId).collect(Collectors.toList());
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        try {
            User loginUser = userService.getLoginUser(request);
            userTeamQueryWrapper.eq("userId",loginUser.getId());
            userTeamQueryWrapper.in("teamId",teamIdList);
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
            //已加入的队伍id
            Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getId).collect(Collectors.toSet());
            teamList.forEach(team->{
                boolean contains = hasJoinTeamIdSet.contains(team.getId());
                team.setHasJoin(contains);
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResultUtils.success(teamList);
    }


    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team, teamQuery);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        Page<Team> resultPage = teamService.page(page, queryWrapper);
        if (resultPage == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(resultPage);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {

        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.QuitTeam(teamQuitRequest, loginUser);
        return ResultUtils.success(result);
    }


}
