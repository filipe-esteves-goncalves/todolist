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

@Slf4j
@RestController
@RequestMapping("/todo")
public class TodoController {


	private final TODOService service;

	public TodoController(TODOService service) {
		this.service = service;
	}

	@GetMapping
	public List<Response> listAll() {
		log.debug("GET /todo - listAll called");
		var result = service.findAll();
		log.debug("GET /todo - returning {} items", result.size());
		return result;
	}

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

	@PostMapping
	public ResponseEntity<Response> create(@RequestBody Request request) {
		log.info("POST /todo - create called: {}", request);
		Response created = service.create(request);
		log.info("POST /todo - created id={}", created.id());
		return ResponseEntity.created(URI.create("/todo/" + created.id())).body(created);
	}

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
