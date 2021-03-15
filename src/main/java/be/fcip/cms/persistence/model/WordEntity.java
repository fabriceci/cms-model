package be.fcip.cms.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "word", indexes = @Index(columnList = "wordKey", unique = true))
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "wordKey"})
public class WordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Column(nullable = false, unique = true)
    private String wordKey;
    private String domain;

    @OneToMany(mappedBy = "word",cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKeyColumn(name="language")
    private Map< String, WordTranslationEntity> translations = new HashMap<>();

    public void setTranslation(WordTranslationEntity translation){
        translation.setWord(this);
        translations.put(translation.getLanguage(), translation);
    }
}