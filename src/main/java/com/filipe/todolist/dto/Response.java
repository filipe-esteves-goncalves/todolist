package com.filipe.todolist.dto;

import java.util.UUID;

/**
 * Response DTO returned by the API representing a TODO item.
 *
 * @param id          unique identifier
 * @param title       short title
 * @param description optional longer description
 * @param completed   completion state
 */
public record Response(
		UUID id,
		String title,
		String description,
		boolean completed
) {
}
