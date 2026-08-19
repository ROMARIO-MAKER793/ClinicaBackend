package com.cibertec.denticore.clinica.repositories;

import com.cibertec.denticore.clinica.entities.ElementoOdontograma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ElementoOdontogramaRepository extends JpaRepository<ElementoOdontograma, Integer> {

    List<ElementoOdontograma> findByActivoTrueOrderByCategoriaAscNombreAsc();
}
