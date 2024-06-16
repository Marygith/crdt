package ru.nms.crdt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nms.crdt.tree.FugueTree;
import ru.nms.crdt.tree.InternalPosition;

@Service
@RequiredArgsConstructor
public class CrdtService {

    private final FugueTree fugueTree;
    private final BroadcastService broadcastService;

    public void receive(InternalPosition internalPosition) {
        fugueTree.receive(internalPosition);
    }

    public void insert(int pos, char symb) {

        InternalPosition inserted;
        if (pos >= fugueTree.getLocalState().size()) {
            inserted = addLast(symb);
        } else if (pos == 0) {
            inserted = addFirst(symb);
        } else {
            InternalPosition[] arrayView = fugueTree.getLocalState().toArray(InternalPosition[]::new);
            inserted = insert(arrayView[pos - 1], arrayView[pos], symb);
        }
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
}
