package com.predictionMarket.predictionMarket.todos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TodoService {
    private final TodoRepository todoRepository;

    @Autowired
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Todo addTodo(Todo todo){
        todoRepository.save(todo);
        return todo;
    }
    public List<Todo> getTodos(){
        return todoRepository.findAll();
    }
    public Todo updateTodo(Todo updatedTodo){
        Optional<Todo> existingTodo = todoRepository.findById(updatedTodo.getId());
        if(existingTodo.isPresent()){
            Todo todoToUpdate = existingTodo.get();
            todoToUpdate.setContent(updatedTodo.getContent());
            todoToUpdate.setDue_date(updatedTodo.getDue_date());
            todoToUpdate.setIs_complete(updatedTodo.isIs_complete());

            todoRepository.save(todoToUpdate);
            return todoToUpdate;
        }
        return null;
    }
    public void deleteTodo(UUID id){
        todoRepository.deleteById(id);
    }
}
