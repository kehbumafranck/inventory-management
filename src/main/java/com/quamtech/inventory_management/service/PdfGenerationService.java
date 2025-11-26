package com.quamtech.inventory_management.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.quamtech.inventory_management.entite.InventoryReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;
@RequiredArgsConstructor
@Service
public class PdfGenerationService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(new Locale("fr", "CM"));

    // Couleurs
    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(41, 128, 185);
    private static final DeviceRgb LIGHT_GRAY = new DeviceRgb(240, 240, 240);

    /**
     * Génère un PDF pour le rapport d'état des stocks
     */
    public byte[] generateStockStatusReportPdf(InventoryReport report) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // En-tête du document
            addDocumentHeader(document, report.getTitle(), report.getGeneratedAt(), report.getGeneratedBy());

            // Informations du rapport
            addReportInfo(document, report);

            // Résumé
            if (report.getSummary() != null) {
                addStockStatusSummary(document, report.getSummary());
            }

            // Tableau des produits
            addStockStatusTable(document, report);

            // Pied de page
            addFooter(document);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Génère un PDF pour le rapport des mouvements
     */
    public byte[] generateMovementsReportPdf(InventoryReport report) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // En-tête du document
            addDocumentHeader(document, report.getTitle(), report.getGeneratedAt(), report.getGeneratedBy());

            // Période
            addPeriodInfo(document, report.getStartDate(), report.getEndDate());

            // Résumé
            if (report.getSummary() != null) {
                addMovementsSummary(document, report.getSummary());
            }

            // Tableau des mouvements
            addMovementsTable(document, report);

            // Pied de page
            addFooter(document);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Génère un PDF pour le rapport des alertes de stock faible
     */
    public byte[] generateLowStockAlertReportPdf(InventoryReport report) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // En-tête du document
            addDocumentHeader(document, report.getTitle(), report.getGeneratedAt(), report.getGeneratedBy());

            // Avertissement
            Paragraph warning = new Paragraph("⚠️ ALERTES DE STOCK CRITIQUE")
                    .setFontSize(14)
                    .setBold()
                    .setFontColor(ColorConstants.RED)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(warning);

            // Résumé
            if (report.getSummary() != null) {
                addLowStockSummary(document, report.getSummary());
            }

            // Tableau des alertes
            addLowStockTable(document, report);

            // Recommandations
            addRecommendations(document, report);

            // Pied de page
            addFooter(document);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF: " + e.getMessage(), e);
        }
    }

    // ========== Méthodes privées pour construction du PDF ==========

    private void addDocumentHeader(Document document, String title, LocalDate generatedAt, String generatedBy) {
        // Titre principal
        Paragraph titlePara = new Paragraph(title)
                .setFontSize(20)
                .setBold()
                .setFontColor(HEADER_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        document.add(titlePara);

        // Date et utilisateur
        String subtitle = String.format("Généré le %s par %s",
                generatedAt.format(DATE_FORMATTER),
                generatedBy != null ? generatedBy : "Système");
        Paragraph subtitlePara = new Paragraph(subtitle)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(subtitlePara);

        // Ligne de séparation
        document.add(new Paragraph("\n"));
    }

    private void addReportInfo(Document document, InventoryReport report) {
        if (report.getWarehouseId() != null) {
            Paragraph info = new Paragraph("Entrepôt: " + report.getWarehouseId())
                    .setFontSize(10)
                    .setMarginBottom(10);
            document.add(info);
        }
    }

    private void addPeriodInfo(Document document, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            String period = String.format("Période: Du %s au %s",
                    startDate.format(DATE_FORMATTER),
                    endDate.format(DATE_FORMATTER));
            Paragraph periodPara = new Paragraph(period)
                    .setFontSize(10)
                    .setBold()
                    .setMarginBottom(15);
            document.add(periodPara);
        }
    }

    private void addStockStatusSummary(Document document, InventoryReport.ReportSummary summary) {
        Paragraph summaryTitle = new Paragraph("RÉSUMÉ")
                .setFontSize(14)
                .setBold()
                .setMarginBottom(10);
        document.add(summaryTitle);

        Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // En-têtes
        summaryTable.addHeaderCell(createHeaderCell("Produits"));
        summaryTable.addHeaderCell(createHeaderCell("Quantité Totale"));
        summaryTable.addHeaderCell(createHeaderCell("Quantité Disponible"));
        summaryTable.addHeaderCell(createHeaderCell("Valeur Totale"));

        // Valeurs
        summaryTable.addCell(createCell(String.valueOf(summary.getTotalProducts())));
        summaryTable.addCell(createCell(String.valueOf(summary.getTotalQuantity())));
        summaryTable.addCell(createCell(String.valueOf(summary.getTotalAvailable())));
        summaryTable.addCell(createCell(formatCurrency(summary.getTotalValue())));

        document.add(summaryTable);

        // Alertes
        if (summary.getLowStockProducts() > 0 || summary.getOutOfStockProducts() > 0) {
            Paragraph alertsTitle = new Paragraph("ALERTES")
                    .setFontSize(12)
                    .setBold()
                    .setMarginTop(10)
                    .setMarginBottom(5);
            document.add(alertsTitle);

            Table alertsTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                    .useAllAvailableWidth()
                    .setMarginBottom(20);

            alertsTable.addHeaderCell(createHeaderCell("Rupture de stock"));
            alertsTable.addHeaderCell(createHeaderCell("Stock critique"));
            alertsTable.addHeaderCell(createHeaderCell("À réapprovisionner"));

            alertsTable.addCell(createCell(String.valueOf(summary.getOutOfStockProducts())));
            alertsTable.addCell(createCell(String.valueOf(summary.getLowStockProducts())));
            alertsTable.addCell(createCell(String.valueOf(summary.getNeedsReorderProducts())));

            document.add(alertsTable);
        }
    }

    private void addMovementsSummary(Document document, InventoryReport.ReportSummary summary) {
        Paragraph summaryTitle = new Paragraph("RÉSUMÉ")
                .setFontSize(14)
                .setBold()
                .setMarginBottom(10);
        document.add(summaryTitle);

        Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        summaryTable.addHeaderCell(createHeaderCell("Produits"));
        summaryTable.addHeaderCell(createHeaderCell("Total Mouvements"));
        summaryTable.addHeaderCell(createHeaderCell("Quantité Totale"));
        summaryTable.addHeaderCell(createHeaderCell("Valeur Totale"));

        summaryTable.addCell(createCell(String.valueOf(summary.getTotalProducts())));
        summaryTable.addCell(createCell(String.valueOf(summary.getTotalMovements())));
        summaryTable.addCell(createCell(String.valueOf(summary.getTotalQuantity())));
        summaryTable.addCell(createCell(formatCurrency(summary.getTotalValue())));

        document.add(summaryTable);
    }

    private void addLowStockSummary(Document document, InventoryReport.ReportSummary summary) {
        Paragraph summaryTitle = new Paragraph("RÉSUMÉ DES ALERTES")
                .setFontSize(14)
                .setBold()
                .setMarginBottom(10);
        document.add(summaryTitle);

        Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        summaryTable.addHeaderCell(createHeaderCell("Produits en alerte"));
        summaryTable.addHeaderCell(createHeaderCell("Valeur totale impactée"));

        summaryTable.addCell(createCell(String.valueOf(summary.getTotalProducts())));
        summaryTable.addCell(createCell(formatCurrency(summary.getTotalValue())));

        document.add(summaryTable);
    }

    private void addStockStatusTable(Document document, InventoryReport report) {
        Paragraph tableTitle = new Paragraph("DÉTAIL DES STOCKS")
                .setFontSize(14)
                .setBold()
                .setMarginBottom(10);
        document.add(tableTitle);

        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 1.5f, 1.5f, 1.5f, 2}))
                .useAllAvailableWidth();

        // En-têtes
        table.addHeaderCell(createHeaderCell("Produit"));
        table.addHeaderCell(createHeaderCell("SKU"));
        table.addHeaderCell(createHeaderCell("Quantité"));
        table.addHeaderCell(createHeaderCell("Réservé"));
        table.addHeaderCell(createHeaderCell("Disponible"));
        table.addHeaderCell(createHeaderCell("Valeur"));

        // Lignes
        for (InventoryReport.ReportItem item : report.getItems()) {
            table.addCell(createCell(item.getProductName()));
            table.addCell(createCell(item.getSku()));
            table.addCell(createCell(String.valueOf(item.getQuantity())));
            table.addCell(createCell(String.valueOf(item.getReservedQuantity())));
            table.addCell(createCell(String.valueOf(item.getAvailableQuantity())));
            table.addCell(createCell(formatCurrency(item.getTotalValue())));
        }

        document.add(table);
    }

    private void addMovementsTable(Document document, InventoryReport report) {
        Paragraph tableTitle = new Paragraph("DÉTAIL DES MOUVEMENTS")
                .setFontSize(14)
                .setBold()
                .setMarginBottom(10);
        document.add(tableTitle);

        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 1.5f, 1.5f, 2}))
                .useAllAvailableWidth();

        // En-têtes
        table.addHeaderCell(createHeaderCell("Produit"));
        table.addHeaderCell(createHeaderCell("SKU"));
        table.addHeaderCell(createHeaderCell("Nb Mouvements"));
        table.addHeaderCell(createHeaderCell("Quantité"));
        table.addHeaderCell(createHeaderCell("Valeur"));

        // Lignes
        for (InventoryReport.ReportItem item : report.getItems()) {
            table.addCell(createCell(item.getProductName()));
            table.addCell(createCell(item.getSku()));
            table.addCell(createCell(String.valueOf(item.getMovements())));
            table.addCell(createCell(String.valueOf(item.getQuantity())));
            table.addCell(createCell(formatCurrency(item.getTotalValue())));
        }

        document.add(table);
    }

    private void addLowStockTable(Document document, InventoryReport report) {
        Paragraph tableTitle = new Paragraph("PRODUITS EN ALERTE")
                .setFontSize(14)
                .setBold()
                .setMarginBottom(10);
        document.add(tableTitle);

        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 1.5f, 2}))
                .useAllAvailableWidth();

        // En-têtes
        table.addHeaderCell(createHeaderCell("Produit"));
        table.addHeaderCell(createHeaderCell("SKU"));
        table.addHeaderCell(createHeaderCell("Entrepôt"));
        table.addHeaderCell(createHeaderCell("Qté Dispo"));
        table.addHeaderCell(createHeaderCell("Valeur"));

        // Lignes avec code couleur selon criticité
        for (InventoryReport.ReportItem item : report.getItems()) {
            Cell nameCell = createCell(item.getProductName());

            // Colorer en rouge si quantité = 0
            if (item.getAvailableQuantity() != null && item.getAvailableQuantity() == 0) {
                nameCell.setBackgroundColor(new DeviceRgb(255, 200, 200));
            }

            table.addCell(nameCell);
            table.addCell(createCell(item.getSku()));
            table.addCell(createCell(item.getWarehouseName() != null ? item.getWarehouseName() : "N/A"));
            table.addCell(createCell(String.valueOf(item.getAvailableQuantity())));
            table.addCell(createCell(formatCurrency(item.getTotalValue())));
        }

        document.add(table);
    }

    private void addRecommendations(Document document, InventoryReport report) {
        Paragraph recTitle = new Paragraph("RECOMMANDATIONS")
                .setFontSize(14)
                .setBold()
                .setMarginTop(20)
                .setMarginBottom(10);
        document.add(recTitle);

        Paragraph rec1 = new Paragraph("• Commander immédiatement les produits en rupture de stock")
                .setFontSize(10)
                .setMarginBottom(5);
        document.add(rec1);

        Paragraph rec2 = new Paragraph("• Vérifier les délais de livraison pour les produits critiques")
                .setFontSize(10)
                .setMarginBottom(5);
        document.add(rec2);

        Paragraph rec3 = new Paragraph("• Contacter les fournisseurs pour les commandes urgentes")
                .setFontSize(10)
                .setMarginBottom(5);
        document.add(rec3);

        Paragraph rec4 = new Paragraph("• Réviser les seuils de réapprovisionnement si nécessaire")
                .setFontSize(10);
        document.add(rec4);
    }

    private void addFooter(Document document) {
        Paragraph footer = new Paragraph("\n\n" + "─".repeat(80))
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(footer);

        Paragraph footerText = new Paragraph("Document généré automatiquement - Système de Gestion d'Inventaire")
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY);
        document.add(footerText);
    }

    private Cell createHeaderCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold().setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(HEADER_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
    }

    private Cell createCell(String text) {
        return new Cell()
                .add(new Paragraph(text != null ? text : "N/A"))
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(5);
    }

    private String formatCurrency(Double value) {
        if (value == null) return "0 FCFA";
        return String.format("%,.0f FCFA", value);
    }
}
