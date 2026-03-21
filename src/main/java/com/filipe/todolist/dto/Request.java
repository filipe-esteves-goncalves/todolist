package com.filipe.todolist.dto;

public record Request(
		String title,
		String description,
		Boolean completed
) {
}
