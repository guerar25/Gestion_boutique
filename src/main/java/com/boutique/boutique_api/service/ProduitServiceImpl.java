package com.boutique.boutique_api.service;

import com.boutique.boutique_api.dto.ProduitDTO;
import com.boutique.boutique_api.dto.ProduitRequest;
import com.boutique.boutique_api.exception.ResourceNotFoundException;
import com.boutique.boutique_api.model.Categorie;
import com.boutique.boutique_api.model.Produit;
import com.boutique.boutique_api.repository.CategorieRepository;
import com.boutique.boutique_api.repository.ProduitRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;
    private final CategorieRepository categorieRepository;
    private final FileStorageService fileStorageService;

    public ProduitServiceImpl(ProduitRepository produitRepository, 
                              CategorieRepository categorieRepository, 
                              FileStorageService fileStorageService) {
        this.produitRepository = produitRepository;
        this.categorieRepository = categorieRepository;
        this.fileStorageService = fileStorageService;
    }

    // --- CREATE ---
    @Override
    public ProduitDTO ajouterProduitAvecImage(ProduitRequest request, MultipartFile image) {
        Categorie categorie = categorieRepository.findById(request.categorieId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));

        Produit produit = new Produit();
        produit.setNom(request.nom());
        produit.setDescription(request.description());
        produit.setPrix(request.prix());
        produit.setQuantiteStock(request.quantiteStock());
        produit.setCategorie(categorie);

        if (image != null && !image.isEmpty()) {
            produit.setImageUrl(fileStorageService.storeFile(image));
        }

        return mapToDTO(produitRepository.save(produit));
    }

    // --- READ (All) ---
    @Override
    public List<ProduitDTO> getAllProduits() {
        return produitRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // --- READ (One) ---
    @Override
    public ProduitDTO getProduitById(Long id) {
        return produitRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
    }

    // --- UPDATE ---
    @Override
    public ProduitDTO modifierProduitAvecImage(Long id, ProduitRequest request, MultipartFile image) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

        Categorie categorie = categorieRepository.findById(request.categorieId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));

        produit.setNom(request.nom());
        produit.setDescription(request.description());
        produit.setPrix(request.prix());
        produit.setQuantiteStock(request.quantiteStock());
        produit.setCategorie(categorie);

        // Si une nouvelle image est envoyée, on supprime l'ancienne et on met la nouvelle
        if (image != null && !image.isEmpty()) {
            if (produit.getImageUrl() != null) {
                fileStorageService.deleteFile(produit.getImageUrl());
            }
            produit.setImageUrl(fileStorageService.storeFile(image));
        }

        return mapToDTO(produitRepository.save(produit));
    }

    // --- DELETE ---
    @Override
    public void supprimerProduit(Long id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
        
        // Supprimer l'image du disque avant de supprimer le produit de la base
        if (produit.getImageUrl() != null) {
            fileStorageService.deleteFile(produit.getImageUrl());
        }
        
        produitRepository.delete(produit);
    }

    // --- UTILS ---
    private ProduitDTO mapToDTO(Produit p) {
        return new ProduitDTO(
                p.getId(), p.getNom(), p.getDescription(), p.getPrix(),
                p.getQuantiteStock(), p.getImageUrl(),
                p.getCategorie() != null ? p.getCategorie().getId() : null,
                p.getCategorie() != null ? p.getCategorie().getNom() : null
        );
    }

    @Override
    public Page<ProduitDTO> rechercherProduit(String nom, int page, int size) {
        return produitRepository.findByNomContainingIgnoreCase(nom, PageRequest.of(page, size)).map(this::mapToDTO);
    }

    @Override
    public Page<ProduitDTO> paginationProduits(int page, int size) {
        return produitRepository.findAll(PageRequest.of(page, size)).map(this::mapToDTO);
    }

    @Override
    public void mettreAJourStock(Long id, Integer quantite) {
        Produit produit = produitRepository.findById(id).orElseThrow();
        produit.setQuantiteStock(produit.getQuantiteStock() - quantite);
        produitRepository.save(produit);
    }
}