package co.com.ebsa.ebsa_nexus.domain.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "areas")
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    private AreaCode code;

    @Column(length = 45)
    private String name;

    @Column(length = 45)
    private String description;

    public enum AreaCode {
        FACTURACION,
        CARTERA,
        PERDIDAS
    }
}
