package de.hskl.cnseqrcode.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;

public class QrGenerator {
    public static byte[] generate(String text, QrBackground background) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            var bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, 1024, 1024);

            MatrixToImageConfig config = switch (background) {
                case TRANSPARENT -> new MatrixToImageConfig(0xFF000000, 0x00FFFFFF);
                case WHITE -> new MatrixToImageConfig(0xFF000000, 0xFFFFFFFF);
            };

            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out, config);
            return out.toByteArray();
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }
}
