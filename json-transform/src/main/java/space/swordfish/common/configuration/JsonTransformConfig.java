package space.swordfish.common.configuration;

import com.github.jasminb.jsonapi.ResourceConverter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.swordfish.common.services.JsonTransformService;
import space.swordfish.common.services.JsonTransformServiceImpl;

@Configuration
public class JsonTransformConfig {

    @Bean
    public JsonTransformService jsonTransformService() {
        ResourceConverter resourceConverter = new ResourceConverter();
        return new JsonTransformServiceImpl(resourceConverter);
    }

}