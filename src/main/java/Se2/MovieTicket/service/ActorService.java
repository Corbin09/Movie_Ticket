package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.ActorDTO;
import Se2.MovieTicket.model.Actor;
import Se2.MovieTicket.repository.ActorRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ActorService {
    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private EntityManager em;
    public List<Actor> getAllActors() {
        return actorRepository.findAll();
    }

    public List<Actor> filterActors(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Actor> cq = cb.createQuery(Actor.class);
        Root<Actor> actor = cq.from(Actor.class);

        List<Predicate> predicates = new ArrayList<>();
        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(actor.get("actorName"), "%" + name + "%"));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }

    public List<Actor> searchActors(String name, Long actorId) {
        if (actorId != null) {
            return actorRepository.findById(actorId).map(List::of).orElse(List.of());
        } else if (name != null && !name.isEmpty()) {
            return actorRepository.findByActorNameContaining(name);
        }
        return actorRepository.findAll(); // Trả về tất cả nếu không có điều kiện nào
    }

    public Optional<Actor> getActorById(Long id) {
        return actorRepository.findById(id);
    }

    public Actor createActor(ActorDTO actorDTO) {
        Actor actor = new Actor();
        actor.setActorName(actorDTO.getActorName());
        actor.setActorImg(actorDTO.getActorImg());
        actor.setActorDescription(actorDTO.getActorDescription());
        return actorRepository.save(actor);
    }

    public Actor updateActor(Long id, ActorDTO actorDTO) {
        Optional<Actor> actorData = actorRepository.findById(id);
        if (actorData.isPresent()) {
            Actor actor = actorData.get();
            actor.setActorName(actorDTO.getActorName());
            actor.setActorImg(actorDTO.getActorImg());
            actor.setActorDescription(actorDTO.getActorDescription());
            return actorRepository.save(actor);
        }
        return null;
    }

    public void deleteActor(Long id) {
        actorRepository.deleteById(id);
    }
}