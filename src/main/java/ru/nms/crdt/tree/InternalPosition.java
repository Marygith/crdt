package ru.nms.crdt.tree;

import lombok.*;
import ru.nms.crdt.util.Pair;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class InternalPosition implements Comparable<InternalPosition> {
    private String sender;
    private int counter;
    private InternalPosition parent;
    private boolean leftChild;
    private int depth;

    private char symbol;

    @Override
    public int compareTo(@NonNull InternalPosition o) {

        InternalPosition a = this;
        InternalPosition b = o;

        if (this.equals(o)) return 0;

        Pair<String, Boolean> lastMove = null;
        while (a.depth > b.depth) {
            lastMove = new Pair<>("a", a.leftChild);
            a = a.parent;
        }
        while (b.depth > a.depth) {
            lastMove = new Pair<>("b", b.leftChild);
            b = b.parent;
        }

        if (a == b) {
            return (lastMove.getFirst().equals("a") ? 1 : -1) * (lastMove.getSecond() ? -1 : 1);
        }

        while (a.parent != b.parent) {
            a = a.parent;
            b = b.parent;
        }

        if (a.leftChild && !b.leftChild) return -1;
        else if (!a.leftChild && b.leftChild) return 1;
        else {
            if (a.sender.compareTo(b.sender) < 0) return -1;
            else if (a.sender.compareTo(b.sender) > 0) return 1;
            else return Integer.compare(a.counter, b.counter);
        }
    }
}
