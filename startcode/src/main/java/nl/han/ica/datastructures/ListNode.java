package nl.han.ica.datastructures;

public class ListNode<T> {
    public T data;
    public ListNode<T> next;

    public ListNode(T data) {
        this.data = data;
        this.next = null;
    }
}
