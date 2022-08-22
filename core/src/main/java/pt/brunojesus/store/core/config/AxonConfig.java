package pt.brunojesus.store.core.config;

import com.thoughtworks.xstream.XStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {

    @Bean
    public XStream xStream() {
        XStream xStream = new XStream();

        xStream.allowTypesByWildcard(new String[] { "pt.brunojesus.store.**" });
        return xStream;
    }
}
