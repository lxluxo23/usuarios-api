package com.ejemplo.usuarios_api.repository;

import com.ejemplo.usuarios_api.model.PagoHonorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;


import java.util.List;

@Repository
public interface PagoHonorarioRepository extends JpaRepository<PagoHonorario, Long> {
    List<PagoHonorario> findByMesHonorario_MesId(Long mesId);

    @Query("SELECT ph FROM PagoHonorario ph WHERE ph.mesHonorario.honorario.cliente.clienteId = :clienteId")
    List<PagoHonorario> findByMesHonorarioHonorarioClienteClienteId(@Param("clienteId") Long clienteId);

}
