package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.ActorDTO;
import Se2.MovieTicket.model.Actor;
import Se2.MovieTicket.repository.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActorService {
    @Autowired
    private ActorRepository actorRepository;

    public List<Actor> getAllActors() {
        return actorRepository.findAll();
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