package fr.progilone.pgcn.web.rest.administration.utils;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.service.storage.FileCleaningManager;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants;

@RestController
@RequestMapping(value = "/api/rest/filecleaning")
public class FileCleaningController extends AbstractRestController {
    
    private static final Logger LOG = LoggerFactory.getLogger(FileCleaningController.class);
    
    private final FileCleaningManager fileCleaningManager;
    
    
    @Autowired
    public FileCleaningController(final FileCleaningManager fileCleaningManager) {
        
        super();
        this.fileCleaningManager = fileCleaningManager;
        
    }
    
    /**
     * Recherche les fichiers binaires orphelins => delete
     * 
     * @param request
     * @param libraryId
     * @return
     * @throws PgcnException
     */
    @RequestMapping(value = "/deleteorphans", method = RequestMethod.GET)
    @Timed
    @RolesAllowed(AuthorizationConstants.FILES_GEST_HAB0)
    public ResponseEntity<?> initDocCheckHistory(final HttpServletRequest request, 
                                                 @RequestParam(name = "library") final String libraryId) throws PgcnException {
        
        fileCleaningManager.cleanOrphanFiles(libraryId);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
