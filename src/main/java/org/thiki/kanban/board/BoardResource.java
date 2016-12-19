package org.thiki.kanban.board;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.thiki.kanban.board.overall.BoardsOverallController;
import org.thiki.kanban.foundation.common.RestResource;
import org.thiki.kanban.foundation.hateoas.TLink;
import org.thiki.kanban.procedure.ProceduresController;
import org.thiki.kanban.tag.TagsController;
import org.thiki.kanban.teams.team.TeamsController;

import javax.annotation.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by xubitao on 05/26/16.
 */
@Service
public class BoardResource extends RestResource {
    public static Logger logger = LoggerFactory.getLogger(BoardResource.class);
    @Resource
    private TLink tlink;

    public Object toResource(String userName) throws Exception {
        logger.info("build board resource.userName:{}", userName);
        Link allLink = linkTo(methodOn(BoardsController.class).loadByUserName(userName)).withRel("all");
        this.add(tlink.from(allLink));
        logger.info("board resource building complete.userName:{}", userName);
        return getResource();
    }

    public Object toResource(Board board, String userName) throws Exception {
        logger.info("build board resource.board:{},userName:{}", board, userName);
        this.domainObject = board;
        if (board != null) {
            Link selfLink = linkTo(methodOn(BoardsController.class).findById(board.getId(), userName)).withSelfRel();
            this.add(tlink.from(selfLink).build());

            Link proceduresLink = linkTo(methodOn(ProceduresController.class).loadAll(board.getId(), userName)).withRel("procedures");
            this.add(tlink.from(proceduresLink).build());
            if (board.getTeamId() != null) {
                Link teamLink = linkTo(methodOn(TeamsController.class).findById(board.getTeamId(), userName)).withRel("team");
                this.add(tlink.from(teamLink).build());
            }

            Link tagsLink = linkTo(methodOn(TagsController.class).loadTagsByBoard(board.getId(), userName)).withRel("tags");
            this.add(tlink.from(tagsLink).build());

            Link overAllLink = linkTo(methodOn(BoardsOverallController.class).load(board.getId(), userName)).withRel("overall");
            this.add(tlink.from(overAllLink).build(userName));
        }
        Link allLink = linkTo(methodOn(BoardsController.class).loadByUserName(userName)).withRel("all");
        this.add(tlink.from(allLink).build());
        logger.info("board resource building complete.board:{},userName:{}", board, userName);
        return getResource();
    }
}
