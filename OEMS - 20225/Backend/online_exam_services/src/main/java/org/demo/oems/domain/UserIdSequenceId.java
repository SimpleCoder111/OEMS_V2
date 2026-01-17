package org.demo.oems.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class UserIdSequenceId implements Serializable {

    private String roleCode;
    private Integer year;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserIdSequenceId)) return false;
        UserIdSequenceId that = (UserIdSequenceId) o;
        return Objects.equals(roleCode, that.roleCode)
                && Objects.equals(year, that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleCode, year);
    }
}
