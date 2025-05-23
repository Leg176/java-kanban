package manager;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node> listViewedTask = new HashMap<>();

    public void removeNode(Node node) {
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        size--;
    }

    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        Task copyTask = new Task(task.getName(), task.getDescription(), task.getStatus(), task.getId());
        if (listViewedTask.containsKey(copyTask.getId())) {
            Node node = listViewedTask.get(copyTask.getId());
            removeNode(node);
        }
        linkLast(copyTask);
        listViewedTask.put(copyTask.getId(), tail);
    }

    @Override
    public void remove(int id) {
        if (!listViewedTask.containsKey(id)) {
            return;
        } else {
            Node nodeTask = listViewedTask.get(id);
            removeNode(nodeTask);
            listViewedTask.remove(id);
        }
    }

    private Node head;
    private Node tail;
    private int size = 0;

    private class Node {

        public Task task;
        public Node next;
        public Node prev;

        public Node(Task task) {
            this.task = task;
            this.next = null;
            this.prev = null;
        }

    }

    public void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(task);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.prev = oldTail;
            tail = newNode;
            oldTail.next = tail;
        }
        size++;
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node node = head;
        while (node != null) {
            tasks.add(node.task);
            node = node.next;
        }
        return tasks;
    }
}
