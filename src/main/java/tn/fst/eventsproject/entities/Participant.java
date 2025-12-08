package tn.fst.eventsproject.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Participant implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPart;

    private String nom;
    private String prenom;

    @Enumerated(EnumType.STRING)
    private Tache tache;

    @ManyToMany
    private Set<Event> events = new HashSet<>();
}
