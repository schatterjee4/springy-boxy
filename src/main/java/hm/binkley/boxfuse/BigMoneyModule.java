package hm.binkley.boxfuse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.math.BigDecimal;

import static com.fasterxml.jackson.core.Version.unknownVersion;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

@Component
public class BigMoneyModule extends SimpleModule {

    public BigMoneyModule() {
        super(BigMoney.class.getName(),
                unknownVersion(),
                singletonMap(BigMoney.class, new BigMoneyJsonDeserializer()),
                singletonList(new BigMoneyJsonSerializer()));
    }

    public static class BigMoneyJsonDeserializer extends StdScalarDeserializer<BigMoney> {
        protected BigMoneyJsonDeserializer() {
            super(BigMoney.class);
        }

        @Override
        public BigMoney deserialize(final JsonParser json, final DeserializationContext context) throws IOException {
            final BigMoneyJson value = json.readValueAs(BigMoneyJson.class);
            return BigMoney.of(CurrencyUnit.of(value.getCurrency()),
                    value.getAmount());
        }
    }

    public static class BigMoneyJsonSerializer
            extends StdScalarSerializer<BigMoney> {
        protected BigMoneyJsonSerializer() {
            super(BigMoney.class);
        }

        @Override
        public void serialize(final BigMoney value, final JsonGenerator json,
                final SerializerProvider serializers)
                throws IOException {
            json.writeObject(
                    new BigMoneyJson(value.getCurrencyUnit().getCode(),
                            value.getAmount()));
        }
    }

    @EqualsAndHashCode
    @Getter
    @JsonPropertyOrder({"currency", "amount"})
    @ToString
    public static class BigMoneyJson {
        @Nonnull
        private final String currency;
        @Nonnull
        private final BigDecimal amount;

        @JsonCreator
        public BigMoneyJson(
                @JsonProperty("currency") @Nonnull final String currency,
                @JsonProperty("amount") @Nonnull final BigDecimal amount) {
            this.currency = currency;
            this.amount = amount;
        }
    }
}
