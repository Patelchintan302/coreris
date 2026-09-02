package com.example.coreris.controller;

import com.example.coreris.dto.ReportCreateDto;
import com.example.coreris.dto.ReportDto;
import com.example.coreris.service.FileStorageService;
import com.example.coreris.service.ReportService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private FileStorageService fileStorageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateReportSuccess() throws Exception {
        ReportCreateDto createDto = ReportCreateDto.builder()
                .finding("Lungs clear.")
                .build();

        ReportDto responseDto = ReportDto.builder()
                .id(1L)
                .finding("Lungs clear.")
                .appointmentId(10L)
                .build();

        Mockito.when(reportService.createReport(Mockito.eq(10L), Mockito.eq(3L), Mockito.any(ReportCreateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/appointments/10/report")
                        .param("radiologistId", "3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.finding").value("Lungs clear."))
                .andExpect(jsonPath("$.appointmentId").value(10L));
    }
}
