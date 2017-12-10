package space.swordfish.common.json.configuration;

import com.github.jasminb.jsonapi.ResourceConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.common.json.services.JsonTransformServiceImpl;

@Configuration
public class JsonTransformConfig {

    @Bean
    public JsonTransformService jsonTransformService() {
        ResourceConverter resourceConverter = new ResourceConverter();
        return new JsonTransformServiceImpl(resourceConverter);
    }

}