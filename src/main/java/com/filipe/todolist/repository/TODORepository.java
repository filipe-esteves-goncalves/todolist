package com.filipe.todolist.repository;

import com.filipe.todolist.domain.TODO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data repository for {@link TODO} entities.
 */
@Repository
public interface TODORepository extends JpaRepository<TODO, UUID> {
}
