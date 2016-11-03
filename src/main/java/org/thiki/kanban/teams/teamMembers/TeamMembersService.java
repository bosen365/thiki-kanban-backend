package org.thiki.kanban.teams.teamMembers;

import org.springframework.stereotype.Service;
import org.thiki.kanban.foundation.exception.BusinessException;
import org.thiki.kanban.foundation.exception.InvalidParamsException;
import org.thiki.kanban.foundation.exception.UnauthorisedException;
import org.thiki.kanban.teams.team.Team;
import org.thiki.kanban.teams.team.TeamsCodes;
import org.thiki.kanban.teams.team.TeamsService;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;

/**
 * Created by 濤 on 7/26/16.
 */
@Service
public class TeamMembersService {
    @Resource
    private TeamMembersPersistence teamMembersPersistence;
    @Resource
    private TeamsService teamsService;

    public TeamMember joinTeam(String teamId, final TeamMember teamMember, String userName) {
        Team targetTeam = teamsService.findById(teamId);
        if (targetTeam == null) {
            throw new BusinessException(TeamsCodes.TEAM_IS_NOT_EXISTS);
        }

        TeamMember foundMember = teamMembersPersistence.findMemberByTeamId(teamMember.getMember(), teamId);
        if (foundMember != null) {
            throw new InvalidParamsException(MessageFormat.format("Member named {0} is already in the teams.", teamMember.getMember()));
        }

        teamMember.setAuthor(userName);
        teamMember.setTeamId(teamId);
        teamMembersPersistence.joinTeam(teamMember);
        return teamMembersPersistence.findById(teamMember.getId());
    }

    public TeamMember joinTeam(String userName, String teamId) {
        boolean isAlreadyJoinTeam = teamMembersPersistence.isAMemberOfTheTeam(userName, teamId);
        if (isAlreadyJoinTeam) {
            throw new BusinessException(TeamMembersCodes.USER_IS_ALREADY_A_MEMBER_OF_THE_TEAM);
        }
        TeamMember teamMember = new TeamMember();
        teamMember.setTeamId(teamId);
        teamMember.setMember(userName);
        teamMember.setAuthor(userName);
        teamMembersPersistence.joinTeam(teamMember);
        return teamMembersPersistence.findById(teamMember.getId());
    }

    public List<Member> loadMembersByTeamId(String userName, String teamId) {

        boolean isTeamExist = teamsService.isTeamExist(teamId);
        if (!isTeamExist) {
            throw new BusinessException(TeamsCodes.TEAM_IS_NOT_EXISTS);
        }

        boolean isAMember = teamMembersPersistence.isAMemberOfTheTeam(userName, teamId);
        if (!isAMember) {
            throw new UnauthorisedException(TeamMembersCodes.CURRENT_USER_IS_NOT_A_MEMBER_OF_THE_TEAM);
        }
        List<Member> members = teamMembersPersistence.loadMembersByTeamId(teamId);

        return members;
    }

    public boolean isMember(String teamId, String userName) {

        return teamMembersPersistence.isAMemberOfTheTeam(userName, teamId);
    }

    public void leaveTeam(String teamId, String memberName) {
        teamMembersPersistence.leaveTeam(teamId, memberName);
    }

    public List<Team> loadTeamsByUserName(String userName) {
        return teamMembersPersistence.findTeams(userName);
    }
}
