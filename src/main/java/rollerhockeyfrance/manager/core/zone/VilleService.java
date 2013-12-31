package rollerhockeyfrance.manager.core.zone;

import java.util.List;

import com.google.inject.Inject;
import com.yammer.dropwizard.hibernate.UnitOfWork;

import rollerhockeyfrance.manager.core.common.GoogleMapsService;
import rollerhockeyfrance.manager.core.common.GoogleMapsService.Coordonnes;
import rollerhockeyfrance.manager.core.db.dao.zone.ZoneVilleDAO;
import rollerhockeyfrance.manager.core.db.entity.zone.ZoneVille;

public class VilleService {

    ZoneVilleDAO villeDAO;
    GoogleMapsService googleMapsService;
    
    @Inject
    public VilleService(ZoneVilleDAO villeDAO, GoogleMapsService googleMapsService) {
        this.villeDAO = villeDAO;
        this.googleMapsService = googleMapsService;
    }
    
    @UnitOfWork
    public ZoneVille create(ZoneVille ville) {
        
        Coordonnes coordonnes = googleMapsService.getCoordonnees(ville);
        ville.setLatitude(coordonnes.getLatitude());
        ville.setLongitude(coordonnes.getLontitude());
        
        return villeDAO.create(ville); 
    }
    
    @UnitOfWork
    public ZoneVille read(Long id) {
        return villeDAO.getById(id);
    }
    
    @UnitOfWork
    public List<ZoneVille> findAll() {
        return villeDAO.getList();
    }
    
    @UnitOfWork
    public ZoneVille update(ZoneVille ville) {
        return villeDAO.update(ville);
    }
    
    @UnitOfWork
    public ZoneVille delete(Long id) {
        return villeDAO.deleteById(id);
    }
    
}
