package org.thiki.kanban.assignment;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by xubitao on 6/16/16.
 */

@Repository
public interface AssignmentPersistence {
    Integer create(Assignment assignment);

    Assignment findById(@Param("id") String id);

    List<Assignment> findByCardId(@Param("cardId") String cardId);

    int deleteById(String id);

    boolean isAlreadyAssigned(@Param("assignee") String assignee, @Param("cardId") String cardId);
}
