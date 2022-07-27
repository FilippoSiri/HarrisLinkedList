import java.util.concurrent.atomic.AtomicMarkableReference;

public class Node<E> {
    private final E val;
    private final AtomicMarkableReference<Node<E>> next;

    public Node(E val, Node<E> next){
        this.next = new AtomicMarkableReference<>(next, false);
        this.val = val;
    }

    public E getVal() {
        return val;
    }

    public AtomicMarkableReference<Node<E>> getNext() {
        return next;
    }

    public boolean isTail(){
        return next == null;
    }
}
