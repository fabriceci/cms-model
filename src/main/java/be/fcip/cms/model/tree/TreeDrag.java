package be.fcip.cms.model.tree;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TreeDrag {
    private Long dropKey;
    private Long dragKey;
    private int position;
    private Boolean dragIn;
}
