package hm.binkley.boxfuse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.joda.money.BigMoney;

import javax.annotation.Nonnull;

@EqualsAndHashCode
@Getter
@JsonPropertyOrder({"id", "content", "value"})
@ToString
public class Greeting {
    private final long id;
    @Nonnull
    private final String content;
    @Nonnull
    private final BigMoney value;

    @JsonCreator
    public Greeting(@JsonProperty("id") final long id,
            @JsonProperty("content") @Nonnull final String content,
            @JsonProperty("value")
            @JsonDeserialize(using = BigMoneyJsonDeserializer.class) @Nonnull
            final BigMoney value) {
        this.id = id;
        this.content = content;
        this.value = value;
    }

    @JsonSerialize(using = BigMoneyJsonSerializer.class)
    @Nonnull
    public BigMoney getValue() {
        return value;
    }
}
