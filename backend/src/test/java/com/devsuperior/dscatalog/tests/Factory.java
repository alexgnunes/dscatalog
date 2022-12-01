package com.devsuperior.dscatalog.tests;

import java.time.Instant;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

public class Factory {
	
	public static Product createdProduct() {
		
		Product product = new Product(1L, "Phone", "good phone", 800.0, "https://imagem.com/img.png", Instant.parse("2020-07-14T10:00:00Z")); 
		product.getCategories().add(new Category(2L, "Eletronics"));
		return product;
	}
	
	public static ProductDTO createdProductDTO() {
		
		Product product = createdProduct();
		return new ProductDTO(product, product.getCategories());
	}
}
