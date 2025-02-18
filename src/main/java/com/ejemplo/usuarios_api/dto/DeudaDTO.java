package com.ejemplo.usuarios_api.dto;

import com.ejemplo.usuarios_api.model.Cliente;
import com.ejemplo.usuarios_api.model.Deuda;
import com.ejemplo.usuarios_api.model.EstadoDeuda;
import com.ejemplo.usuarios_api.model.TipoDeuda;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeudaDTO {

    private Long deudaId;
    private BigDecimal montoRestante;
    private LocalDate fechaVencimiento;
    private ClienteDTO cliente; // Usamos ClienteDTO en lugar de Cliente
    private TipoDeuda tipoDeuda;
    private BigDecimal montoTotal;
    private LocalDate fechaInicio;
    private LocalDate fechaCreacion;
    private String observaciones;
    private List<PagoDTO> pagos; // Lista de Pagos como DTOs
    private EstadoDeuda estadoDeuda;

    public DeudaDTO(Deuda model){
        this.deudaId = model.getDeudaId();
        this.montoRestante = model.getMontoRestante();
        this.fechaVencimiento = model.getFechaVencimiento();
        this.cliente = Optional.ofNullable(model.getCliente()).map(cliente -> new ClienteDTO(cliente.getClienteId())).orElse(null);
        this.tipoDeuda = model.getTipoDeuda();
        this.montoTotal = model.getMontoTotal();
        this.fechaInicio = model.getFechaInicio();
        this.fechaCreacion = model.getFechaCreacion();
        this.observaciones = model.getObservaciones();
        this.pagos = Optional.ofNullable(model.getPagos()).orElse(List.of()).stream()
                .map(PagoDTO::new)
                .collect(Collectors.toList());
        this.estadoDeuda = model.getEstadoDeuda();
    }

}
