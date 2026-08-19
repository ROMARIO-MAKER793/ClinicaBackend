package com.cibertec.denticore.security.entities;

import com.cibertec.denticore.clinica.entities.HistoriaClinica;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "paciente", schema = "seguridad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paciente {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "grupo_sanguineo", length = 5)
    private String grupoSanguineo;

    @Column(name = "alergias", columnDefinition = "TEXT")
    private String alergias;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(nullable = false)
    private Boolean activo;

    @OneToOne(mappedBy = "paciente", fetch = FetchType.LAZY)
    private HistoriaClinica historiaClinica;
}
