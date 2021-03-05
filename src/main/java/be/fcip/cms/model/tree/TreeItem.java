package be.fcip.cms.model.tree;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = {"key", "title"})
@NoArgsConstructor
@AllArgsConstructor
public class TreeItem {

    private Long key;
    private String title;
    private String type;
    private List<TreeItem> children = new ArrayList<>();
}
