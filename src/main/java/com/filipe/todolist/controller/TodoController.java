package com.filipe.todolist.controller;

import com.filipe.todolist.dto.Request;
import com.filipe.todolist.dto.Response;
import com.filipe.todolist.service.TODOService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("/todo")
public class TodoController {

	private static final Logger log = Logger.getLogger(TodoController.class.getName());

	private final TODOService service;

	public TodoController(TODOService service) {
		this.service = service;
	}

	@GetMapping
	public List<Response> listAll() {
		return service.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Response> getById(@PathVariable UUID id) {
		return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Response> create(@RequestBody Request request) {
		Response created = service.create(request);
		return ResponseEntity.created(URI.create("/todo/" + created.id())).body(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Response> update(@PathVariable UUID id, @RequestBody Request request) {
		return service.update(id, request).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		boolean deleted = service.delete(id);
		return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}
}
