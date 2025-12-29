package de.hskl.cnseqrcode.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;

public class QrGenerator {
    public static byte[] generate(String text) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            var bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300);
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Failed to generate Qr code", e);
        }
    }
}
