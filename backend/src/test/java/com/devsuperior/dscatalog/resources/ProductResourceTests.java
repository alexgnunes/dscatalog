package com.devsuperior.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundyException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private ProductService service;
	
	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	
	private Long existId;
	private Long nonExistId;
	private Long dependentId;
	
	@BeforeEach
	void setUp() throws Exception{
		
		existId = 1L;
		nonExistId = 2L;
		dependentId = 3L;
		
		productDTO = Factory.createdProductDTO();
		page = new PageImpl<>(List.of(productDTO));
		
		when(service.findAllPaged(any())).thenReturn(page);
		
		when(service.findById(existId)).thenReturn(productDTO);
		when(service.findById(nonExistId)).thenThrow(ResourceNotFoundyException.class);
		
		when(service.insert(any())).thenReturn(productDTO);
		
		when(service.update(eq(existId), any())).thenReturn(productDTO);
		when(service.update(eq(nonExistId), any())).thenThrow(ResourceNotFoundyException.class);
		
		doNothing().when(service).delete(existId);
		doThrow(ResourceNotFoundyException.class).when(service).delete(nonExistId);
		doThrow(DataBaseException.class).when(service).delete(dependentId);
	}
	
	@Test
	public void deleteShouldReturnNoContentWhenIdExist() throws Exception {
		
		ResultActions result =  mockMvc.perform(delete("/products/{id}", existId));
		
				result.andExpect(status().isNoContent());		
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundyExceptionWhenIdNotExist() throws Exception {
		
		ResultActions result =  mockMvc.perform(delete("/products/{id}", nonExistId));
		
		result.andExpect(status().isNotFound());	
	}
	
	@Test
	public void deleteShouldThrowDataBaseExceptionWhenDependentId() throws Exception {
		
		ResultActions result =  mockMvc.perform(delete("/products/{id}", dependentId));
		
		result.andExpect(status().isBadRequest());	
	}
	
	@Test
	public void insertShouldReturnProductDTO() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result =  mockMvc.perform(post("/products")
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExist() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result =  mockMvc.perform(put("/products/{id}", existId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
	}
	
	@Test
	public void updateShouldReturnNotFoundExceptionWhenIdNonExist() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result =  mockMvc.perform(put("/products/{id}", nonExistId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void findAllShouldReturnPage() throws Exception {
		
		ResultActions result =  mockMvc.perform(get("/products")
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
	}
	
	@Test
	public void findByIdShouldReturnProductWhenIdExist() throws Exception {
		
		ResultActions result =  mockMvc.perform(get("/products/{id}", existId)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
	}
	
	@Test
	public void findByIdShouldReturnNotFoundExceptionWhenIdNonExist() throws Exception {
		
		ResultActions result =  mockMvc.perform(get("/products/{id}", nonExistId)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
}
