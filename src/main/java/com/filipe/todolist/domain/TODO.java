package com.filipe.todolist.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "TODO")
public class TODO {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(updatable = false, nullable = false)
    private UUID id;
}
