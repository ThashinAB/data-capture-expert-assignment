package com.ehr.model;

import java.util.Objects;

/**
 * Immutable value object representing a clinical coding triple.
 * Treated as a value type — equality is by all three fields, not identity.
 *
 * No setters intentionally: once created, a Coding is fixed.
 * This prevents accidental mutation after SNOMED validation has passed.
 */
public class Coding {

    private final String system;
    private final String code;
    private final String display;

    public Coding(String system, String code, String display) {
        this.system  = system;
        this.code    = code;
        this.display = display;
    }

    public String getSystem()  { return system;  }
    public String getCode()    { return code;    }
    public String getDisplay() { return display; }

    /**
     * Value-object equality: two Codings are equal when all three fields match.
     * This lets us detect duplicate problem entries if needed in the future.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coding)) return false;
        Coding other = (Coding) o;
        return Objects.equals(system,  other.system)
            && Objects.equals(code,    other.code)
            && Objects.equals(display, other.display);
    }

    @Override
    public int hashCode() {
        return Objects.hash(system, code, display);
    }

    @Override
    public String toString() {
        return "Coding{system='" + system + "', code='" + code + "', display='" + display + "'}";
    }
}
