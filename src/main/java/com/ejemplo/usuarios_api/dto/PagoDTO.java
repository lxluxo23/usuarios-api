package com.ejemplo.usuarios_api.dto;

import com.ejemplo.usuarios_api.model.Pago;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoDTO {
    private Long pagoId;
    private Long deudaId;
    private LocalDate fechaTransaccion;
    private BigDecimal monto;
    private String metodoPago;
    private String observaciones;
    private Integer mes;
    private DeudaSimpleDTO deuda;


    public PagoDTO(Long pagoId) {
        this.pagoId = pagoId;
    }

    public PagoDTO(Pago model){
        this.pagoId = model.getPagoId();
        this.deudaId = model.getDeuda().getDeudaId();
        this.fechaTransaccion = model.getFechaTransaccion();
        this.monto = model.getMonto();
        this.metodoPago = model.getMetodoPago().toString();
        this.observaciones = model.getObservaciones();
        this.mes = model.getMes();
        this.deuda = Optional.ofNullable(model.getDeuda()).map(DeudaSimpleDTO::new).orElse(null);
    }

}