package com.avra.qa.common.util.datautil;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.apache.pdfbox.Loader.loadPDF;

@Component
public class PdfContentReader {

    public String extractPdfContent(byte[] pdf) throws IOException {
        try (PDDocument document = loadPDF(pdf)) {
            return new PDFTextStripper().getText(document);
        }
    }
}
