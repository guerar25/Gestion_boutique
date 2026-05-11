package com.boutique.boutique_api.dto;

import jakarta.validation.constraints.*;

public record ProduitRequest(
    @NotBlank(message = "Le nom est obligatoire")
    String nom,
    
    String description,
    
    @Positive(message = "Le prix doit être positif")
    Double prix,
    
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    Integer quantiteStock,
    
    Long categorieId
) {}
