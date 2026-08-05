package com.halukkilincer.inspection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InspectionWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createStartAndCompleteInspection() throws Exception {
        String body = """
                {
                  "title": "Incoming thickness check",
                  "checklistSummary": "Measure thickness at 3 points; compare to upper/lower limits.",
                  "lotNumber": "LOT-4821",
                  "partNumber": "WAFER-A",
                  "inspector": "qa.inspector"
                }
                """;

        MvcResult created = mockMvc.perform(post("/api/v1/inspections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("PLANNED"))
                .andExpect(jsonPath("$.data.result").value("PENDING"))
                .andExpect(jsonPath("$.data.inspectionNumber").isNotEmpty())
                .andReturn();

        JsonNode data = objectMapper.readTree(created.getResponse().getContentAsString()).path("data");
        long id = data.path("id").asLong();
        assertThat(data.path("inspectionNumber").asText()).startsWith("INS-");

        mockMvc.perform(put("/api/v1/inspections/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"IN_PROGRESS","note":"Started on line 2"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));

        mockMvc.perform(put("/api/v1/inspections/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status":"COMPLETED",
                                  "result":"FAIL",
                                  "relatedNcrNumber":"NCR-2026-0001",
                                  "note":"Out of tolerance; escalated"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.data.result").value("FAIL"))
                .andExpect(jsonPath("$.data.relatedNcrNumber").value("NCR-2026-0001"));

        mockMvc.perform(get("/api/v1/inspections").param("result", "FAIL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(id));

        mockMvc.perform(put("/api/v1/inspections/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"PLANNED"}
                                """))
                .andExpect(status().isBadRequest());
    }
}
