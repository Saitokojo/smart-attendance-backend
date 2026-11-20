package com.bbu.attendance.qr.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

/**
 * Component បម្លែង Dynamic QR Token ទៅជារូបភាព Base64 PNG ដោយប្រើ Google ZXing Library
 * ទទួលខុសត្រូវដោយ៖ និស្សិត សេង ស៊ីមផា (ថ្ងៃសុក្រ)
 */
@Slf4j
@Component
public class QrCodeImageGenerator {

    /**
     * Encodes a string into a QR code matrix and converts it into a Base64 Data URL.
     *
     * @param text   Text or token to encode inside the QR code
     * @param width  Width of the generated QR image in pixels
     * @param height Height of the generated QR image in pixels
     * @return Base64 data URL string (data:image/png;base64,...)
     */
    public String generateBase64QrImage(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngData);
        } catch (Exception e) {
            log.error("Failed to generate QR Code image", e);
            throw new RuntimeException("QR Code image rendering failed", e);
        }
    }
}
