package com.ejemplo.usuarios_api.repository;

import com.ejemplo.usuarios_api.model.MesHonorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MesHonorarioRepository extends JpaRepository<MesHonorario, Long> {
    List<MesHonorario> findByHonorario_HonorarioId(Long honorarioId);

    Optional<MesHonorario> findByHonorario_HonorarioIdAndMes(Long honorarioId, int mes);
    Optional<MesHonorario> findByMesId(Long mesId);
}
