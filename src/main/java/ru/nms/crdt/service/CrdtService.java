package ru.nms.crdt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nms.crdt.tree.FugueTree;
import ru.nms.crdt.tree.InternalPosition;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrdtService {

    private final FugueTree fugueTree;
    private final BroadcastService broadcastService;

    public void receive(InternalPosition internalPosition) {
        log.info("Received new position: {}", internalPosition);
        fugueTree.receive(internalPosition);
        logCurrentState();
    }

    public void insert(int pos, char symb) {
        if (pos < 0) throw new RuntimeException("Position must not be negative!");
        InternalPosition inserted;
        if (pos >= fugueTree.getLocalState().size()) {
            log.info("Inserting {} as last symbol", symb);
            if (fugueTree.getLocalState().isEmpty()) inserted = insert(null, null, symb);
            else inserted = addLast(symb);
        } else if (pos == 0) {
            log.info("Inserting {} as first symbol", symb);

            inserted = addFirst(symb);
        } else {
            InternalPosition[] arrayView = fugueTree.getLocalState().toArray(InternalPosition[]::new);
            log.info("Inserting {} at position: {}, between {} and {}", symb, pos, arrayView[pos - 1].getSymbol(), arrayView[pos].getSymbol());

            inserted = insert(arrayView[pos - 1], arrayView[pos], symb);
        }

        logCurrentState();
        broadcastService.send(inserted);

    }


    private InternalPosition addFirst(char symb) {
        return fugueTree.createBetween(null, fugueTree.getLocalState().first(), symb);
    }

    private InternalPosition addLast(char symb) {
        return fugueTree.createBetween(fugueTree.getLocalState().last(), null, symb);

    }

    private InternalPosition insert(InternalPosition a, InternalPosition b, char symb) {
        return fugueTree.createBetween(a, b, symb);
    }

    private void logCurrentState() {
        log.info("Updated text: {}", fugueTree.getLocalState().stream().map(InternalPosition::getSymbol).map(Object::toString).collect(Collectors.joining("")));
    }
}
