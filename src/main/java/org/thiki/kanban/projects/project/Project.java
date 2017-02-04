package org.thiki.kanban.projects.project;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by bogehu on 7/11/16.
 */

public class Project {
    private String id;
    @NotEmpty(message = ProjectCodes.nameIsRequired)
    @NotNull(message = ProjectCodes.nameIsRequired)
    @Length(max = 20, message = ProjectCodes.nameIsInvalid)
    private String name;
    private String author;
    private String creationTime;
    private String modificationTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(String modificationTime) {
        this.modificationTime = modificationTime;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }
}
