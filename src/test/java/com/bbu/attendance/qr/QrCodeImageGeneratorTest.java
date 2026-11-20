package com.bbu.attendance.qr;

import com.bbu.attendance.qr.service.QrCodeImageGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for QrCodeImageGenerator (ZXing Base64 PNG Rendering)")
class QrCodeImageGeneratorTest {

    private QrCodeImageGenerator qrCodeImageGenerator;

    @BeforeEach
    void setUp() {
        qrCodeImageGenerator = new QrCodeImageGenerator();
    }

    @Test
    @DisplayName("1. generateBase64QrImage should generate a valid PNG image data URL")
    void testGenerateBase64QrImage_shouldReturnValidDataUrl() {
        String content = "BBU-SMART-ATTENDANCE-DYNAMIC-QR-TOKEN-SESSION-101";
        int width = 300;
        int height = 300;

        String dataUrl = qrCodeImageGenerator.generateBase64QrImage(content, width, height);

        assertNotNull(dataUrl);
        assertTrue(dataUrl.startsWith("data:image/png;base64,"),
                "Output must be a PNG data URL starting with 'data:image/png;base64,'");

        // Verify base64 decoding produces a valid PNG binary with correct magic bytes
        String base64Data = dataUrl.substring("data:image/png;base64,".length());
        byte[] imageBytes = Base64.getDecoder().decode(base64Data);

        assertTrue(imageBytes.length > 100, "Generated PNG image must have substantial binary content");

        // PNG Header Magic Bytes: 0x89 0x50 0x4E 0x47 0x0D 0x0A 0x1A 0x0A
        assertEquals((byte) 0x89, imageBytes[0]);
        assertEquals((byte) 'P', imageBytes[1]);
        assertEquals((byte) 'N', imageBytes[2]);
        assertEquals((byte) 'G', imageBytes[3]);
    }

    @Test
    @DisplayName("2. generateBase64QrImage with invalid dimension should throw RuntimeException")
    void testGenerateBase64QrImage_invalidDimension_shouldThrowException() {
        assertThrows(RuntimeException.class, () ->
                qrCodeImageGenerator.generateBase64QrImage("test", -1, -1));
    }
}
