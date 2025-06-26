package manager;

import model.Task;
import model.Epic;
import model.Subtask;
import model.Status;

import java.time.Duration;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> listTask = new HashMap<>();
    protected HashMap<Integer, Subtask> listSubtask = new HashMap<>();
    protected HashMap<Integer, Epic> listEpic = new HashMap<>();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())));
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
            prioritizedTasks.remove(listTask.get(key));
        }
        listTask.clear();
    }

    @Override
    public void fullDelSubtask() {
        for (Integer key : listSubtask.keySet()) {
            historyManager.remove(key);
            prioritizedTasks.remove(listSubtask.get(key));
        }
        listSubtask.clear();
        for (Epic epic : listEpic.values()) {
            epic.getListSubtaskEpic().clear();
            epic.setStatus(Status.NEW);
            updatingTimeParametersEpic(epic.getId());
        }
    }

    @Override
    public void fullDelEpic() {
        for (Integer key : listSubtask.keySet()) {
            historyManager.remove(key);
            prioritizedTasks.remove(listSubtask.get(key));
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
            prioritizedTasks.remove(listTask.get(idDel));
        } else if (listSubtask.containsKey(idDel)) {
            Subtask subtaskToDelete = listSubtask.get(idDel);
            Epic epic = listEpic.get(subtaskToDelete.getIdEpic());
            ArrayList<Integer> newListSubtaskEpic = epic.getListSubtaskEpic();
            newListSubtaskEpic.remove((Integer) idDel);
            listSubtask.remove(idDel);
            prioritizedTasks.remove(listSubtask.get(idDel));
            updateStatusEpic(epic.getId());
            updatingTimeParametersEpic(epic.getId());

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
        if (epic != null && !intersect(subtask)) {
            int newId = getCounterId();
            subtask.setId(newId);
            ArrayList<Integer> newListSubtaskEpic = epic.getListSubtaskEpic();
            newListSubtaskEpic.add(subtask.getId());
            listSubtask.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);
            updateStatusEpic(subtask.getIdEpic());
            updatingTimeParametersEpic(subtask.getIdEpic());
            return subtask;
        } else if (epic != null && subtask.getDuration() == null) {
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
        if (!intersect(task)) {
            int newId = getCounterId();
            task.setId(newId);
            listTask.put(task.getId(), task);
            prioritizedTasks.add(task);
            return task;
        } else if (task.getDuration() == null) {
            int newId = getCounterId();
            task.setId(newId);
            listTask.put(task.getId(), task);
            return task;
        } else {
            return null;
        }
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
        Task oldTaskCopy = listTask.get(task.getId());
        deleteById(task.getId());
        if (!intersect(task)) {
            listTask.put(task.getId(), task);
            prioritizedTasks.add(task);
        } else if (task.getDuration() == null) {
            listTask.put(task.getId(), task);
        } else {
            listTask.put(oldTaskCopy.getId(), oldTaskCopy);
            prioritizedTasks.add(oldTaskCopy);
        }
    }

    // Обновление подзадачи по номеру Id подзадачи
    @Override
    public void updateSubTask(Subtask subtask) {
        Subtask oldSubtaskCopy = listSubtask.get(subtask.getId());
        deleteById(subtask.getId());
        if (!intersect(subtask)) {
            listSubtask.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);
            updateStatusEpic(subtask.getIdEpic());
            updatingTimeParametersEpic(subtask.getIdEpic());
        } else if (subtask.getDuration() == null) {
            listSubtask.put(subtask.getId(), subtask);
            updateStatusEpic(subtask.getIdEpic());
            updatingTimeParametersEpic(subtask.getIdEpic());
        } else {
            listSubtask.put(oldSubtaskCopy.getId(), oldSubtaskCopy);
            prioritizedTasks.add(oldSubtaskCopy);
            updateStatusEpic(subtask.getIdEpic());
            updatingTimeParametersEpic(subtask.getIdEpic());
        }
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

    // Расчитываем время длительности, начала и окончания выполнения эпической задачи
    protected void updatingTimeParametersEpic(int idEpic) {
        Epic epic = listEpic.get(idEpic);
        ArrayList<Subtask> epicSubtasks = getListSubtask(idEpic);
        int quantitySubtask = epicSubtasks.size();
        if (quantitySubtask == 0) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(null);
        } else {
            long minutes = 0;
            for (Subtask subtask : epicSubtasks) {
                if (subtask.getStartTime() != null) {
                    if (epic.getStartTime() == null) {
                        epic.setStartTime(subtask.getStartTime());
                    } else if (epic.getStartTime().isAfter(subtask.getStartTime())) {
                        epic.setStartTime(subtask.getStartTime());
                    }
                }
                if (subtask.getEndTime() != null) {
                    if (epic.getEndTime() == null) {
                        epic.setEndTime(subtask.getEndTime());
                    } else if (epic.getEndTime().isBefore(subtask.getEndTime())) {
                        epic.setEndTime(subtask.getEndTime());
                    }
                }
                if (subtask.getDuration() != null) {
                    minutes += subtask.getDuration().toMinutes();
                }
                epic.setDuration(Duration.ofMinutes(minutes));
            }
        }
    }

    // Вывод списка отсортированных задач
    public TreeSet<Task> getPrioritizedTasks() {
        return new TreeSet<>(prioritizedTasks);
    }

    /*// Сортировка задач по времени начала выполнения задач
    public void sortedTasksPrioritized() {
        List<Task> allTask = new ArrayList<>();
        allTask.addAll(fullListTask());
        allTask.addAll(fullListSubtask());
        prioritizedTasks = allTask.stream()
                .filter(task -> task.getStartTime() != null)
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(Task::getStartTime))));
    }*/

    // Проверка задач на пересечение по времени выполнения
    public boolean intersect(Task task) {
        if (task.getStartTime() == null && task.getDuration() != null) {
            return false;
        } else if (task.getStartTime() != null && task.getDuration() != null) {
            boolean isPositive = getPrioritizedTasks().stream()
                    .allMatch(taskInSet -> (taskInSet.getStartTime().isAfter(task.getEndTime()) ||
                            taskInSet.getEndTime().isBefore(task.getStartTime())));

            return !isPositive;
        } else {
            return true;
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
