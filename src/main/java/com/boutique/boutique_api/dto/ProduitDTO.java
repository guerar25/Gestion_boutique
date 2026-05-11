package com.boutique.boutique_api.dto;

public record ProduitDTO(
        Long id,
        String nom,
        String description,
        Double prix,
        Integer quantiteStock,
        String imageUrl,
        Long categorieId,
        String categorieNom) {
}
