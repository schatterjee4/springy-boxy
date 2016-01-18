package hm.binkley.boxfuse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.math.BigDecimal;

@EqualsAndHashCode
@Getter
@JsonPropertyOrder({"currency", "amount"})
@ToString
public class JsonBigMoney {
    @Nonnull
    private final String currency;
    @Nonnull
    private final BigDecimal amount;

    @JsonCreator
    public JsonBigMoney(
            @JsonProperty("currency") @Nonnull final String currency,
            @JsonProperty("amount") @Nonnull final BigDecimal amount) {
        this.currency = currency;
        this.amount = amount;
    }
}
