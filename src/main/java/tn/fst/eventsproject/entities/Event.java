package tn.fst.eventsproject.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Event implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idEvent;

    private String description;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private float cout;

    @ManyToMany(mappedBy = "events")
    private Set<Participant> participants = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Logistics> logistics = new HashSet<>();
}
