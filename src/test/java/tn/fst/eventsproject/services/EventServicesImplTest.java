package tn.fst.eventsproject.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.fst.eventsproject.entities.Event;
import tn.fst.eventsproject.entities.Logistics;
import tn.fst.eventsproject.entities.Participant;
import tn.fst.eventsproject.entities.Tache;
import tn.fst.eventsproject.repositories.EventRepository;
import tn.fst.eventsproject.repositories.LogisticsRepository;
import tn.fst.eventsproject.repositories.ParticipantRepository;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServicesImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private LogisticsRepository logisticsRepository;

    @InjectMocks
    private EventServicesImpl eventServices;

    // 🔹 Test pour: Participant addParticipant(Participant participant)
    @Test
    void addParticipant_shouldSaveAndReturnParticipant() {
        Participant participant = new Participant();

        when(participantRepository.save(participant)).thenReturn(participant);

        Participant result = eventServices.addParticipant(participant);

        assertSame(participant, result);
        verify(participantRepository).save(participant);
    }

    // 🔹 Test pour: Event addAffectEvenParticipant(Event event, int idParticipant)
    @Test
    void addAffectEvenParticipant_withId_shouldAddEventToParticipantAndSaveEvent() {
        Event event = new Event();
        Participant participantFromDb = mock(Participant.class);

        when(participantRepository.findById(1)).thenReturn(Optional.of(participantFromDb));
        // branche où participant.getEvents() == null
        when(participantFromDb.getEvents()).thenReturn(null);
        when(eventRepository.save(event)).thenReturn(event);

        Event result = eventServices.addAffectEvenParticipant(event, 1);

        assertSame(event, result);
        verify(eventRepository).save(event);

        // on capture l'ensemble d'events passé à setEvents()
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Set<Event>> captor = ArgumentCaptor.forClass(Set.class);
        verify(participantFromDb).setEvents(captor.capture());
        Set<Event> eventsSet = captor.getValue();
        assertTrue(eventsSet.contains(event));
    }

    // 🔹 Test pour: Event addAffectEvenParticipant(Event event)
    @Test
    void addAffectEvenParticipant_withoutId_shouldAddEventToAllParticipantsAndSaveEvent() {
        Event event = mock(Event.class);
        Participant aParticipant = mock(Participant.class);
        Participant participantFromDb = mock(Participant.class);

        when(aParticipant.getIdPart()).thenReturn(1);
        Set<Participant> participants = new HashSet<>();
        participants.add(aParticipant);
        when(event.getParticipants()).thenReturn(participants);

        when(participantRepository.findById(1)).thenReturn(Optional.of(participantFromDb));
        when(participantFromDb.getEvents()).thenReturn(null);
        when(eventRepository.save(event)).thenReturn(event);

        Event result = eventServices.addAffectEvenParticipant(event);

        assertSame(event, result);
        verify(eventRepository).save(event);
        verify(participantFromDb).setEvents(anySet());
    }

    // 🔹 Test pour: Logistics addAffectLog(Logistics logistics, String descriptionEvent)
    @Test
    void addAffectLog_shouldAttachLogisticsToEventAndSave() {
        Logistics logistics = new Logistics();
        Event event = mock(Event.class);

        when(eventRepository.findByDescription("Conférence")).thenReturn(event);
        // branche où event.getLogistics() == null
        when(event.getLogistics()).thenReturn(null);
        when(logisticsRepository.save(logistics)).thenReturn(logistics);

        Logistics result = eventServices.addAffectLog(logistics, "Conférence");

        assertSame(logistics, result);
        verify(eventRepository).findByDescription("Conférence");
        verify(event).setLogistics(anySet());
        verify(logisticsRepository).save(logistics);
    }

    // 🔹 Test pour: List<Logistics> getLogisticsDates(LocalDate startDate, LocalDate endDate)
    @Test
    void getLogisticsDates_shouldReturnOnlyReservedLogistics() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);

        Event event = mock(Event.class);
        Logistics reserved = mock(Logistics.class);
        Logistics notReserved = mock(Logistics.class);

        when(reserved.isReserve()).thenReturn(true);
        when(notReserved.isReserve()).thenReturn(false);

        Set<Logistics> logisticsSet = new HashSet<>(Arrays.asList(reserved, notReserved));
        when(event.getLogistics()).thenReturn(logisticsSet);

        List<Event> events = List.of(event);
        when(eventRepository.findByDateDebutBetween(start, end)).thenReturn(events);

        List<Logistics> result = eventServices.getLogisticsDates(start, end);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(reserved));
        assertFalse(result.contains(notReserved));
    }

    // 🔹 Test pour: void calculCout()
    @Test
    void calculCout_shouldComputeCostOfReservedLogisticsAndSaveEvents() {
        Event event = mock(Event.class);
        Logistics log1 = mock(Logistics.class);
        Logistics log2 = mock(Logistics.class);

        when(log1.isReserve()).thenReturn(true);
        when(log1.getPrixUnit()).thenReturn(10f);
        when(log1.getQuantite()).thenReturn(2);

        when(log2.isReserve()).thenReturn(false);

        Set<Logistics> logisticsSet = new HashSet<>(Arrays.asList(log1, log2));
        when(event.getLogistics()).thenReturn(logisticsSet);
        when(event.getDescription()).thenReturn("Test Event");

        List<Event> events = List.of(event);
        when(eventRepository.findByParticipantsNomAndParticipantsPrenomAndParticipantsTache(
                "Tounsi", "Ahmed", Tache.ORGANISATEUR)).thenReturn(events);

        eventServices.calculCout();

        // somme attendue = 10 * 2 = 20
        verify(event).setCout(20f);
        verify(eventRepository).save(event);
    }
}
