package com.example.test_todo.controllers;

import com.example.test_todo.entities.Todo;
import com.example.test_todo.repositories.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("todo")
public class TodoController {
    private final TodoRepository todoRepository;

    @Autowired
    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @PostMapping
    public ResponseEntity<Todo> createTodo(@Valid @RequestBody Todo todo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(todoRepository.save(todo));
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getTodos() {
        return ResponseEntity.status(HttpStatus.OK).body(todoRepository.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @Valid @RequestBody Todo newtodo) {
        Optional<Todo> todo = todoRepository.findById(id);
        if (todo.isPresent()) {
            Todo tmp = new Todo(todo.get().getId(), newtodo.getTodo(), todo.get().getCreatedAt());
            return ResponseEntity.status(HttpStatus.OK).body(todoRepository.save(tmp));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(newtodo);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HashMap<String, String>> detleteTodo(@PathVariable Long id) {
        Optional<Todo> todo = todoRepository.findById(id);
        HashMap<String, String> response = new HashMap<String, String>();
        if (todo.isPresent()) {
            todoRepository.deleteById(id);
            response.put("msg", "todo has been deleted");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("msg", "todo do not exists");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
