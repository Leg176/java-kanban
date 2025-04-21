package manager;

import model.Task;
import model.Epic;
import model.Subtask;
import model.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private HashMap<Integer, Task> listTask= new HashMap<>();
    private HashMap<Integer, Subtask> listSubtask= new HashMap<>();
    private HashMap<Integer, Epic> listEpic= new HashMap<>();
    private int counterId = 1;

    // Вывод задачи по номеру Id
    public Task getTaskById(int outputTaskId) {
        return listTask.get(outputTaskId);
    }

    public Subtask getSubtaskById(int outputSubtaskId) {
        return listSubtask.get(outputSubtaskId);
    }

    public Epic getEpicById(int outputEpicId) {
        return listEpic.get(outputEpicId);
    }

    // Удаление задач
    public void fullDelTask() {
        listTask.clear();
    }

    public void fullDelSubtask() {
        listSubtask.clear();
        for (Epic epic : listEpic.values()) {
            epic.getListSubtaskEpic().clear();
            epic.setStatus(Status.NEW);
        }
    }

    public void fullDelEpic() {
        listSubtask.clear();
        listEpic.clear();
    }

    // Удаление задачи по номеру Id
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
    }

    // Вывод подзадач по номеру эпичной задачи
    public ArrayList<Subtask> getListSubtask (int idEpic) {
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
    public Task createTask(Task task) {
        int newId = getCounterId();
        task.setId(newId);
        listTask.put(task.getId(), task);
        return task;
    }

    // Добавление эпической задачи
    public Epic createEpic(Epic epic) {
        int newId = getCounterId();
        epic.setId(newId);
        listEpic.put(epic.getId(), epic);
        return epic;
    }

    // Обновление задачи по номеру Id задачи
    public void updateTask(Task task) {
        listTask.put(task.getId(), task);
    }

    // Обновление подзадачи по номеру Id подзадачи
    public void updateSubTask(Subtask subtask) {
        listSubtask.put(subtask.getId(), subtask);
        updateStatusEpic(subtask.getIdEpic());
    }

    // Обновление эпика по номеру Id эпика
    public void updateEpic(Epic epic) {
        Epic oldEpic = listEpic.get(epic.getId());
        oldEpic.setName(epic.getName());
        oldEpic.setDescription(epic.getDescription());
    }

    // Обновление статуса эпической задачи
    protected void updateStatusEpic(int idEpic) {
        Epic epic = listEpic.get(idEpic);
        ArrayList<Subtask> epicSubtasks =  getListSubtask(idEpic);
        int quantitySubtask = epicSubtasks.size();
            if (quantitySubtask == 0) {
                epic.setStatus(Status.NEW);
            } else if (quantitySubtask == 1) {
                epic.setStatus(listSubtask.get(epicSubtasks.get(0)).getStatus());
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
    public ArrayList<Task> fullListTask() {
        return new ArrayList<Task>(listTask.values());
    }

    public ArrayList<Subtask> fullListSubtask() {
        return new ArrayList<Subtask>(listSubtask.values());
    }

    public ArrayList<Epic> fullListEpic() {
        return new ArrayList<Epic>(listEpic.values());
    }

    private int getCounterId() {
        return counterId++;
    }

    protected void setCounterId(int counterId) {
        this.counterId = counterId;
    }
}
