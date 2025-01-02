package com.ejemplo.usuarios_api.repository;

import com.ejemplo.usuarios_api.dto.ClienteSaldoPendienteDTO;
import com.ejemplo.usuarios_api.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByRut(String rut);
    boolean existsByNombre(String nombre);
    @Query("SELECT new com.ejemplo.usuarios_api.dto.ClienteSaldoPendienteDTO(c.clienteId, c.nombre, c.rut, c.email, c.telefono, COALESCE(SUM(d.montoRestante), 0)) " +
            "FROM Cliente c LEFT JOIN c.deudas d GROUP BY c.clienteId, c.nombre, c.rut, c.email, c.telefono")
    List<ClienteSaldoPendienteDTO> findAllClientesConSaldoPendiente();
}
