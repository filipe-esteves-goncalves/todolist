package com.filipe.todolist.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * JPA entity representing a TODO item persisted in the database.
 */
@Entity(name = "TODO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Ensure the entity has an id before persisting.
     * Set createAt and updatedAt timestamps to the current time.
     */
    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }


    /**
     * Update updatedAt timestamps when updating the row
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
