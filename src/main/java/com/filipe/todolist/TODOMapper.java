package com.filipe.todolist;

import com.filipe.todolist.domain.TODO;
import com.filipe.todolist.dto.Request;
import com.filipe.todolist.dto.Response;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TODOMapper {

    public Response toResponse(TODO todo) {
        if (todo == null) return null;
        return new Response(todo.getId(), todo.getTitle(), todo.getDescription(), todo.isCompleted());
    }

    public TODO toEntity(Request request) {
        if (request == null) return null;
        TODO t = new TODO();
        // id is generated on persist if not set
        t.setTitle(request.title());
        t.setDescription(request.description());
        t.setCompleted(request.completed() != null ? request.completed() : false);
        return t;
    }

    public void updateFromRequest(Request request, TODO entity) {
        if (request == null || entity == null) return;
        if (request.title() != null) entity.setTitle(request.title());
        if (request.description() != null) entity.setDescription(request.description());
        if (request.completed() != null) entity.setCompleted(request.completed());
    }
}
