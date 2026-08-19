package com.cibertec.denticore.clinica.repositories;

import com.cibertec.denticore.clinica.entities.DetalleOdontograma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleOdontogramaRepository extends JpaRepository<DetalleOdontograma, Integer> {

    @Query("SELECT d FROM DetalleOdontograma d " +
           "JOIN FETCH d.odontograma o " +
           "JOIN FETCH o.atencionClinica a " +
           "WHERE a.historiaClinica.paciente.id = :idPaciente " +
           "ORDER BY a.fechaAtencion DESC")
    List<DetalleOdontograma> findHistorialByPacienteId(@Param("idPaciente") Integer idPaciente);
}
