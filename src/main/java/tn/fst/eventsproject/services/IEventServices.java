package tn.fst.eventsproject.services;

import tn.fst.eventsproject.entities.Event;
import tn.fst.eventsproject.entities.Logistics;
import tn.fst.eventsproject.entities.Participant;

import java.time.LocalDate;
import java.util.List;

public interface IEventServices {

    Participant addParticipant(Participant participant);

    Event addAffectEvenParticipant(Event event, int participantId);

    Event addAffectEvenParticipant(Event event);

    Logistics addAffectLog(Logistics logistics, String eventDescription);

    List<Logistics> getLogisticsDates(LocalDate startDate, LocalDate endDate);

    void calculCout();
}
