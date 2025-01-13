// src/main/java/com/ejemplo/usuarios_api/util/ExcelUtil.java
package com.ejemplo.usuarios_api.util;

import com.ejemplo.usuarios_api.dto.ClienteSaldoPendienteDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class ExcelUtil {

    public byte[] generarExcelClientesConSaldo(List<ClienteSaldoPendienteDTO> clientes) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Clientes Saldo Pendiente");

        // Crear encabezados
        Row headerRow = sheet.createRow(0);
        String[] encabezados = {"ID Cliente", "Nombre", "RUT", "Email", "Teléfono", "Saldo Pendiente"};

        for (int i = 0; i < encabezados.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(encabezados[i]);
        }

        // Rellenar datos de los clientes
        int fila = 1;
        for (ClienteSaldoPendienteDTO cliente : clientes) {
            Row row = sheet.createRow(fila++);
            row.createCell(0).setCellValue(cliente.getClienteId());
            row.createCell(1).setCellValue(cliente.getNombre());
            row.createCell(2).setCellValue(cliente.getRut());
            row.createCell(3).setCellValue(cliente.getEmail());
            row.createCell(4).setCellValue(cliente.getTelefono());
            row.createCell(5).setCellValue(cliente.getSaldoPendiente().doubleValue());
        }

        // Autoajustar el tamaño de las columnas
        for (int i = 0; i < encabezados.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        return baos.toByteArray();
    }

}
