package com.predictionMarket.predictionMarket.todos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="todos")
public class Todo {
    @Id
    @Column(name="id", unique=true, nullable=false)
    private UUID id;
    private String content;
    private Instant due_date;
    private boolean is_complete;

    public Todo(String content, Instant due_date) {
        this.id = UUID.randomUUID();
        this.content = content;
        this.due_date = due_date;
    }

    public Todo(String content) {
        this.id = UUID.randomUUID();
        this.content = content;
    }

    public Todo(){
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getDue_date() {
        return due_date;
    }

    public void setDue_date(Instant due_date) {
        this.due_date = due_date;
    }

    public boolean isIs_complete() {
        return is_complete;
    }

    public void setIs_complete(boolean is_complete) {
        this.is_complete = is_complete;
    }
}
