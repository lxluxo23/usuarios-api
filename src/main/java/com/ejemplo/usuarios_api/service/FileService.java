package com.ejemplo.usuarios_api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileService {

    // Convertir archivo MultipartFile a bytes para almacenamiento en base de datos
    public byte[] convertFileToBytes(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("El archivo está vacío.");
        }
        return file.getBytes();
    }

    // Recuperar archivo desde bytes almacenados (puedes procesarlo o enviarlo directamente)
    public byte[] retrieveFileFromBytes(byte[] fileBytes) {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new IllegalArgumentException("El archivo no está disponible.");
        }
        return fileBytes; // Devuelve directamente los bytes para ser utilizados o enviados.
    }
}
