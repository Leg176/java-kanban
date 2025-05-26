package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;

public interface TaskManager {
    // Вывод задачи по номеру Id
    Task getTaskById(int outputTaskId);

    // Вывод подзадачи по номеру Id
    Subtask getSubtaskById(int outputSubtaskId);

    // Вывод эпической задачи по номеру Id
    Epic getEpicById(int outputEpicId);

    // Удаление задач
    void fullDelTask();

    // Удаление подзадач
    void fullDelSubtask();

    // Удаление эпических задач
    void fullDelEpic();

    // Удаление задачи по номеру Id
    void deleteById(int idDel);

    // Вывод подзадач по номеру эпичной задачи
    ArrayList<Subtask> getListSubtask(int idEpic);

    // Добавление подзадачи
    Subtask createSubtask(Subtask subtask);

    // Добавление задачи
    Task createTask(Task task);

    // Добавление эпической задачи
    Epic createEpic(Epic epic);

    // Обновление задачи по номеру Id задачи
    void updateTask(Task task);

    // Обновление подзадачи по номеру Id подзадачи
    void updateSubTask(Subtask subtask);

    // Обновление эпика по номеру Id эпика
    void updateEpic(Epic epic);

    ArrayList<Task> getHistory();

    // Вывод всех задач
    ArrayList<Task> fullListTask();

    // Вывод всех подзадач
    ArrayList<Subtask> fullListSubtask();

    // Вывод всех эпических задач
    ArrayList<Epic> fullListEpic();
}
