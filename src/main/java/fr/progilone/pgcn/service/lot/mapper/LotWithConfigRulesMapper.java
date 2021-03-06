package fr.progilone.pgcn.service.lot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.dto.lot.LotWithConfigRulesDTO;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.service.administration.mapper.CinesPACMapper;
import fr.progilone.pgcn.service.administration.mapper.InternetArchiveCollectionMapper;
import fr.progilone.pgcn.service.administration.mapper.OmekaListMapper;
import fr.progilone.pgcn.service.administration.mapper.SimpleViewsFormatConfigurationMapper;
import fr.progilone.pgcn.service.checkconfiguration.mapper.CheckConfigurationMapper;
import fr.progilone.pgcn.service.document.mapper.SimpleDocUnitMapper;
import fr.progilone.pgcn.service.ftpconfiguration.mapper.SimpleFTPConfigurationMapper;
import fr.progilone.pgcn.service.library.mapper.LibraryMapper;
import fr.progilone.pgcn.service.ocrlangconfiguration.mapper.OcrLanguageMapper;
import fr.progilone.pgcn.service.project.mapper.SimpleProjectMapper;
import fr.progilone.pgcn.service.user.mapper.AddressMapper;
import fr.progilone.pgcn.service.user.mapper.UserMapper;
import fr.progilone.pgcn.service.workflow.mapper.SimpleWorkflowMapper;

@Mapper(uses = {AddressMapper.class,
                LibraryMapper.class,
                SimpleDocUnitMapper.class,
                SimpleProjectMapper.class,
                SimpleFTPConfigurationMapper.class,
                CheckConfigurationMapper.class,
                SimpleViewsFormatConfigurationMapper.class,
                UserMapper.class,
                OcrLanguageMapper.class,
    InternetArchiveCollectionMapper.class,
    CinesPACMapper.class,
    SimpleWorkflowMapper.class,
    OmekaListMapper.class})
public interface LotWithConfigRulesMapper {

    LotWithConfigRulesMapper INSTANCE = Mappers.getMapper(LotWithConfigRulesMapper.class);

    LotWithConfigRulesDTO lotToLotWithConfigRulesDTO(Lot lot);

}
