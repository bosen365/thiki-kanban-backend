package org.thiki.kanban.projects.projectMembers;

import org.thiki.kanban.foundation.application.DomainOrder;

/**
 * Created by xubt on 8/8/16.
 */
public enum ProjectMembersCodes {
    CURRENT_USER_IS_NOT_A_MEMBER_OF_THE_PROJECT("001", "当前用户非该团队成员。"),
    USER_IS_ALREADY_A_MEMBER_OF_THE_PROJECT("002", "你已是团队成员,无须再次加入。");
    private String code;
    private String message;

    ProjectMembersCodes(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return Integer.parseInt(DomainOrder.PROJECT_MEMBER + "" + code);
    }

    public String message() {
        return message;
    }
}
