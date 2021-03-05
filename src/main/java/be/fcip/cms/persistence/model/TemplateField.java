package be.fcip.cms.persistence.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TemplateField {
    private String type;
    private String title;
    private String namespace;
    private String hint;
    private boolean isArray;
    private int position = 1;
}
