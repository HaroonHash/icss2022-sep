package nl.han.ica.datastructures;

public class HANStack<T> implements IHANStack<T> {
    private HANLinkedList<T> linkedList;

    public HANStack() {
        linkedList = new HANLinkedList<>();
    }

    @Override
    public void push(T value) {
        linkedList.addFirst(value);
    }

    @Override
    public T pop() {
        T topValue = linkedList.getFirst();
        linkedList.removeFirst();
        return topValue;
    }

    @Override
    public T peek() {
        return linkedList.getFirst();
    }
}
