package com.filipe.todolist.service;

import com.filipe.todolist.TODOMapper;
import com.filipe.todolist.domain.TODO;
import com.filipe.todolist.dto.Request;
import com.filipe.todolist.dto.Response;
import com.filipe.todolist.repository.TODORepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.logging.Logger;

@Service
public class TODOService {

	private static final Logger log = Logger.getLogger(TODOService.class.getName());

	private final TODORepository repository;
	private final TODOMapper mapper;

	public TODOService(TODORepository repository, TODOMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	public List<Response> findAll() {
		return repository.findAll().stream().map(mapper::toResponse).collect(Collectors.toList());
	}

	public Optional<Response> findById(UUID id) {
		return repository.findById(id).map(mapper::toResponse);
	}

	public Response create(Request request) {
		TODO entity = mapper.toEntity(request);
		TODO saved = repository.save(entity);
		return mapper.toResponse(saved);
	}

	public Optional<Response> update(UUID id, Request request) {
		return repository.findById(id).map(existing -> {
			mapper.updateFromRequest(request, existing);
			TODO saved = repository.save(existing);
			return mapper.toResponse(saved);
		});
	}

	public boolean delete(UUID id) {
		if (repository.existsById(id)) {
			repository.deleteById(id);
			return true;
		}
		return false;
	}
}
