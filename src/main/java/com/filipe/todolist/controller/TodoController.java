package com.filipe.todolist.controller;

import com.filipe.todolist.dto.Request;
import com.filipe.todolist.dto.Response;
import com.filipe.todolist.service.TODOService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * REST controller exposing CRUD operations for TODO items.
 * <p>
 * All endpoints are rooted under {@code /todo}.
 */
@Slf4j
@RestController
@RequestMapping("/v1/todo")
public class TodoController {

	private final TODOService service;

	public TodoController(TODOService service) {
		this.service = service;
	}

	/**
	 * List all TODO items.
	 *
	 * @return a list of TODO responses
	 */
	@GetMapping
	public List<Response> listAll() {
		log.debug("GET /todo - listAll called");
		var result = service.findAll();
		log.debug("GET /todo - returning {} items", result.size());
		return result;
	}

	/**
	 * Retrieve a TODO item by id.
	 *
	 * @param id UUID of the todo item
	 * @return 200 with the TODO when found, or 404 when not found
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Response> getById(@PathVariable UUID id) {
		log.info("GET /todo/{} - getById called", id);
		return service.findById(id).map(r -> {
			log.debug("GET /todo/{} - found: {}", id, r);
			return ResponseEntity.ok(r);
		}).orElseGet(() -> {
			log.warn("GET /todo/{} - not found", id);
			return ResponseEntity.notFound().build();
		});
	}

	/**
	 * Create a new TODO item from the provided request body.
	 *
	 * @param request request payload containing todo fields
	 * @return 201 Created with the new TODO in the body and Location header
	 */
	@PostMapping
	public ResponseEntity<Response> create(@RequestBody Request request) {
		log.info("POST /todo - create called: {}", request);
		Response created = service.create(request);
		log.info("POST /todo - created id={}", created.id());
		return ResponseEntity.created(URI.create("/todo/" + created.id())).body(created);
	}

	/**
	 * Update an existing TODO item.
	 *
	 * @param id      UUID of the todo to update
	 * @param request request payload with fields to update
	 * @return 200 with updated TODO when successful, or 404 if not found
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Response> update(@PathVariable UUID id, @RequestBody Request request) {
		log.info("PUT /todo/{} - update called: {}", id, request);
		return service.update(id, request).map(r -> {
			log.info("PUT /todo/{} - updated", id);
			return ResponseEntity.ok(r);
		}).orElseGet(() -> {
			log.warn("PUT /todo/{} - not found", id);
			return ResponseEntity.notFound().build();
		});
	}

	/**
	 * Delete a TODO item by id.
	 *
	 * @param id UUID of the todo to delete
	 * @return 204 when deleted, 404 when not found
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		log.info("DELETE /todo/{} - delete called", id);
		boolean deleted = service.delete(id);
		if (deleted) {
			log.info("DELETE /todo/{} - deleted", id);
			return ResponseEntity.noContent().build();
		} else {
			log.warn("DELETE /todo/{} - not found", id);
			return ResponseEntity.notFound().build();
		}
	}
}
