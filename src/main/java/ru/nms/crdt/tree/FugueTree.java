package ru.nms.crdt.tree;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class FugueTree {

    @Value("${server.port}")
    private String replicaID;

    @Getter
    private final SortedSet<InternalPosition> localState = Collections.synchronizedSortedSet(new TreeSet<>());


    public InternalPosition createBetween(InternalPosition a, InternalPosition b, char symbol) {
        if (localState.isEmpty()) {
            var newIntPos = new InternalPosition(replicaID, 0, null, true, 1, symbol);
            localState.add(newIntPos);
            return newIntPos;
        }
        boolean isAncestor = false;
        if (b != null) {
            if (a == null) isAncestor = true;
            else {
                if (b.getDepth() > a.getDepth()) {
                    InternalPosition ancestorOfB = b;
                    while (ancestorOfB.getDepth() > a.getDepth()) {
                        ancestorOfB = ancestorOfB.getParent();
                    }
                    if (Objects.equals(ancestorOfB, a)) isAncestor = true;
                }
            }
        }

        InternalPosition newIntPos;
        if (isAncestor) {
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
        newIntPos.setCounter(localState.size());
        localState.add(newIntPos);
        return newIntPos;
    }

    public void receive(InternalPosition receivedIntPos) {
        if (receivedIntPos.getParent() != null && !localState.contains(receivedIntPos.getParent())) {
            throw new RuntimeException("There is no such parent in local fugue tree");
        }
        localState.add(receivedIntPos);
    }
}
