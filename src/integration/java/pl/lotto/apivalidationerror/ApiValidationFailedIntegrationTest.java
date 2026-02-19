package pl.lotto.apivalidationerror;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import pl.lotto.BaseIntegrationTest;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApiValidationFailedIntegrationTest extends BaseIntegrationTest {

    @Test
    public void should_return_bad_request_and_validation_messages_when_client_sends_empty_request_input_data() throws Exception {
        //given
        String postInputNumbersPath = "/inputNumbers";

        //when
        mockMvc.perform(post(postInputNumbersPath)
                        .content(
                                """
                                        {
                                         "inputNumbers": []
                                        }
                                        """.trim()
                        ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessages").value(contains("inputNumbers must not be empty")));
    }

    @Test
    public void should_return_bad_request_and_validation_messages_when_client_sends_no_request_input_data() throws Exception {
        //given
        String postInputNumbersPath = "/inputNumbers";

        //when && then
        mockMvc.perform(post(postInputNumbersPath)
                        .content(
                                """
                                        {
                                        }
                                        """.trim()
                        ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessages").value(containsInAnyOrder(
                        "inputNumbers must not be null",
                        "inputNumbers must not be empty"
                )));
    }

}
