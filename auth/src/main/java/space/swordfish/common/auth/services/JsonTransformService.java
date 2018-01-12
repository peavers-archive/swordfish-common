package space.swordfish.common.auth.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.jasminb.jsonapi.DeserializationFeature;
import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import org.springframework.stereotype.Component;
import space.swordfish.common.auth.domain.User;

import java.util.Collection;
import java.util.List;

@Component
public class JsonTransformService {

    private ResourceConverter converter;

    JsonTransformService() {
        this.converter = resourceConverter();

        converter.disableDeserializationOption(DeserializationFeature.REQUIRE_RESOURCE_ID);
    }

    <T> T read(Class<T> cls, String payload) {
        return converter.readDocument(payload.getBytes(), cls).get();
    }

    <T> JSONAPIDocument<List<T>> readList(Class<T> cls, String payload) {
        return converter.readDocumentCollection(payload.getBytes(), cls);
    }

    String write(Object object) {
        JSONAPIDocument<Object> document = new JSONAPIDocument<>(object);

        byte[] bytes = new byte[0];
        try {
            bytes = converter.writeDocument(document);
        } catch (DocumentSerializationException e) {
            e.printStackTrace();
        }

        return new String(bytes);
    }

    String writeList(Iterable<?> iterable) {
        JSONAPIDocument<Collection<?>> listJSONAPIDocument = new JSONAPIDocument<>(
                (Collection<?>) iterable);

        byte[] bytes = new byte[0];
        try {
            bytes = converter.writeDocumentCollection(listJSONAPIDocument);
        } catch (DocumentSerializationException e) {
            e.printStackTrace();
        }

        return new String(bytes);
    }

    ResourceConverter resourceConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

        return new ResourceConverter(objectMapper, User.class);
    }
}
