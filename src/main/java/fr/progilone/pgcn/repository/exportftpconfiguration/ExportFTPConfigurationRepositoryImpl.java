package fr.progilone.pgcn.repository.exportftpconfiguration;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;

import fr.progilone.pgcn.domain.exportftpconfiguration.ExportFTPConfiguration;
import fr.progilone.pgcn.domain.exportftpconfiguration.QExportFTPConfiguration;

public class ExportFTPConfigurationRepositoryImpl implements ExportFTPConfigurationRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<ExportFTPConfiguration> search(final String search, final List<String> libraries, final Pageable pageable) {

        final QExportFTPConfiguration configuration = QExportFTPConfiguration.exportFTPConfiguration;

        final BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(search)) {
            final BooleanExpression nameFilter = configuration.label.containsIgnoreCase(search);
            builder.andAnyOf(nameFilter);
        }
        if (libraries != null && !libraries.isEmpty()) {
            final BooleanExpression libraryFilter = configuration.library.identifier.in(libraries);
            builder.and(libraryFilter);
        }

        final JPQLQuery baseQuery = new JPAQuery(em);
        final JPQLQuery countQuery = new JPAQuery(em);

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset())
                     .limit(pageable.getPageSize());
        }

        final List<String> librariesIdentifiers = countQuery.from(configuration)
                                                            .leftJoin(configuration.library)
                                                            .groupBy(configuration.identifier)
                                                            .where(builder.getValue()).distinct().list(configuration.identifier);
        final long total = librariesIdentifiers.size();

        final List<ExportFTPConfiguration> result = baseQuery.from(configuration)
                                                 .leftJoin(configuration.library).fetch()
                                                 .where(builder.getValue())
                                                 .orderBy(configuration.label.asc())
                                                 .distinct().list(configuration);

        return new PageImpl<>(result, pageable, total);
    }
    
}
