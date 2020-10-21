package com.smalaca.taskamanager.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Codename {
    private String shortName;
    private String fullName;

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Codename codename = (Codename) o;

        return new EqualsBuilder()
                .append(shortName, codename.shortName)
                .append(fullName, codename.fullName)
                .isEquals();
    }

    @Override
    @SuppressWarnings("MagicNumber")
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(shortName)
                .append(fullName)
                .toHashCode();
    }
}
