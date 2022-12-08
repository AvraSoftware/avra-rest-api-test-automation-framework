package com.avra.qa.common.util.datautil;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Component
public class PdfContentReader {

    public String extractPdfContent(byte[] pdf) throws IOException {
        PDDocument document = PDDocument.load(new ByteArrayInputStream(pdf));
        try {
            return new PDFTextStripper().getText(document);
        } finally {
            document.close();
        }
    }
}
