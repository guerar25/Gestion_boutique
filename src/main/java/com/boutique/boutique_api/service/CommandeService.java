package com.boutique.boutique_api.service;

import com.boutique.boutique_api.model.Commande;

public interface CommandeService {
    Commande passerCommande(Long produitId, String nomClient, String emailClient, Integer quantite);
}