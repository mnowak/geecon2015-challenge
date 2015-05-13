package pl.allegro.promo.geecon2015.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class ReportedUser {

    private final UUID id;

    private final String name;

    private final BigDecimal transactionsAmount;

    public ReportedUser(UUID id, String name, BigDecimal transactionsAmount) {
        this.id = id;
        this.name = name;
        this.transactionsAmount = transactionsAmount;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getTransactionsAmount() {
        return transactionsAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReportedUser)) return false;

        ReportedUser that = (ReportedUser) o;
        return Objects.equals(transactionsAmount, that.transactionsAmount)
                && Objects.equals(id, that.id)
                && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, transactionsAmount);
    }

    @Override
    public String toString() {
        return String.format("[%s %s %s]", getId(), getName(), getTransactionsAmount());
    }
}
