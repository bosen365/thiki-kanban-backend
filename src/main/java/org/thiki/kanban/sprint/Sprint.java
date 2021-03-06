package org.thiki.kanban.sprint;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.thiki.kanban.foundation.common.date.DateService;

import java.util.Date;

/**
 * Created by xubt on 04/02/2017.
 */
public class Sprint {
    private String id;
    @Length(max = 40)
    @NotEmpty(message = SprintCodes.sprintNameIsRequired)
    private String sprintName;
    @NotEmpty(message = SprintCodes.startTimeIsRequired)
    private String startTime;
    @NotEmpty(message = SprintCodes.endTimeIsRequired)
    private String endTime;
    @Range(min = 1, max = 2)
    private Integer status;
    private String competedTime;
    private String archiveId;
    private String creationTime;
    private String modificationTime;
    private int remainingDays;
    private int totalDays;
    private int wentDays;
    private String boardId;

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(String modificationTime) {
        this.modificationTime = modificationTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public boolean isStartTimeAfterEndTime() {
        DateService dateService = new DateService();
        return dateService.isAfter(this.startTime, this.endTime);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public int getRemainingDays() {
        return DateService.instance().daysBetween(new Date(), this.endTime);
    }

    public int getWentDays() {
        return DateService.instance().daysBetween(this.startTime, new Date());
    }

    public int getTotalDays() {
        return DateService.instance().daysBetween(this.startTime, this.endTime);
    }

    @JsonIgnore
    public boolean isCompleted() {
        return SprintCodes.SPRINT_COMPLETED.equals(status);
    }

    public String getCompetedTime() {
        return competedTime;
    }

    public void setCompetedTime(String competedTime) {
        this.competedTime = competedTime;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public String getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(String archiveId) {
        this.archiveId = archiveId;
    }
}
