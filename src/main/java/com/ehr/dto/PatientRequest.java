package com.ehr.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

/** Inbound payload for POST /api/patients */
public class PatientRequest {

    private String    name;
    private String    gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    public PatientRequest() {}

    public String    getName()        { return name;        }
    public String    getGender()      { return gender;      }
    public LocalDate getDateOfBirth() { return dateOfBirth; }

    public void setName(String name)               { this.name = name;               }
    public void setGender(String gender)           { this.gender = gender;           }
    public void setDateOfBirth(LocalDate dob)      { this.dateOfBirth = dob;         }
}
