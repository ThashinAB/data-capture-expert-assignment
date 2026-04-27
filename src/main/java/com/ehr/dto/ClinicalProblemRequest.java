package com.ehr.dto;

import com.ehr.model.Coding;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

/** Inbound payload for POST /api/patients/{id}/problems */
public class ClinicalProblemRequest {

    private Coding coding;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate onsetDate;

    public ClinicalProblemRequest() {}

    public Coding    getCoding()   { return coding;   }
    public LocalDate getOnsetDate(){ return onsetDate;}

    public void setCoding(Coding coding)        { this.coding = coding;     }
    public void setOnsetDate(LocalDate onsetDate){ this.onsetDate = onsetDate;}
}
