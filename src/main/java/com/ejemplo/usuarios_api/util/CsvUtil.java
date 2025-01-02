// src/main/java/com/ejemplo/usuarios_api/util/CsvUtil.java
package com.ejemplo.usuarios_api.util;

import com.ejemplo.usuarios_api.dto.ClienteSaldoPendienteDTO;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Component
public class CsvUtil {

    public String generarCsvClientesConSaldo(List<ClienteSaldoPendienteDTO> clientes) {
        StringWriter sw = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(sw);

        // Escribir encabezados
        String[] encabezados = {"ID Cliente", "Nombre", "RUT", "Email", "Teléfono", "Saldo Pendiente"};
        csvWriter.writeNext(encabezados);

        // Configurar el formateador para CLP
        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("es", "CL"));
        numberFormat.setGroupingUsed(true);
        numberFormat.setMaximumFractionDigits(0);
        numberFormat.setMinimumFractionDigits(0);

        // Escribir datos
        for (ClienteSaldoPendienteDTO cliente : clientes) {
            String saldo = cliente.getSaldoPendiente() != null
                    ? numberFormat.format(cliente.getSaldoPendiente())
                    : "0";
            String[] datos = {
                    cliente.getClienteId().toString(),
                    cliente.getNombre(),
                    cliente.getRut(),
                    cliente.getEmail(),
                    cliente.getTelefono(),
                    saldo
            };
            csvWriter.writeNext(datos);
        }

        try {
            csvWriter.close();
        } catch (Exception e) {
            // Manejar excepción según corresponda
            e.printStackTrace();
        }

        return sw.toString();
    }
}
