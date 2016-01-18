package hm.binkley.boxfuse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.joda.money.BigMoney;

import javax.annotation.Nonnull;
import java.io.IOException;

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

    public static class BigMoneyJsonSerializer
            extends JsonSerializer<BigMoney> {
        @Override
        public void serialize(final BigMoney value, final JsonGenerator json,
                final SerializerProvider serializers)
                throws IOException {
            json.writeString(value.toString());
        }
    }

    public static class BigMoneyJsonDeserializer
            extends JsonDeserializer<BigMoney> {
        @Override
        public BigMoney deserialize(final JsonParser json,
                final DeserializationContext context)
                throws IOException {
            return BigMoney.parse(json.getText());
        }
    }
}
