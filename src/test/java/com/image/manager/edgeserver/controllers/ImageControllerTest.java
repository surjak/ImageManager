package com.image.manager.edgeserver.controllers;

import com.image.manager.edgeserver.OriginFacade;
import com.image.manager.edgeserver.services.CropService;
import com.image.manager.edgeserver.services.ScaleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ImageController.class)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private OriginFacade originFacade;
    @MockBean
    private CropService cropService;
    @MockBean
    private ScaleService scaleService;

    @Test
    void retrieveAppropriateFileName() throws Exception {
        //given
        String fileName = "logo_agh.png";
        String params = "op=crop&w=20&h=20&h=10&op=scale&w=500&h=500";

        //when
        mockMvc.perform(get("/" + fileName + "?" + params)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(originFacade).fetchImageFromOrigin(fileNameCaptor.capture());
        assertThat(fileNameCaptor.getValue()).isEqualTo(fileName);
    }
}