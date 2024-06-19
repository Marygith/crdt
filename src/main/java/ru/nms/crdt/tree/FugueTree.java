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


    public synchronized InternalPosition createBetween(InternalPosition a, InternalPosition b, char symbol) {
        boolean isAnc = false;
        if (b != null) {
            if (a == null) isAnc = true;
            else {
                if (b.getDepth() > a.getDepth()) {
                    InternalPosition bAnc = b;
                    while (bAnc.getDepth() > a.getDepth()) {
                        bAnc = bAnc.getParent();
                    }
                    if (Objects.equals(bAnc, a)) isAnc = true;
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
        newIntPos.setCounter(localState.size());
        localState.add(newIntPos);
        return newIntPos;
    }

    public void receive(InternalPosition receivedIntPos) {
        if (receivedIntPos.getParent() != null && !localState.contains(receivedIntPos.getParent())) {
            var pos = receivedIntPos.getParent();

            log.info("Starting comparing!!!\n");
            List<InternalPosition> posList = localState.stream().toList();
            for (int i = 0; i < posList.size() - 1; i++) {
                log.info("elem {} compareTo elem {} is {}", i, i+1, posList.get(i).compareTo(posList.get(i + 1)));
                log.info("elem {} compareTo elem {} is {}", i+1, i, posList.get(i+1).compareTo(posList.get(i)));
            }
            log.info("Checking equals!!!\n");
            for (int i = 0; i < posList.size(); i++) {
                for (int j = 0; j < posList.size(); j++) {
                    log.info("elem {} compareTo elem {} is {}, equals is {}", i, j, posList.get(i).compareTo(posList.get(j)), posList.get(i).equals(posList.get(j)));
                }
            }
            throw new RuntimeException("Unknown parent!!");
        }
        localState.add(receivedIntPos);
    }
}
