package com.ehr.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Patient entity.
 *
 * Entity equality is by id only — two Patient objects with the same id
 * represent the same patient regardless of other field values.
 * This is intentional: if name/gender are updated in the future,
 * collections/sets holding Patient still behave correctly.
 */
public class Patient {

    private final String    id;
    private final String    name;
    private final String    gender;       // stored normalized lowercase
    private final LocalDate dateOfBirth;

    public Patient(String id, String name, String gender, LocalDate dateOfBirth) {
        this.id          = id;
        this.name        = name;
        this.gender      = gender;
        this.dateOfBirth = dateOfBirth;
    }

    public String    getId()          { return id;          }
    public String    getName()        { return name;        }
    public String    getGender()      { return gender;      }
    public LocalDate getDateOfBirth() { return dateOfBirth; }

    /**
     * Entity equality: only by id.
     * Justification: Patient is an aggregate root identified by its id.
     * Other fields are mutable state that should not affect identity.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;
        Patient other = (Patient) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Patient{id='" + id + "', name='" + name
             + "', gender='" + gender + "', dateOfBirth=" + dateOfBirth + "}";
    }
}
