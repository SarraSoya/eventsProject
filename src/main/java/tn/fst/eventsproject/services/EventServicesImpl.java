package tn.fst.eventsproject.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.fst.eventsproject.entities.Event;
import tn.fst.eventsproject.entities.Logistics;
import tn.fst.eventsproject.entities.Participant;
import tn.fst.eventsproject.entities.Tache;
import tn.fst.eventsproject.repositories.EventRepository;
import tn.fst.eventsproject.repositories.LogisticsRepository;
import tn.fst.eventsproject.repositories.ParticipantRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventServicesImpl implements IEventServices {

    private static final String NOT_FOUND_SUFFIX = " not found";

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final LogisticsRepository logisticsRepository;

    // ---------- Méthodes utilitaires ----------

    private void attachEventToParticipant(Event event, Participant participant) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        if (participant == null) {
            throw new IllegalArgumentException("participant must not be null");
        }

        Set<Event> events = participant.getEvents();
        if (events == null) {
            events = new HashSet<>();
            participant.setEvents(events);
        }
        events.add(event);

        participantRepository.save(participant);
    }

    // ---------- Implémentation de IEventServices ----------

    @Override
    public Participant addParticipant(Participant participant) {
        if (participant == null) {
            throw new IllegalArgumentException("participant must not be null");
        }
        return participantRepository.save(participant);
    }

    @Override
    public Event addAffectEvenParticipant(Event event, int participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Participant with id " + participantId + NOT_FOUND_SUFFIX));

        attachEventToParticipant(event, participant);

        Set<Participant> participants = event.getParticipants();
        if (participants == null) {
            participants = new HashSet<>();
            event.setParticipants(participants);
        }
        participants.add(participant);

        return eventRepository.save(event);
    }

    @Override
    public Event addAffectEvenParticipant(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }

        Set<Participant> participants = event.getParticipants();
        if (participants == null || participants.isEmpty()) {
            return eventRepository.save(event);
        }

        Set<Participant> managedParticipants = new HashSet<>();

        for (Participant p : participants) {
            Participant managedParticipant = participantRepository.findById(p.getIdPart())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Participant with id " + p.getIdPart() + NOT_FOUND_SUFFIX));

            attachEventToParticipant(event, managedParticipant);
            managedParticipants.add(managedParticipant);
        }

        event.setParticipants(managedParticipants);

        return eventRepository.save(event);
    }

    @Override
    public Logistics addAffectLog(Logistics logistics, String eventDescription) {
        if (logistics == null) {
            throw new IllegalArgumentException("logistics must not be null");
        }

        Event event = eventRepository.findByDescription(eventDescription);
        if (event == null) {
            throw new IllegalArgumentException(
                    "Event with description " + eventDescription + NOT_FOUND_SUFFIX);
        }

        Set<Logistics> logisticsSet = event.getLogistics();
        if (logisticsSet == null) {
            logisticsSet = new HashSet<>();
            event.setLogistics(logisticsSet);
        }
        logisticsSet.add(logistics);

        logisticsRepository.save(logistics);
        eventRepository.save(event);

        return logistics;
    }

    @Override
    public List<Logistics> getLogisticsDates(LocalDate startDate, LocalDate endDate) {
        List<Event> events = eventRepository.findByDateDebutBetween(startDate, endDate);

        if (events == null || events.isEmpty()) {
            return new ArrayList<>();
        }

        List<Logistics> logisticsList = new ArrayList<>();

        for (Event event : events) {
            Set<Logistics> logisticsSet = event.getLogistics();
            if (logisticsSet == null || logisticsSet.isEmpty()) {
                continue;
            }

            for (Logistics logistics : logisticsSet) {
                if (logistics.isReserve()) {
                    logisticsList.add(logistics);
                }
            }
        }

        return logisticsList;
    }

    @Scheduled(cron = "*/60 * * * * *")
    @Override
    public void calculCout() {
        List<Event> events = eventRepository
                .findByParticipantsNomAndParticipantsPrenomAndParticipantsTache(
                        "Tounsi", "Ahmed", Tache.ORGANISATEUR
                );

        if (events == null || events.isEmpty()) {
            return;
        }

        for (Event event : events) {
            float somme = 0f;

            Set<Logistics> logisticsSet = event.getLogistics();
            if (logisticsSet != null && !logisticsSet.isEmpty()) {
                for (Logistics logistics : logisticsSet) {
                    if (logistics.isReserve()) {
                        somme += logistics.getPrixUnit() * logistics.getQuantite();
                    }
                }
            }

            event.setCout(somme);
            eventRepository.save(event);
            log.info("Cout de l'Event {} est {}", event.getDescription(), somme);
        }
    }
}
