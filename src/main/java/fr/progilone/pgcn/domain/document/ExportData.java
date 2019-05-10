package fr.progilone.pgcn.domain.document;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonSubTypes;

import fr.progilone.pgcn.domain.AbstractDomainObject;

@Entity
@Table(name = ExportData.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_export_data", value = ExportData.class)})
public class ExportData extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_export_data";

    /**
     * Liste des propriétés de la notice (DC, DCq, Custom)
     */
    @OneToMany(mappedBy = "record", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<ExportProperty> properties = new HashSet<>();

    /**
     * Unité documentaire rattachée
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_unit")
    private DocUnit docUnit;

    public DocUnit getDocUnit() {
        return docUnit;
    }

    public void setDocUnit(final DocUnit docUnit) {
        this.docUnit = docUnit;
    }

    public Set<ExportProperty> getProperties() {
        return properties;
    }

    public void setProperties(final Set<ExportProperty> properties) {
        this.properties.clear();
        if (properties != null) {
            properties.forEach(this::addProperty);
        }
    }

    public void addProperty(final ExportProperty property) {
        if (property != null) {
            this.properties.add(property);
            property.setRecord(this);
        }
    }
}