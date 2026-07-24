package com.example.coreris.controller;

import com.example.coreris.dto.ScanResultCreateDto;
import com.example.coreris.dto.ScanResultDto;
import com.example.coreris.service.ScanResultService;
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

@WebMvcTest(ScanResultController.class)
public class ScanResultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ScanResultService scanResultService;

    @Test
    public void testCreateScanResultSuccess() throws Exception {
        ScanResultCreateDto createDto = ScanResultCreateDto.builder()
                .scanDetails("Normal scan details")
                .imageUrl("http://example.com/scan.png")
                .build();

        ScanResultDto responseDto = ScanResultDto.builder()
                .id(1L)
                .scanDetails("Normal scan details")
                .imageUrl("http://example.com/scan.png")
                .appointmentId(5L)
                .build();

        Mockito.when(scanResultService.createScanResult(Mockito.eq(5L), Mockito.eq(2L), Mockito.any(ScanResultCreateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/appointments/5/scan")
                        .param("technicianId", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.scanDetails").value("Normal scan details"))
                .andExpect(jsonPath("$.appointmentId").value(5L));
    }
}
