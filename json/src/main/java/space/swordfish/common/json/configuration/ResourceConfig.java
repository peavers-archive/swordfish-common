package space.swordfish.common.json.configuration;

import com.github.jasminb.jsonapi.ResourceConverter;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceConfig {

    public ResourceConverter resourceConverter() {
        return new ResourceConverter();
    }

}
