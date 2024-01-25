package es.iesteis.proyectoud4bugstars;

import es.iesteis.proyectoud4bugstars.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ControllerTests {
	@Autowired
	private MockMvc mvc;

	@MockBean
	private AccountService accountService;

	@Test
	void testApiInfo() throws Exception {
		mvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.environment").value("test"));
	}

	@Test
	void testMemberIdAvailable() throws Exception {
		given(accountService.memberIdExists("existe")).willReturn(true);
		given(accountService.memberIdExists("noexiste")).willReturn(false);

		mvc.perform(
				get("/api/v1/memberidAvailable")
					.queryParam("id", "existe")
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.available").value(false));

		mvc.perform(
				get("/api/v1/memberidAvailable")
					.queryParam("id", "noexiste")
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.available").value(true));
	}


}
