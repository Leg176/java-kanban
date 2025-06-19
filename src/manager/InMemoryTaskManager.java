package manager;

import model.Task;
import model.Epic;
import model.Subtask;
import model.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> listTask = new HashMap<>();
    protected HashMap<Integer, Subtask> listSubtask = new HashMap<>();
    protected HashMap<Integer, Epic> listEpic = new HashMap<>();
    private int counterId = 1;

    private HistoryManager historyManager = Managers.getDefaultHistory();

    // Вывод задачи по номеру Id
    @Override
    public Task getTaskById(int outputTaskId) {
        Task task = listTask.get(outputTaskId);
        historyManager.addTask(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int outputSubtaskId) {
        Subtask subtask = listSubtask.get(outputSubtaskId);
        historyManager.addTask(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int outputEpicId) {
        Epic epic = listEpic.get(outputEpicId);
        historyManager.addTask(epic);
        return epic;
    }

    // Удаление задач
    @Override
    public void fullDelTask() {
        for (Integer key : listTask.keySet()) {
            historyManager.remove(key);
        }
        listTask.clear();
    }

    @Override
    public void fullDelSubtask() {
        for (Integer key : listSubtask.keySet()) {
            historyManager.remove(key);
        }
        listSubtask.clear();
        for (Epic epic : listEpic.values()) {
            epic.getListSubtaskEpic().clear();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public void fullDelEpic() {
        for (Integer key : listSubtask.keySet()) {
            historyManager.remove(key);
        }
        for (Integer key : listEpic.keySet()) {
            historyManager.remove(key);
        }
        listSubtask.clear();
        listEpic.clear();
    }

    // Удаление задачи по номеру Id
    @Override
    public void deleteById(int idDel) {
        if (listTask.containsKey(idDel)) {
            listTask.remove(idDel);
        } else if (listSubtask.containsKey(idDel)) {
            Subtask subtaskToDelete = listSubtask.get(idDel);
            Epic epic = listEpic.get(subtaskToDelete.getIdEpic());
            ArrayList<Integer> newListSubtaskEpic = epic.getListSubtaskEpic();
            newListSubtaskEpic.remove((Integer) idDel);
            listSubtask.remove(idDel);
            updateStatusEpic(epic.getId());
        } else if (listEpic.containsKey(idDel)) {
            ArrayList<Integer> idSubtaskEpic = listEpic.get(idDel).getListSubtaskEpic();
            for (int idSubtask : idSubtaskEpic) {
                listSubtask.remove(idSubtask);
            }
            listEpic.remove(idDel);
        }
        historyManager.remove(idDel);
    }

    // Вывод подзадач по номеру эпичной задачи
    @Override
    public ArrayList<Subtask> getListSubtask(int idEpic) {
        if (listEpic.containsKey(idEpic)) {
            ArrayList<Subtask> listSubtasks = new ArrayList<>();
            ArrayList<Integer> idSubtaskEpic = listEpic.get(idEpic).getListSubtaskEpic();
            for (int idSubtask : idSubtaskEpic) {
                listSubtasks.add(listSubtask.get(idSubtask));
            }
            return listSubtasks;
        }
        return new ArrayList<>();
    }

    // Добавление подзадачи
    @Override
    public Subtask createSubtask(Subtask subtask) {
        Epic epic = listEpic.get(subtask.getIdEpic());
        if (epic != null) {
            int newId = getCounterId();
            subtask.setId(newId);
            ArrayList<Integer> newListSubtaskEpic = epic.getListSubtaskEpic();
            newListSubtaskEpic.add(subtask.getId());
            listSubtask.put(subtask.getId(), subtask);
            updateStatusEpic(subtask.getIdEpic());
            return subtask;
        } else {
            return null;
        }
    }

    // Добавление задачи
    @Override
    public Task createTask(Task task) {
        int newId = getCounterId();
        task.setId(newId);
        listTask.put(task.getId(), task);
        return task;
    }

    // Добавление эпической задачи
    @Override
    public Epic createEpic(Epic epic) {
        int newId = getCounterId();
        epic.setId(newId);
        listEpic.put(epic.getId(), epic);
        return epic;
    }

    // Обновление задачи по номеру Id задачи
    @Override
    public void updateTask(Task task) {
        listTask.put(task.getId(), task);
    }

    // Обновление подзадачи по номеру Id подзадачи
    @Override
    public void updateSubTask(Subtask subtask) {
        listSubtask.put(subtask.getId(), subtask);
        updateStatusEpic(subtask.getIdEpic());
    }

    // Обновление эпика по номеру Id эпика
    @Override
    public void updateEpic(Epic epic) {
        Epic oldEpic = listEpic.get(epic.getId());
        oldEpic.setName(epic.getName());
        oldEpic.setDescription(epic.getDescription());
    }

    // Обновление статуса эпической задачи
    protected void updateStatusEpic(int idEpic) {
        Epic epic = listEpic.get(idEpic);
        ArrayList<Subtask> epicSubtasks = getListSubtask(idEpic);
        int quantitySubtask = epicSubtasks.size();
        if (quantitySubtask == 0) {
            epic.setStatus(Status.NEW);
        } else {
            int countDone = 0;
            int countNEW = 0;
            for (int i = 0; i < quantitySubtask; i++) {
                if (epicSubtasks.get(i).getStatus() == Status.DONE) {
                    countDone++;
                } else if (epicSubtasks.get(i).getStatus() == Status.NEW) {
                    countNEW++;
                }
            }
            if (countDone == quantitySubtask) {
                epic.setStatus(Status.DONE);
            } else if ((countNEW == quantitySubtask)) {
                epic.setStatus(Status.NEW);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    // Вывод всех типов задач
    @Override
    public ArrayList<Task> fullListTask() {
        return new ArrayList<Task>(listTask.values());
    }

    @Override
    public ArrayList<Subtask> fullListSubtask() {
        return new ArrayList<Subtask>(listSubtask.values());
    }

    @Override
    public ArrayList<Epic> fullListEpic() {
        return new ArrayList<Epic>(listEpic.values());
    }

    // Вывод истории просмотров задач
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public int getCounterId() {
        return counterId++;
    }

    protected void setCounterId(int counterId) {
        this.counterId = counterId;
    }
}
