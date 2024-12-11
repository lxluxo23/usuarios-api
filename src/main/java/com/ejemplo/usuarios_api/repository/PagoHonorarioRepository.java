package com.ejemplo.usuarios_api.repository;

import com.ejemplo.usuarios_api.model.PagoHonorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoHonorarioRepository extends JpaRepository<PagoHonorario, Long> {
    List<PagoHonorario> findByMesHonorario_MesId(Long mesId);
}
