package com.cibertec.denticore.crm.repositories;

import com.cibertec.denticore.crm.entities.Cita;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cita c " +
       "WHERE c.odontologo.idUsuario = :idOdontologo " +
       "AND c.fechaHora = :fechaHora " +
       "AND c.estado IN ('PENDIENTE', 'CONFIRMADA', 'EN_SALA', 'EN_CURSO')")
boolean existsByOdontologoAndFechaHoraAndEstadoOcupado(
        @Param("idOdontologo") Integer idOdontologo,
        @Param("fechaHora") LocalDateTime fechaHora);

    @Query(value = "SELECT c FROM Cita c " +
                   "JOIN FETCH c.paciente p " +
                   "JOIN FETCH p.usuario " +
                   "JOIN FETCH c.odontologo o " +
                   "JOIN FETCH o.usuario " +
                   "WHERE c.fechaHora BETWEEN :inicio AND :fin " +
                   "ORDER BY c.fechaHora ASC",
           countQuery = "SELECT COUNT(c) FROM Cita c WHERE c.fechaHora BETWEEN :inicio AND :fin")
    Page<Cita> findByFechaHoraBetweenOrderByFechaHoraAsc(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            Pageable pageable);

    @Query(value = "SELECT c FROM Cita c " +
                   "JOIN FETCH c.paciente p " +
                   "JOIN FETCH p.usuario " +
                   "JOIN FETCH c.odontologo o " +
                   "JOIN FETCH o.usuario " +
                   "WHERE c.fechaHora >= :fechaInicio " +
                   "ORDER BY c.fechaHora ASC",
           countQuery = "SELECT COUNT(c) FROM Cita c WHERE c.fechaHora >= :fechaInicio")
    Page<Cita> findByFechaHoraGreaterThanEqualOrderByFechaHoraAsc(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            Pageable pageable);


        @Query(value = "SELECT c FROM Cita c " +
               "JOIN FETCH c.paciente p " +
               "JOIN FETCH p.usuario " +
               "JOIN FETCH c.odontologo o " +
               "JOIN FETCH o.usuario " +
               "WHERE c.odontologo.id = :idOdontologo " +
               "AND c.fechaHora BETWEEN :inicio AND :fin " +
               "ORDER BY c.fechaHora ASC",
       countQuery = "SELECT COUNT(c) FROM Cita c " +
                    "WHERE c.odontologo.id = :idOdontologo " +
                    "AND c.fechaHora BETWEEN :inicio AND :fin")
        Page<Cita> findAgendaDiariaByOdontologo(
                @Param("idOdontologo") Integer idOdontologo,
                @Param("inicio") LocalDateTime inicio,
                @Param("fin") LocalDateTime fin,
                Pageable pageable);



                @Query(
        value = "SELECT c FROM Cita c " +
                "JOIN FETCH c.paciente p " +
                "JOIN FETCH p.usuario " +
                "JOIN FETCH c.odontologo o " +
                "JOIN FETCH o.usuario " +
                "WHERE c.paciente.idUsuario = :idPaciente " +
                "ORDER BY c.fechaHora DESC",
        countQuery = "SELECT COUNT(c) FROM Cita c " +
                        "WHERE c.paciente.idUsuario = :idPaciente"
        )
        Page<Cita> findByPaciente(
                @Param("idPaciente") Integer idPaciente,
                Pageable pageable);
}
