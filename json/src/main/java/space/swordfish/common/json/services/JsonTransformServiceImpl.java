package space.swordfish.common.json.services;

import com.github.jasminb.jsonapi.DeserializationFeature;
import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class JsonTransformServiceImpl implements JsonTransformService {

    private ResourceConverter converter;

    public JsonTransformServiceImpl(ResourceConverter converter) {
        this.converter = converter;

        converter.disableDeserializationOption(DeserializationFeature.REQUIRE_RESOURCE_ID);
    }

    @Override
    public <T> T read(Class<T> cls, String payload) {
        return converter.readDocument(payload.getBytes(), cls).get();
    }

    @Override
    public <T> JSONAPIDocument<List<T>> readList(Class<T> cls, String payload) {
        return converter.readDocumentCollection(payload.getBytes(), cls);
    }

    @Override
    public String write(Object object) {
        JSONAPIDocument<Object> document = new JSONAPIDocument<>(object);

        byte[] bytes = new byte[0];
        try {
            bytes = converter.writeDocument(document);
        } catch (DocumentSerializationException e) {
            e.printStackTrace();
        }

        return new String(bytes);
    }

    @Override
    public String writeList(Iterable<?> iterable) {
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
}
