package ru.nms.crdt.tree;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.TreeSet;
import java.util.UUID;

@Component
public class FugueTree {

    private final String replicaID = UUID.randomUUID().toString();

    @Getter
    private final TreeSet<InternalPosition> localState = new TreeSet<>();

    public InternalPosition createBetween(InternalPosition a, InternalPosition b, char symbol) {
        boolean isAnc = false;
        if (b != null) {
            if (a == null) isAnc = true;
            else {
                if (b.getDepth() > a.getDepth()) {
                    InternalPosition bAnc = b;
                    while (bAnc.getDepth() > a.getDepth()) {
                        bAnc = bAnc.getParent();
                    }
                    if (bAnc == a) isAnc = true;
                }
            }
        }

        InternalPosition newIntPos;
        if (isAnc) {
            if (a == null) {
                newIntPos = new InternalPosition(replicaID, localState.size(), null, true, 1, symbol);
            } else {
                newIntPos = new InternalPosition(replicaID, localState.size(), b, true, b.getDepth() + 1, symbol);
            }
        } else {
            if (a == null) {
                newIntPos = new InternalPosition(replicaID, localState.size(), null, false, 1, symbol);
            } else {
                newIntPos = new InternalPosition(replicaID, localState.size(), a, false, a.getDepth() + 1, symbol);
            }
        }
        localState.add(newIntPos);
        return newIntPos;
    }

    public void receive(InternalPosition receivedIntPos) {
        localState.add(receivedIntPos);
    }
}
