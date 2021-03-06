package fr.progilone.pgcn.service.es;

import fr.progilone.pgcn.domain.AbstractDomainObject_;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport;
import fr.progilone.pgcn.repository.es.EsCinesReportRepository;
import fr.progilone.pgcn.repository.exchange.cines.CinesReportRepository;
import fr.progilone.pgcn.service.util.transaction.TransactionService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EsCinesReportService extends AbstractElasticsearchOperations<CinesReport> {

    private static final Logger LOG = LoggerFactory.getLogger(EsCinesReportService.class);

    private final CinesReportRepository cinesReportRepository;
    private final EsCinesReportRepository esCinesReportRepository;
    private final TransactionService transactionService;

    @Value("${elasticsearch.bulk_size}")
    private Integer bulkSize;

    @Autowired
    public EsCinesReportService(final CinesReportRepository cinesReportRepository,
                                final EsCinesReportRepository esCinesReportRepository,
                                final TransactionService transactionService) {
        this.cinesReportRepository = cinesReportRepository;
        this.esCinesReportRepository = esCinesReportRepository;
        this.transactionService = transactionService;
    }

    /**
     * Indexation de la notice dans le moteur de recherche
     *
     * @param identifier
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public CinesReport index(final String identifier) {
        final CinesReport report = cinesReportRepository.findOne(identifier);
        if (report != null) {
            esCinesReportRepository.indexEntity(report);
            return report;
        }
        return null;
    }

    /**
     * Indexation de la notice dans le moteur de recherche
     *
     * @param identifiers
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Iterable<CinesReport> index(final List<String> identifiers) {
        if (CollectionUtils.isEmpty(identifiers)) {
            return Collections.emptyList();
        }
        final List<CinesReport> reports = cinesReportRepository.findAllByIdentifierIn(identifiers);
        final List<CinesReport> filteredReports =
            reports.stream().filter(rec -> StringUtils.isNotEmpty(rec.getDocUnitId())).collect(Collectors.toList());

        if (reports.isEmpty()) {
            return Collections.emptyList();
        }
        esCinesReportRepository.indexEntities(filteredReports);
        return filteredReports;
    }

    /**
     * Suppression de la notice du moteur de recherche
     *
     * @param report
     */
    @Override
    public void delete(final CinesReport report) {
        esCinesReportRepository.deleteEntity(report);
    }

    @Override
    public void delete(final Collection<CinesReport> reports) {
        esCinesReportRepository.deleteEntities(reports);
    }

    /**
     * Réindexation de toutes les notices disponibles
     *
     * @param index
     * @return
     */
    public long reindex(final String index) {
        long nbImported = 0;
        Page<CinesReport> pageOfObjects = null;    // Chargement des objets par page de bulkSize éléments

        do {
            final TransactionStatus status = transactionService.startTransaction(true);

            // Chargement des objets
            final Pageable pageable = pageOfObjects == null ?
                                      new PageRequest(0, bulkSize, Sort.Direction.ASC, AbstractDomainObject_.identifier.getName()) :
                                      pageOfObjects.nextPageable();
            pageOfObjects = cinesReportRepository.findAllByDocUnitState(DocUnit.State.AVAILABLE, pageable);

            // Traitement des unités documentaires
            final List<CinesReport> entities = pageOfObjects.getContent();
            esCinesReportRepository.index(index, entities);

            transactionService.commitTransaction(status);

            nbImported += entities.size();
            LOG.trace("{} / {} éléments indexés", nbImported, pageOfObjects.getTotalElements());

        } while (pageOfObjects.hasNext());

        return nbImported;
    }
}
