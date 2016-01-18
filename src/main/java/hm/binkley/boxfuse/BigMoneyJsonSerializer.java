package hm.binkley.boxfuse;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.money.BigMoney;

import java.io.IOException;

public class BigMoneyJsonSerializer
        extends JsonSerializer<BigMoney> {
    @Override
    public void serialize(final BigMoney value, final JsonGenerator json,
            final SerializerProvider serializers)
            throws IOException {
        json.writeObject(new JsonBigMoney(value.getCurrencyUnit().getCode(),
                value.getAmount()));
    }
}
