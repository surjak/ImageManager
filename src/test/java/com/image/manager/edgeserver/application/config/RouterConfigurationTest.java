package com.image.manager.edgeserver.application.config;

import com.image.manager.edgeserver.application.config.RouterConfiguration;
import com.image.manager.edgeserver.domain.origin.OriginFacade;
import com.image.manager.edgeserver.domain.operation.OperationFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = RouterConfiguration.class)
class RouterConfigurationTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private OriginFacade originFacade;
    @MockBean
    private OperationFactory operationFactory;

    @Test
    void retrieveAppropriateFileName() throws Exception {
        //given
        String fileName = "logo_agh.png";

        //when
        webTestClient
                .get()
                .uri("/" + fileName)
                .exchange()
                .expectBody(byte[].class);
        //then
        ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(originFacade).getImageAndApplyOperations(fileNameCaptor.capture(), any());
        assertThat(fileNameCaptor.getValue()).isEqualTo(fileName);
    }
}