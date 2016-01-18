package hm.binkley.boxfuse;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import java.io.IOException;

public class BigMoneyJsonDeserializer
        extends JsonDeserializer<BigMoney> {
    @Override
    public BigMoney deserialize(final JsonParser json,
            final DeserializationContext context)
            throws IOException {
        final JsonBigMoney value = json.readValueAs(JsonBigMoney.class);
        return BigMoney
                .of(CurrencyUnit.of(value.getCurrency()), value.getAmount());
    }
}
