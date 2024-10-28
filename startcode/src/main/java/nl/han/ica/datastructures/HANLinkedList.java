package nl.han.ica.datastructures;

public class HANLinkedList<T> implements IHANLinkedList<T> {

    private ListNode<T> head;
    private int size;

    public HANLinkedList() {
        head = new ListNode<>(null); // Header node zonder data
        size = 0;
    }

    @Override
    public void addFirst(T value) {
        ListNode<T> newNode = new ListNode<>(value);
        newNode.next = head.next;
        head.next = newNode;
        size++;
    }

    @Override
    public void clear() {
        head.next = null;
        size = 0;
    }

    @Override
    public void insert(int index, T value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        ListNode<T> newNode = new ListNode<>(value);
        ListNode<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        newNode.next = current.next;
        current.next = newNode;
        size++;
    }

    @Override
    public void delete(int pos) {
        if (pos < 0 || pos >= size) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        ListNode<T> current = head;
        for (int i = 0; i < pos; i++) {
            current = current.next;
        }
        current.next = current.next.next;
        size--;
    }

    @Override
    public T get(int pos) {
        if (pos < 0 || pos >= size) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        ListNode<T> current = head.next;
        for (int i = 0; i < pos; i++) {
            current = current.next;
        }
        return current.data;
    }

    @Override
    public void removeFirst() {
        if (size == 0) {
            throw new IllegalStateException("List is empty");
        }
        head.next = head.next.next;
        size--;
    }

    @Override
    public T getFirst() {
        if (size == 0) {
            throw new IllegalStateException("List is empty");
        }
        return head.next.data;
    }

    @Override
    public int getSize() {
        return size;
    }
}
