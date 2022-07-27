public class HarrisLinkedList<E> {
    private record Window<E>(Node<E> pred, Node<E> curr){}
    private final Node<E> head;

    public HarrisLinkedList() {
        var tail = new Node<E>(null, null);
        this.head = new Node<>(null, tail);
    }

    private Window<E> find(E value){
        boolean[] marked = {false};
        Node<E> pred = head, curr = head, succ = head;

        //Phase 1
        do{
            if(!marked[0]) pred = curr;
            curr = succ;
            if(curr.isTail()) break;
            succ = curr.getNext().get(marked);
        } while(curr.getVal().hashCode() < value.hashCode() || marked[0]); //Sorted by HashCode

        //Phase 2
        var pred_next = pred.getNext().getReference();
        if(pred_next != curr){ //Try to remove marked nodes
            if(!pred_next.isTail()){
                pred_next.getNext().get(marked);
                if(!marked[0]) return find(value);
            }
            if(!pred.getNext().compareAndSet(pred_next, curr, false, false))
                return find(value); //if CAS fail we call find again
        }

        //Phase 3
        return new Window<>(pred, curr); //Marked nodes removed, pred is the curr's predecessor
    }

    /*public Window find2(int key) {
        Node<E> pred = null, curr = null, succ = null;
        boolean[] marked = {false}; boolean snip;

        retry:while (true) {
            pred = head;
            curr = pred.getNext().getReference();
            while (true) {
                succ = curr.getNext().get(marked);
                while (marked[0]) {
                    snip = pred.getNext().compareAndSet(curr,
                            succ, false, false);
                    if (!snip) continue retry;
                    curr = succ;
                    succ = curr.getNext().get(marked);
                }
                if (curr.getVal().hashCode() >= ((Integer)key).hashCode())
                    return new Window(pred, curr);
                pred = curr;
                curr = succ;
            }
        }
    }*/

    public boolean contains(E e) {
        Node<E> curr = find(e).curr();
        return !curr.isTail() && curr.getVal().equals(e);
    }

    public boolean add(E e) {
        Node<E> newNode = new Node<>(e, null);
        while (true) {
            Window<E> w = find(e);
            Node<E> pred = w.pred(), curr = w.curr();
            if(!curr.isTail() && curr.getVal().equals(e)) return false;
            newNode.getNext().set(curr, false);
            if(pred.getNext().compareAndSet(curr, newNode, false, false)) return true;
        }
    }

    public boolean remove(E e) {
        Node<E> pred, curr, succ;
        while (true) {
            Window<E> w = find(e);
            pred = w.pred();
            curr = w.curr();
            if(curr.isTail() || !curr.getVal().equals(e)) return false;
            succ = curr.getNext().getReference();
            if(curr.getNext().compareAndSet(succ, succ, false, false)) break;
        }
        if(!pred.getNext().compareAndSet(curr, succ, false, false)) find(curr.getVal());
        return true;
    }

/*
    public boolean remove2(E e){
        Boolean snip;
        while (true) {
            Window<E> window = find(e);
            Node<E> pred = window.getPred(), curr = window.getCurr();
            if (curr.getVal() != e) {
                return false;
            } else {
                Node<E> succ = curr.getNext().getReference();
                snip = curr.getNext().compareAndSet(succ, succ, false, true);
                if (!snip) continue;
                pred.getNext().compareAndSet(curr, succ, false, false);
                return true;
            }}
    }*/

}
