package com.filipe.todolist.dto;

/**
 * Request DTO used when creating or updating a TODO item.
 *
 * @param title       short title for the todo
 * @param description optional longer description
 * @param completed   optional completion flag (when null the current state is preserved)
 */
public record Request(
		String title,
		String description,
		Boolean completed
) {
}
