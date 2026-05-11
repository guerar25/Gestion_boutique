package com.boutique.boutique_api.service;

import com.boutique.boutique_api.dto.ProduitDTO;
import com.boutique.boutique_api.dto.ProduitRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProduitService {
    // Méthodes avec images
    ProduitDTO ajouterProduitAvecImage(ProduitRequest request, MultipartFile image);
    ProduitDTO modifierProduitAvecImage(Long id, ProduitRequest request, MultipartFile image);

    // Méthodes classiques
    List<ProduitDTO> getAllProduits();
    ProduitDTO getProduitById(Long id);
    void supprimerProduit(Long id);
    Page<ProduitDTO> rechercherProduit(String nom, int page, int size);
    Page<ProduitDTO> paginationProduits(int page, int size);
    void mettreAJourStock(Long id, Integer quantite);
}