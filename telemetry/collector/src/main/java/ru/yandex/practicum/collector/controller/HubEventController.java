package ru.yandex.practicum.collector.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.collector.dto.hub.HubEventDto;
import ru.yandex.practicum.collector.mapper.HubEventMapper;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class HubEventController {

    private final HubEventMapper mapper;
    private final KafkaEventProducer producer;

    @PostMapping("/hubs")
    public ResponseEntity<Void> collectHubEvent(
            @Valid @RequestBody HubEventDto event
    ) {
        producer.sendHubEvent(mapper.toAvro(event));
        return ResponseEntity.ok().build();
    }
}
