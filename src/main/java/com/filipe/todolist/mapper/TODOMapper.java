package com.filipe.todolist.mapper;

import com.filipe.todolist.domain.TODO;
import com.filipe.todolist.dto.Request;
import com.filipe.todolist.dto.Response;
import org.springframework.stereotype.Component;

/**
 * Mapper component that converts between {@link TODO} entities and API DTOs.
 */
@Component
public class TODOMapper {

    /**
     * Map a TODO entity to a Response DTO.
     *
     * @param todo entity to map
     * @return response DTO or null when input is null
     */
    public Response toResponse(TODO todo) {
        if (todo == null) return null;
        return new Response(todo.getId(), todo.getTitle(), todo.getDescription(), todo.isCompleted());
    }

    /**
     * Map a Request DTO to a new TODO entity instance.
     *
     * @param request incoming request data
     * @return new TODO entity or null when request is null
     */
    public TODO toEntity(Request request) {
        if (request == null) return null;
        TODO t = new TODO();
        // id is generated on persist if not set
        t.setTitle(request.title());
        t.setDescription(request.description());
        t.setCompleted(request.completed() != null ? request.completed() : false);
        return t;
    }

    /**
     * Update an existing entity with values from the request. Null request fields are ignored.
     *
     * @param request incoming request with new values
     * @param entity  existing entity to update
     */
    public void updateFromRequest(Request request, TODO entity) {
        if (request == null || entity == null) return;
        if (request.title() != null) entity.setTitle(request.title());
        if (request.description() != null) entity.setDescription(request.description());
        if (request.completed() != null) entity.setCompleted(request.completed());
    }
}
