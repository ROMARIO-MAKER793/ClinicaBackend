package com.cibertec.denticore.clinica.repositories;

import com.cibertec.denticore.clinica.entities.Odontograma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OdontogramaRepository extends JpaRepository<Odontograma, Integer> {

    Optional<Odontograma> findByAtencionClinicaCitaId(Integer idCita);
}
