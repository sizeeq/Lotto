package pl.lotto.apivalidationerror;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import pl.lotto.BaseIntegrationTest;
import pl.lotto.Lotto.infrastructure.apivalidation.ApiValidationErrorDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiValidationFailedIntegrationTest extends BaseIntegrationTest {

    @Test
    public void should_return_bad_request_and_validation_messages_when_client_sends_empty_request_input_data() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(post("/inputNumbers")
                .content(
                        """
                                {
                                 "inputNumbers": []
                                }
                                """.trim()
                ).contentType(MediaType.APPLICATION_JSON)
        );

        //then
        MvcResult mvcResult = perform.andExpect(status().isBadRequest()).andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        ApiValidationErrorDto apiValidationErrorDto = objectMapper.readValue(json, ApiValidationErrorDto.class);
        assertThat(apiValidationErrorDto.errorMessages().contains("inputNumbers must not be empty"));
    }

    @Test
    public void should_return_bad_request_and_validation_messages_when_client_sends_no_request_input_data() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(post("/inputNumbers")
                .content(
                        """
                                {
                                }
                                """.trim()
                ).contentType(MediaType.APPLICATION_JSON)
        );

        //then
        MvcResult mvcResult = perform.andExpect(status().isBadRequest()).andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        ApiValidationErrorDto apiValidationErrorDto = objectMapper.readValue(json, ApiValidationErrorDto.class);
        assertThat(apiValidationErrorDto.errorMessages().containsAll(List.of("inputNumbers must not be null", "inputNumbers must not be empty")));
    }

}
