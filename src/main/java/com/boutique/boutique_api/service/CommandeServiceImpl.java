package com.boutique.boutique_api.service;

import com.boutique.boutique_api.model.Commande;
import com.boutique.boutique_api.model.Produit;
import com.boutique.boutique_api.repository.CommandeRepository;
import com.boutique.boutique_api.repository.ProduitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository commandeRepository;
    private final ProduitRepository produitRepository;
    private final ProduitService produitService;
    private final EmailService emailService;
    private final PdfService pdfService; // <--- C'ÉTAIT L'OUBLI ICI

    // AJOUTE pdfService DANS LE CONSTRUCTEUR AUSSI
    public CommandeServiceImpl(CommandeRepository commandeRepository, 
                               ProduitRepository produitRepository, 
                               ProduitService produitService,
                               EmailService emailService,
                               PdfService pdfService) {
        this.commandeRepository = commandeRepository;
        this.produitRepository = produitRepository;
        this.produitService = produitService;
        this.emailService = emailService;
        this.pdfService = pdfService;
    }

    @Override
    public Commande passerCommande(Long produitId, String nomClient, String emailClient, Integer quantite) {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        if (produit.getQuantiteStock() < quantite) {
            throw new RuntimeException("Stock insuffisant ! Il ne reste que " + produit.getQuantiteStock());
        }

        Commande commande = new Commande();
        commande.setNomClient(nomClient);
        commande.setEmailClient(emailClient);
        commande.setQuantite(quantite);
        commande.setProduit(produit);
        commande.setTotal(produit.getPrix() * quantite);

        // Mise à jour du stock
        produitService.mettreAJourStock(produitId, quantite);
        
        // Sauvegarde la commande pour avoir un ID
        Commande commandeSauvegardee = commandeRepository.save(commande);

        // GÉNÉRATION DU PDF ET ENVOI DU MAIL
        try {
            // Maintenant pdfService est reconnu !
            byte[] pdfFacture = pdfService.genererFacturePdf(commandeSauvegardee);

            emailService.envoyerConfirmationAvecFacture(
                    emailClient, 
                    nomClient, 
                    produit.getNom(), 
                    commande.getTotal(), 
                    pdfFacture
            );
        } catch (Exception e) {
            System.err.println("Erreur mail/PDF : " + e.getMessage());
            e.printStackTrace();
        }

        return commandeSauvegardee;
    }
}