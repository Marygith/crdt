package ru.nms.crdt.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nms.crdt.dto.InsertDto;
import ru.nms.crdt.dto.ReadyDto;
import ru.nms.crdt.dto.TextDto;
import ru.nms.crdt.service.CrdtService;
import ru.nms.crdt.tree.InternalPosition;

@RestController
@RequiredArgsConstructor
public class CrdtController {

    private final CrdtService crdtService;

    @PostMapping("/receive")
    public void receive(@RequestBody InternalPosition pos) {
        crdtService.receive(pos);
    }

    @PostMapping("/insert")
    public void insert(@RequestBody InsertDto insertDto) {
        crdtService.insert(insertDto.getPos(), insertDto.getSymb());
    }

    @GetMapping("/ready")
    public ReadyDto isReady() {
        return new ReadyDto(crdtService.isReady());
    }

    @GetMapping("/state")
    public TextDto getLocalText() {
        return new TextDto(crdtService.getLocalText());
    }

    @PostMapping("/clear")
    public void clear() {
        crdtService.clear();
    }
}
