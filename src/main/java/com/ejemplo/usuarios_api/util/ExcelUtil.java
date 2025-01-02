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

        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        for (int i = 0; i < encabezados.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(encabezados[i]);
            cell.setCellStyle(headerStyle);
        }

        // Crear estilo para 'Saldo Pendiente'
        CellStyle saldoStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        saldoStyle.setDataFormat(format.getFormat("#,##0")); // Formato sin decimales y con punto como separador de miles

        // Rellenar datos
        int fila = 1;
        for (ClienteSaldoPendienteDTO cliente : clientes) {
            Row row = sheet.createRow(fila++);
            row.createCell(0).setCellValue(cliente.getClienteId());
            row.createCell(1).setCellValue(cliente.getNombre());
            row.createCell(2).setCellValue(cliente.getRut());
            row.createCell(3).setCellValue(cliente.getEmail());
            row.createCell(4).setCellValue(cliente.getTelefono());

            Cell saldoCell = row.createCell(5);
            if (cliente.getSaldoPendiente() != null) {
                saldoCell.setCellValue(cliente.getSaldoPendiente().doubleValue());
            } else {
                saldoCell.setCellValue(0);
            }
            saldoCell.setCellStyle(saldoStyle);
        }

        // Autoajustar el tamaño de las columnas
        for (int i = 0; i < encabezados.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Escribir el contenido en un ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        return baos.toByteArray();
    }
}
