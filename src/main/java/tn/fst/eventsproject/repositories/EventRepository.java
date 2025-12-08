package tn.fst.eventsproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.fst.eventsproject.entities.Event;
import tn.fst.eventsproject.entities.Tache;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {

    Event findByDescription(String description);

    List<Event> findByDateDebutBetween(LocalDate startDate, LocalDate endDate);

    List<Event> findByParticipantsNomAndParticipantsPrenomAndParticipantsTache(
            String nom,
            String prenom,
            Tache tache
    );
}
