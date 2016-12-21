package org.thiki.kanban.teams.team;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.thiki.kanban.foundation.exception.BusinessException;
import org.thiki.kanban.teams.teamMembers.TeamMembersService;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by bogehu on 7/11/16.
 */
@Service
public class TeamsService {
    @Resource
    private TeamsPersistence teamsPersistence;
    @Resource
    private TeamMembersService teamMembersService;

    @CacheEvict(value = "team", key = "contains('teams'+#userName)", allEntries = true)
    public Team create(String userName, final Team team) {
        boolean isNameConflict = teamsPersistence.isNameConflict(userName, team.getName());
        if (isNameConflict) {
            throw new BusinessException(TeamsCodes.TEAM_IS_ALREADY_EXISTS);
        }
        teamsPersistence.create(userName, team);

        teamMembersService.joinTeam(userName, team.getId());
        return teamsPersistence.findById(team.getId());
    }

    @Cacheable(value = "team", key = "'service-team'+#teamId")
    public Team findById(String teamId) {
        Team team = teamsPersistence.findById(teamId);
        if (team == null) {
            throw new BusinessException(TeamsCodes.TEAM_IS_NOT_EXISTS);
        }
        return team;
    }

    @Cacheable(value = "team", key = "'service-teams'+#userName")
    public List<Team> findByUserName(String userName) {
        return teamsPersistence.findByUserName(userName);
    }

    public boolean isTeamExist(String teamId) {
        return teamsPersistence.isTeamExist(teamId);
    }

    @CacheEvict(value = "team", key = "contains('#teamId')", allEntries = true)
    public Team update(String teamId, Team team, String userName) {
        Team originTeam = teamsPersistence.findById(teamId);
        if (originTeam == null) {
            throw new BusinessException(TeamsCodes.TEAM_IS_NOT_EXISTS);
        }
        teamsPersistence.update(teamId, team);
        return teamsPersistence.findById(teamId);
    }
}
