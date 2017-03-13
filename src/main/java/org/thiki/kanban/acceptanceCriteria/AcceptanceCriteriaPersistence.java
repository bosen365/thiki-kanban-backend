package org.thiki.kanban.acceptanceCriteria;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.thiki.kanban.verification.Verification;

import java.util.List;

/**
 * Created by xubt on 10/17/16.
 */
@Repository
public interface AcceptanceCriteriaPersistence {
    Integer addAcceptCriteria(@Param("userName") String userName, @Param("cardId") String cardId, @Param("acceptanceCriteria") AcceptanceCriteria acceptanceCriteria);

    AcceptanceCriteria findById(String id);

    List<AcceptanceCriteria> loadAcceptanceCriteriasByCardId(@Param("cardId") String cardId);

    Integer updateAcceptCriteria(@Param("acceptanceCriteriaId") String acceptanceCriteriaId, @Param("acceptanceCriteria") AcceptanceCriteria acceptanceCriteria);

    Integer deleteAcceptanceCriteria(String acceptanceCriteriaId);

    Integer resort(@Param("acceptanceCriteria") AcceptanceCriteria acceptanceCriteria);

    boolean isHasUnFinishedAcceptanceCriterias(@Param("cardId") String cardId);

    void verify(@Param("cardId") String cardId, @Param("acceptanceCriteriaId") String acceptanceCriteriaId, @Param("verification") Verification verification);
}
