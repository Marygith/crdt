package ru.nms.crdt.tree;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.TreeSet;

@Component
public class FugueTree {

    @Value("${server.port}")
    private String replicaID;

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
                newIntPos = new InternalPosition(replicaID, 0, null, true, 1, symbol);
            } else {
                newIntPos = new InternalPosition(replicaID, 0, b, true, b.getDepth() + 1, symbol);
            }
        } else {
            if (a == null) {
                newIntPos = new InternalPosition(replicaID, 0, null, false, 1, symbol);
            } else {
                newIntPos = new InternalPosition(replicaID, 0, a, false, a.getDepth() + 1, symbol);
            }
        }
        localState.add(newIntPos);
        newIntPos.setCounter(localState.size());
        return newIntPos;
    }

    public void receive(InternalPosition receivedIntPos) {
        if (receivedIntPos.getParent() != null && !localState.contains(receivedIntPos.getParent()))
            throw new RuntimeException("Unknown parent!!");

        localState.add(receivedIntPos);
    }
}
