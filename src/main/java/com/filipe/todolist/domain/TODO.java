package com.filipe.todolist.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

import java.util.UUID;

/**
 * JPA entity representing a TODO item persisted in the database.
 */
@Entity(name = "TODO")
public class TODO {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private boolean completed = false;

    /**
     * Default constructor required by JPA.
     */
    public TODO() {
    }

    /**
     * Full constructor used for manual instantiation in tests or mapping.
     *
     * @param id          unique identifier
     * @param title       short title
     * @param description longer description
     * @param completed   completion flag
     */
    public TODO(UUID id, String title, String description, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    /**
     * Ensure the entity has an id before persisting.
     */
    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    /**
     * @return the unique identifier of the todo
     */
    public UUID getId() {
        return id;
    }

    /**
     * @param id new id to set
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * @return the todo title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title new title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the todo description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description new description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return true when the todo is completed
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * @param completed new completion state
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
