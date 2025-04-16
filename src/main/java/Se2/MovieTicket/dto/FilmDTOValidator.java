package Se2.MovieTicket.dto;

//import org.junit.jupiter.params.shadow.com.univocity.parsers.conversions.Validator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class FilmDTOValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return FilmDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FilmDTO filmDTO = (FilmDTO) target;

        // Validate film name
        if (StringUtils.isEmpty(filmDTO.getFilmName())) {
            errors.rejectValue("filmName", "NotEmpty", "Movie name cannot be empty");
        } else if (filmDTO.getFilmName().length() > 255) {
            errors.rejectValue("filmName", "Size", "Movie name must be less than 255 characters");
        }

        // Validate film description
        if (StringUtils.isEmpty(filmDTO.getFilmDescription())) {
            errors.rejectValue("filmDescription", "NotEmpty", "Movie description cannot be empty");
        }

        // Validate release date
        if (filmDTO.getReleaseDate() == null) {
            errors.rejectValue("releaseDate", "NotNull", "Release date cannot be empty");
        }

        // Validate duration
        if (filmDTO.getDuration() == null || filmDTO.getDuration() <= 0) {
            errors.rejectValue("duration", "Positive", "Duration must be a positive number");
        }

        // Validate age limit
        if (filmDTO.getAgeLimit() == null || filmDTO.getAgeLimit() < 0) {
            errors.rejectValue("ageLimit", "Min", "Age limit must be a non-negative number");
        }

        // Validate categories
        if (filmDTO.getCategoryNames() == null || filmDTO.getCategoryNames().isEmpty()) {
            errors.rejectValue("categoryNames", "NotEmpty", "At least one category must be selected");
        }

        // Validate country
        if (StringUtils.isEmpty(filmDTO.getCountry())) {
            errors.rejectValue("country", "NotEmpty", "Country must be selected");
        }

        // Validate directors
        if (filmDTO.getDirectorNames() == null || filmDTO.getDirectorNames().isEmpty()) {
            errors.rejectValue("directorNames", "NotEmpty", "At least one director must be selected");
        }

        // Validate actors
        if (filmDTO.getActorNames() == null || filmDTO.getActorNames().isEmpty()) {
            errors.rejectValue("actorNames", "NotEmpty", "At least one actor must be selected");
        }
    }
}