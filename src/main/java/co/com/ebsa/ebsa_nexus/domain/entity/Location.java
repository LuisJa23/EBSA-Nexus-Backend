package co.com.ebsa.ebsa_nexus.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un municipio o ubicación geográfica.
 * Utilizada para estandarizar la asignación de municipios a novedades.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-11-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Location")
public class Location {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id_location")
    private Long idLocation;
    
    @Column(length = 45)
    private String name;
    
    @Column(length = 45)
    private String details;
}
