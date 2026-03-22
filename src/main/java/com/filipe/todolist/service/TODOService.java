package com.filipe.todolist.service;

import com.filipe.todolist.TODOMapper;
import com.filipe.todolist.domain.TODO;
import com.filipe.todolist.dto.Request;
import com.filipe.todolist.dto.Response;
import com.filipe.todolist.repository.TODORepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TODOService {


	private final TODORepository repository;
	private final TODOMapper mapper;

	public TODOService(TODORepository repository, TODOMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	public List<Response> findAll() {
		log.info("Service.findAll called");
		var res = repository.findAll().stream().map(mapper::toResponse).collect(Collectors.toList());
		log.debug("Service.findAll returning {} items", res.size());
		return res;
	}

	public Optional<Response> findById(UUID id) {
		log.info("Service.findById called with id={}", id);
		var r = repository.findById(id).map(mapper::toResponse);
		if (r.isPresent()) log.debug("Service.findById found {}", r.get());
		else log.debug("Service.findById not found for id={}", id);
		return r;
	}

	public Response create(Request request) {
		log.info("Service.create called: {}", request);
		TODO entity = mapper.toEntity(request);
		TODO saved = repository.save(entity);
		var resp = mapper.toResponse(saved);
		log.info("Service.create saved id={}", resp.id());
		return resp;
	}

	public Optional<Response> update(UUID id, Request request) {
		log.info("Service.update called id={} request={}", id, request);
		return repository.findById(id).map(existing -> {
			mapper.updateFromRequest(request, existing);
			TODO saved = repository.save(existing);
			var resp = mapper.toResponse(saved);
			log.info("Service.update succeeded id={}", id);
			return resp;
		});
	}

	public boolean delete(UUID id) {
		log.info("Service.delete called id={}", id);
		if (repository.existsById(id)) {
			repository.deleteById(id);
			log.info("Service.delete deleted id={}", id);
			return true;
		}
		log.debug("Service.delete not found id={}", id);
		return false;
	}
}
