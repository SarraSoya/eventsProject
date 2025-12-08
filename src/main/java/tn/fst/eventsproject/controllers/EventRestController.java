package tn.fst.eventsproject.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.fst.eventsproject.entities.Event;
import tn.fst.eventsproject.entities.Logistics;
import tn.fst.eventsproject.entities.Participant;
import tn.fst.eventsproject.services.IEventServices;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("event")
@RestController
public class EventRestController {

    private final IEventServices eventServices;

    @PostMapping("/addPart")
    public Participant addParticipant(@RequestBody Participant participant) {
        return eventServices.addParticipant(participant);
    }

    @PostMapping("/addEvent/{id}")
    public Event addEventPart(@RequestBody Event event,
                              @PathVariable("id") int participantId) {
        return eventServices.addAffectEvenParticipant(event, participantId);
    }

    @PostMapping("/addEvent")
    public Event addEvent(@RequestBody Event event) {
        return eventServices.addAffectEvenParticipant(event);
    }

    @PutMapping("/addAffectLog/{description}")
    public Logistics addAffectLog(@RequestBody Logistics logistics,
                                  @PathVariable("description") String eventDescription) {
        return eventServices.addAffectLog(logistics, eventDescription);
    }

    @GetMapping("/getLogs/{d1}/{d2}")
    public List<Logistics> getLogisticsBetweenDates(@PathVariable("d1") LocalDate startDate,
                                                    @PathVariable("d2") LocalDate endDate) {
        return eventServices.getLogisticsDates(startDate, endDate);
    }
}
