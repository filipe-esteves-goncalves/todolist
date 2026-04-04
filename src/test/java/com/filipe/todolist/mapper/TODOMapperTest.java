package com.filipe.todolist.mapper;

import com.filipe.todolist.domain.TODO;
import com.filipe.todolist.dto.Request;
import com.filipe.todolist.dto.Response;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TODOMapper}.
 */
class TODOMapperTest {

    @Test
    void toResponse() {
        TODOMapper mapper = new TODOMapper();
        UUID id = UUID.randomUUID();
        TODO todo = new TODO();
        todo.setId(id);
        todo.setTitle("title");
        todo.setDescription("desc");
        todo.setCompleted(true);

        Response resp = mapper.toResponse(todo);

        assertNotNull(resp);
        assertEquals(id, resp.id());
        assertEquals("title", resp.title());
        assertEquals("desc", resp.description());
        assertTrue(resp.completed());
    }

    @Test
    void toEntity() {
        TODOMapper mapper = new TODOMapper();
        Request req = new Request("buy milk", "2 liters", Boolean.TRUE);

        TODO entity = mapper.toEntity(req);

        assertNotNull(entity);
        // id is generated on persist, mapper should not set it
        assertNull(entity.getId());
        assertEquals("buy milk", entity.getTitle());
        assertEquals("2 liters", entity.getDescription());
        assertTrue(entity.isCompleted());
    }

    @Test
    void updateFromRequest() {
        TODOMapper mapper = new TODOMapper();
        TODO original = new TODO();
        original.setId(UUID.randomUUID());
        original.setTitle("old");
        original.setDescription("old desc");
        original.setCompleted(false);

        // Only update title and leave completed null (should be ignored)
        Request req = new Request("new title", null, null);

        mapper.updateFromRequest(req, original);

        assertEquals("new title", original.getTitle());
        assertEquals("old desc", original.getDescription());
        assertFalse(original.isCompleted());
    }
}