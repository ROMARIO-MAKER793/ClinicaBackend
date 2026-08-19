package com.cibertec.denticore.ventas.repositories;

import com.cibertec.denticore.ventas.entities.TransaccionComercial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransaccionComercialRepository extends JpaRepository<TransaccionComercial, Integer> {

    Integer countBySerie(String serie);
}