package co.edu.javeriana.lms.integration;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StreamingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetVideoExistsWithRange() throws Exception {
        mockMvc.perform(get("/streaming/video/test.mp4")
                .header("Range", "bytes=0-1024"))
                .andExpect(status().isPartialContent())
                .andExpect(header().string("Content-Range", startsWith("bytes 0-1024")))
                .andExpect(header().string("Content-Type", "video/mp4"))
                .andExpect(header().string("Accept-Ranges", "bytes"));
    }

    @Test
    public void testGetVideoExistsWithoutRange() throws Exception {
        mockMvc.perform(get("/streaming/video/test.mp4"))
                .andExpect(status().isPartialContent())
                .andExpect(header().string("Content-Range", startsWith("bytes 0-")))
                .andExpect(header().string("Content-Type", "video/mp4"))
                .andExpect(header().string("Accept-Ranges", "bytes"));
    }

    @Test
    public void testGetVideoNotExists() throws Exception {
        mockMvc.perform(get("/streaming/video/notexists.mp4"))
                .andExpect(status().isNotFound());
    }
}
