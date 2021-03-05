package be.fcip.cms.persistence.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "web_content_rule")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class WebContentRuleTemplateEntity {

    @Id
    @Access(AccessType.PROPERTY)
    private String name;
    private Long templateId;
}
