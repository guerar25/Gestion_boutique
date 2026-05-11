package com.boutique.boutique_api.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void envoyerConfirmationAvecFacture(String to, String nomClient, String nomProduit, Double total, byte[] pdfBytes) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        
        // "true" indique que le message est multipart (texte + fichier)
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("guerar351@gmail.com");
        helper.setTo(to);
        helper.setSubject("Confirmation et Facture - Boutique Guerar");
        
        String contenu = "Bonjour " + nomClient + ",\n\n" +
                "Merci pour votre achat ! Vous trouverez ci-joint la facture pour le produit : " + nomProduit + ".\n" +
                "Montant total : " + total + " FCFA.\n\n" +
                "Cordialement,\nL'équipe Boutique.";
        
        helper.setText(contenu);

        // AJOUT DE LA PIÈCE JOINTE (Le PDF)
        helper.addAttachment("Facture_Boutique.pdf", new ByteArrayResource(pdfBytes));

        mailSender.send(message);
    }
}