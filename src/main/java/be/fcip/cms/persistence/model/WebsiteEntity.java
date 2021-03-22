package be.fcip.cms.persistence.model;

import be.fcip.cms.persistence.service.IWebsiteService;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "website")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name"})
@NoArgsConstructor
@AllArgsConstructor
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class),
})
public class WebsiteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, updatable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true)
    private String slug;

    private boolean https; // used for full url

    private String baseUrl;

    private String image;

    boolean maintenance = false;

    @NotNull @Column(nullable = false) private String mailTo;
    @NotNull @Column(nullable = false) private String mailFrom;
    @NotNull @Column(nullable = false) private String mailName;

    @Lob
    private String template;
    @Lob
    private String errorTemplate;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private Map<String, String> property;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private Map<String, Map<String, String>> translatableProperty;

    public void addProperty(String key, String value) {
        if(property == null) property = new HashMap<>();
        property.put(key, value);
    }

    public String findProperty(String key){
        return property == null ? null : property.get(key);
    }

    public void addTranslatableProperty(String key, String lang, String value){
        if(translatableProperty == null) translatableProperty = new HashMap<>();
        if(!translatableProperty.containsKey(key)) translatableProperty.put(key, new HashMap<>());
        Map<String, String> properties = translatableProperty.get(key);
        properties.put(lang, value);
    }

    public String findTranslatableProperty(String key, String lang){
        if(translatableProperty == null) translatableProperty = new HashMap<>();
        if(!translatableProperty.containsKey(key)) return null;
        translatableProperty.put(lang, new HashMap<>());
        return translatableProperty.get(key).get(lang);
    }
    public String findTranslatableProperty(String key, String lang, String defaultLang){
        String result = findTranslatableProperty(key, lang);
        return result != null ? result : translatableProperty.get(key).get(defaultLang);
    }

    public List<String> getDestinationEmails(){
        String[] split = mailTo.split(";");
        return Arrays.stream(split).collect(Collectors.toList());
    }

    public String getFullUrl(){
        return https ? "https://" + baseUrl : "http://" + baseUrl;
    }

    public Map<String, String> getSeoMap(String locale){
        Map<String, String> result = new HashMap<>();
        result.put("url", baseUrl);
        if(translatableProperty == null) return result;

        for (String seoFieldKey : IWebsiteService.SEO_FIELD_KEYS) {
            if(translatableProperty.containsKey(seoFieldKey)){
                result.put(seoFieldKey, translatableProperty.get(seoFieldKey).get(locale));
            } else {
                result.put(seoFieldKey, null);
            }

        }
        return result;
    }
}
