package manager;

import exceptions.ManagerReadException;
import exceptions.ManagerSaveException;
import model.*;

import java.io.*;

import static manager.CSVConverter.fromString;

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

    // Запись в файл
    private void save() {
        try (Writer fileWriter = new FileWriter("resources/TaskBacked.csv")) {
            fileWriter.write("id,type,name,status,description,epic,duration,startTime" + "\n");
            if (!listTask.values().isEmpty()) {
                for (Task task : listTask.values()) {
                    fileWriter.write(CSVConverter.transformationString(task) + "\n");
                }
            }
            if (!listEpic.values().isEmpty()) {
                for (Epic epic : listEpic.values()) {
                    fileWriter.write(CSVConverter.transformationString(epic) + "\n");
                }
            }
            if (!listSubtask.values().isEmpty()) {
                for (Subtask subtask : listSubtask.values()) {
                    fileWriter.write(CSVConverter.transformationString(subtask) + "\n");
                }
            }
        } catch (IOException exc) {
            throw new ManagerSaveException("Ошибка сохранения задач в файл" + exc.getMessage());
        }
    }

    // Восстанавление данных менеджера из файла при запуске программы
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager();
        int idMax = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                String line = br.readLine();
                if (line.equals("id,type,name,status,description,epic,duration,startTime")) {
                    continue;
                }
                Task task = fromString(line);
                int idTask = task.getId();
                idMax = Math.max(idTask, idMax);
                switch (task.getType()) {
                    case SUBTASK:
                        fileBackedTaskManager.listSubtask.put(idTask, (Subtask) task);
                        break;
                    case EPIC:
                        fileBackedTaskManager.listEpic.put(idTask, (Epic) task);
                        break;
                    case TASK:
                        fileBackedTaskManager.listTask.put(idTask, task);
                        break;
                }
            }
        } catch (
                IOException exc) {
            throw new ManagerReadException("Ошибка чтения задач из файла" + exc.getMessage());
        }
        fileBackedTaskManager.setCounterId(idMax + 1);
        return fileBackedTaskManager;
    }
}



