package be.fcip.cms.persistence.model;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "word_translation")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "value"})
public class WordTranslationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Column(nullable = false)
    private String value;
    @ManyToOne(fetch = FetchType.LAZY)
    private WordEntity word;
    private String language;

    public WordTranslationEntity() {
    }

    public WordTranslationEntity(String language, String value, WordEntity word) {
        this.language = language;
        this.value = value;
        this.word = word;
    }
}
