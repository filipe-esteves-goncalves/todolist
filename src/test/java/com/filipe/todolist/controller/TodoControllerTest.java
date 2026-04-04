package com.filipe.todolist.controller;

import com.filipe.todolist.dto.Request;
import com.filipe.todolist.dto.Response;
import com.filipe.todolist.service.TODOService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TodoControllerTest {

    @Test
    void listAll() {
        TODOService svc = Mockito.mock(TODOService.class);
        Response r1 = new Response(UUID.randomUUID(), "a", "d", false);
        Response r2 = new Response(UUID.randomUUID(), "b", null, true);
        Mockito.when(svc.findAll()).thenReturn(List.of(r1, r2));

        TodoController controller = new TodoController(svc);
        var result = controller.listAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(r1, result.get(0));
    }

    @Test
    void getById_foundAndNotFound() {
        TODOService svc = Mockito.mock(TODOService.class);
        UUID id = UUID.randomUUID();
        Response resp = new Response(id, "t", "desc", false);
        Mockito.when(svc.findById(id)).thenReturn(Optional.of(resp));

        TodoController controller = new TodoController(svc);
        ResponseEntity<Response> ok = controller.getById(id);
        assertEquals(200, ok.getStatusCode().value());
        assertEquals(resp, ok.getBody());

        UUID miss = UUID.randomUUID();
        Mockito.when(svc.findById(miss)).thenReturn(Optional.empty());
        ResponseEntity<Response> notFound = controller.getById(miss);
        assertEquals(404, notFound.getStatusCode().value());
        assertNull(notFound.getBody());
    }

    @Test
    void create() {
        TODOService svc = Mockito.mock(TODOService.class);
        Request req = new Request("title", "desc", Boolean.FALSE);
        Response created = new Response(UUID.randomUUID(), "title", "desc", false);
        Mockito.when(svc.create(req)).thenReturn(created);

        TodoController controller = new TodoController(svc);
        ResponseEntity<Response> res = controller.create(req);

        assertEquals(201, res.getStatusCode().value());
        assertEquals(created, res.getBody());
        assertTrue(res.getHeaders().getLocation().toString().endsWith("/todo/" + created.id()));
    }

    @Test
    void update_foundAndNotFound() {
        TODOService svc = Mockito.mock(TODOService.class);
        UUID id = UUID.randomUUID();
        Request req = new Request("t", null, null);
        Response updated = new Response(id, "t", "desc", false);
        Mockito.when(svc.update(id, req)).thenReturn(Optional.of(updated));

        TodoController controller = new TodoController(svc);
        ResponseEntity<Response> ok = controller.update(id, req);
        assertEquals(200, ok.getStatusCode().value());
        assertEquals(updated, ok.getBody());

        UUID miss = UUID.randomUUID();
        Mockito.when(svc.update(miss, req)).thenReturn(Optional.empty());
        ResponseEntity<Response> notFound = controller.update(miss, req);
        assertEquals(404, notFound.getStatusCode().value());
    }

    @Test
    void delete_foundAndNotFound() {
        TODOService svc = Mockito.mock(TODOService.class);
        UUID id = UUID.randomUUID();
        Mockito.when(svc.delete(id)).thenReturn(true);

        TodoController controller = new TodoController(svc);
        ResponseEntity<Void> noContent = controller.delete(id);
        assertEquals(204, noContent.getStatusCode().value());

        UUID miss = UUID.randomUUID();
        Mockito.when(svc.delete(miss)).thenReturn(false);
        ResponseEntity<Void> notFound = controller.delete(miss);
        assertEquals(404, notFound.getStatusCode().value());
    }
}