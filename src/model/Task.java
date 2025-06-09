package model;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private Status status = Status.NEW;

    public Task(String name, String description, Status status, int id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TypesOfTasks typeOutput (Task task) {
        if (task instanceof Subtask) {
            return TypesOfTasks.SUBTASK;
        } else if (task instanceof Epic) {
            return TypesOfTasks.EPIC;
        }
        return  TypesOfTasks.TASK;
    }

    public String toString(Task task) {
        if (task instanceof Subtask) {
            return String.format("%s;%s;%s;%s;%s;%s", typeOutput(task), task.getName(), task.getDescription(),
                    task.getStatus(), task.getId(), ((Subtask) task).getIdEpic());
        } else {
            return String.format("%s;%s;%s;%s;%s", typeOutput(task), task.getName(), task.getDescription(),
                    task.getStatus(), task.getId());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }
}
