package com.filipe.todolist.dto;

import java.util.UUID;

public record Request(
		String title,
		String description,
		Boolean completed
) {
}
