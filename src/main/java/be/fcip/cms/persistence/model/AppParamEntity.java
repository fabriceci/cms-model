package be.fcip.cms.persistence.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "app_param")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AppParamEntity {

    @Id
    @Column(nullable = false, unique = true)
    @Access(AccessType.PROPERTY)
    private String id;

    private String value;
}
