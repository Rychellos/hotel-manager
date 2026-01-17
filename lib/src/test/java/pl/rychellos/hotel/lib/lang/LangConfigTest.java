package pl.rychellos.hotel.lib.lang;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

class LangConfigTest {

    @Test
    void getBaseNames_ShouldIdentifyCorrectPaths() {
        /// Given
        LangConfig config = new LangConfig(null);
        Resource res1 = new ByteArrayResource(new byte[0]) {
            @Override
            public String getFilename() {
                return "messages-core_en.properties";
            }
        };
        Resource res2 = new ByteArrayResource(new byte[0]) {
            @Override
            public String getFilename() {
                return "messages-auth.properties";
            }
        };

        /// When
        // Using reflection or making the method package-private for testing
        Set<String> names = ReflectionTestUtils.invokeMethod(config, "getBaseNames",
            (Object) new Resource[]{res1, res2});

        /// Then
        assertTrue(names.contains("classpath:messages-core"));
        assertTrue(names.contains("classpath:messages-auth"));
        assertTrue(names.contains("classpath:messages")); // The default override
    }
}
