// DeudaSimpleDTO.java
package com.ejemplo.usuarios_api.dto;

import com.ejemplo.usuarios_api.model.Deuda;
import com.ejemplo.usuarios_api.model.TipoDeuda;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Setter
@Getter
@AllArgsConstructor
public class DeudaSimpleDTO {
    private String tipoDeuda;
    private String observaciones;

    public DeudaSimpleDTO(Deuda model){
        this.tipoDeuda = Optional.ofNullable(model.getTipoDeuda()).map(TipoDeuda::name).orElse(null);
        this.observaciones = model.getObservaciones();
    }

}
