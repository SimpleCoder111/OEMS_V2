package org.demo.oems.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class QRCodeUtils {

    private static final String appBaseUrl = "http://localhost:7001"; // or @Value

    public String generateJoinUrl(Long classId, String token) {
        return appBaseUrl + "/join?classId=" + classId + "&token=" + token;
    }

    public byte[] generateQrPng(String content, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    public String generateBase64Qr(String content, int size) throws Exception {
        byte[] bytes = generateQrPng(content, size, size);
        return Base64.getEncoder().encodeToString(bytes);
    }
}