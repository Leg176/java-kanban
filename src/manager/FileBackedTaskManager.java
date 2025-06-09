package manager;

import exceptions.ManagerReadException;
import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File fileBacked;

    public FileBackedTaskManager() {
        super();
        this.fileBacked = new File("resources/TaskBacked.csv");
    }

    public FileBackedTaskManager(File fileBacked) {
        super();
        this.fileBacked = fileBacked;
    }

    // Вывод задачи по номеру Id
    @Override
    public Task getTaskById(int outputTaskId) {
        Task taskBack = super.getTaskById(outputTaskId);
        return taskBack;
    }

    @Override
    public Subtask getSubtaskById(int outputSubtaskId) {
        Subtask subtaskBack = super.getSubtaskById(outputSubtaskId);
        return subtaskBack;
    }

    @Override
    public Epic getEpicById(int outputEpicId) {
        Epic epicBack = super.getEpicById(outputEpicId);
        return epicBack;
    }

    // Вывод подзадач по номеру эпичной задачи
    @Override
    public ArrayList<Subtask> getListSubtask(int idEpic) {
        return super.getListSubtask(idEpic);
    }

    // Удаление задач
    @Override
    public void fullDelTask() {
        super.fullDelTask();
        save();
    }

    @Override
    public void fullDelSubtask() {
        super.fullDelSubtask();
        save();
    }

    @Override
    public void fullDelEpic() {
        super.fullDelEpic();
        save();
    }

    // Удаление задачи по номеру Id
    @Override
    public void deleteById(int idDel) {
        super.deleteById(idDel);
        save();
    }

    // Добавление подзадачи
    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask subtaskBack = super.createSubtask(subtask);
        save();
        return subtaskBack;
    }

    // Добавление задачи
    @Override
    public Task createTask(Task task) {
        Task taskBack = super.createTask(task);
        save();
        return taskBack;
    }

    // Добавление эпической задачи
    @Override
    public Epic createEpic(Epic epic) {
        Epic epicBack = super.createEpic(epic);
        save();
        return epicBack;
    }

    // Обновление задачи по номеру Id задачи
    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    // Обновление подзадачи по номеру Id подзадачи
    @Override
    public void updateSubTask(Subtask subtask) {
        super.updateSubTask(subtask);
        save();
    }

    // Обновление эпика по номеру Id эпика
    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    // Обновление статуса эпической задачи
    protected void updateStatusEpic(int idEpic) {
        super.updateStatusEpic(idEpic);
    }

    // Вывод всех типов задач
    @Override
    public ArrayList<Task> fullListTask() {
        return super.fullListTask();
    }

    @Override
    public ArrayList<Subtask> fullListSubtask() {
        return super.fullListSubtask();
    }

    @Override
    public ArrayList<Epic> fullListEpic() {
        return super.fullListEpic();
    }

    // Вывод истории просмотров задач
    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    // Запись в файл
    private void save() {
        try (Writer fileWriter = new FileWriter("resources/TaskBacked.csv")) {
            if (!fullListTask().isEmpty()) {
                for (Task task : fullListTask()) {
                    fileWriter.write(task.toString(task) + "\n");
                }
            }
            if (!fullListEpic().isEmpty()) {
                for (Task task : fullListEpic()) {
                    fileWriter.write(task.toString(task) + "\n");
                }
            }
            if (!fullListSubtask().isEmpty()) {
                for (Task task : fullListSubtask()) {
                    fileWriter.write(task.toString(task) + "\n");
                }
            }
        } catch (IOException exc) {
            throw new ManagerSaveException("Ошибка сохранения задач в файл" + exc.getMessage());
        }
    }

    // Создание задачи из строки
    public Task fromString(String value) {
        Task task = null;
        if (!value.isEmpty() && !value.isBlank()) {
            String[] elements = value.split(";");
            switch (TypesOfTasks.valueOf(elements[0])) {
                case EPIC:
                    int idEpic = Integer.parseInt(elements[4]);
                    task = new Epic(elements[1], elements[2], Status.valueOf(elements[3]), idEpic);
                    break;
                case SUBTASK:
                    int idSubtask = Integer.parseInt(elements[4]);
                    int idEpicInSubtask = Integer.parseInt(elements[5]);
                    task = new Subtask(elements[1], elements[2], Status.valueOf(elements[3]),
                            idSubtask, idEpicInSubtask);
                    break;
                case TASK:
                    int idTask = Integer.parseInt(elements[4]);
                    task = new Task(elements[1], elements[2], Status.valueOf(elements[3]), idTask);
                    break;
            }
        }
        return task;
    }

    // Восстанавление данные менеджера из файла при запуске программы
    private FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                String line = br.readLine();
                if (fromString(line) instanceof Epic) {
                    Epic epic = (Epic) fromString(line);
                    fileBackedTaskManager.setCounterId(epic.getId());
                    fileBackedTaskManager.createEpic(epic);
                } else if (fromString(line) instanceof Subtask) {
                    Subtask subtask = (Subtask) fromString(line);
                    fileBackedTaskManager.setCounterId(subtask.getId());
                    fileBackedTaskManager.createSubtask(subtask);
                } else {
                    Task task = fromString(line);
                    fileBackedTaskManager.setCounterId(task.getId());
                    fileBackedTaskManager.createTask(task);
                }
            }
        } catch (IOException exc) {
            throw new ManagerReadException("Ошибка чтения задач из файла" + exc.getMessage());
        }
        return fileBackedTaskManager;
    }
}

    /*// Создание задач из строк файла
    public Task fromString(String value) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager();
        try(BufferedReader br = new BufferedReader(new FileReader("resources/TaskBacked.csv"))) {
            while(br.ready()) {
                String line = br.readLine();
                if (!line.isEmpty() && !line.isBlank()) {
                    String[] elements = line.split(";");
                    switch (TypesOfTasks.valueOf(elements[0])) {
                        case TASK:
                            int idTask = Integer.parseInt(elements[4]);
                            Task task = new Task(elements[1], elements[2], Status.valueOf(elements[3]), idTask);
                            fileBackedTaskManager.setCounterId(idTask);
                            fileBackedTaskManager.createTask(task);
                            break;
                        case EPIC:
                            int idEpic = Integer.parseInt(elements[4]);
                            Epic epic = new Epic(elements[1], elements[2], Status.valueOf(elements[3]), idEpic);
                            fileBackedTaskManager.setCounterId(idEpic);
                            fileBackedTaskManager.createEpic(epic);
                            break;
                        case SUBTASK:
                            int idSubtask = Integer.parseInt(elements[4]);
                            int idEpicInSubtask = Integer.parseInt(elements[5]);
                            Subtask subtask = new Subtask(elements[1], elements[2], Status.valueOf(elements[3]),
                                    idSubtask, idEpicInSubtask);
                            fileBackedTaskManager.setCounterId(idSubtask);
                            fileBackedTaskManager.createSubtask(subtask);
                            break;
                    }
                }
            }
        } catch (IOException exc) {
            throw new ManagerReadException("Ошибка чтения задач из файла" + exc.getMessage());
        }
    }*/


