package com.ejemplo.usuarios_api.repository;

import com.ejemplo.usuarios_api.model.Deuda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeudaRepository extends JpaRepository<Deuda, Long> {
    List<Deuda> findByCliente_ClienteId(Long clienteID); // Relación Cliente -> Propiedad clienteID
}
