package space.swordfish.common.auth.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.jasminb.jsonapi.ResourceConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.swordfish.common.auth.domain.User;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.common.json.services.JsonTransformServiceImpl;

@Configuration
public class AuthConfig {

    @Bean
    public JsonTransformService jsonTransformService() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

        ResourceConverter resourceConverter = new ResourceConverter(objectMapper,
                User.class);

        return new JsonTransformServiceImpl(resourceConverter);
    }

    @Bean
    public ResourceConverter resourceConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

        return new ResourceConverter(objectMapper, User.class);
    }

}