package com.ejemplo.usuarios_api.repository;

import com.ejemplo.usuarios_api.model.HonorarioContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HonorarioRepository extends JpaRepository<HonorarioContable, Long> {
    List<HonorarioContable> findByCliente_ClienteIdAndAnio(Long clienteId, int anio);
    List<HonorarioContable> findByCliente_ClienteId(Long clienteId);

}