package com.filipe.todolist.dto;

import java.util.UUID;

public record Response(
		UUID id,
		String title,
		String description,
		boolean completed
) {
}
