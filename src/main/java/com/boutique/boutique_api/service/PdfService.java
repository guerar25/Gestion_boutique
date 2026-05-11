package com.boutique.boutique_api.service;

import com.boutique.boutique_api.model.Commande;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    public byte[] genererFacturePdf(Commande commande) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
            Paragraph title = new Paragraph("FACTURE - BOUTIQUE GUERAR", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Client : " + commande.getNomClient()));
            document.add(new Paragraph("Email : " + commande.getEmailClient()));
            document.add(new Paragraph("Date : " + commande.getDateCommande()));
            document.add(new Paragraph("-----------------------------------------------------------"));
            document.add(new Paragraph("Produit : " + commande.getProduit().getNom()));
            document.add(new Paragraph("Quantité : " + commande.getQuantite()));
            document.add(new Paragraph("Prix Unitaire : " + commande.getProduit().getPrix() + " FCFA"));
            document.add(new Paragraph("TOTAL : " + commande.getTotal() + " FCFA"));
            document.add(new Paragraph("-----------------------------------------------------------"));

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}
