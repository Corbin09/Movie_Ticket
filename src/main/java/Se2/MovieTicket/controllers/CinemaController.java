//package Se2.MovieTicket.controllers;
//
//import Se2.MovieTicket.dto.CinemaDTO;
//import Se2.MovieTicket.model.Cinema;
//import Se2.MovieTicket.service.CinemaService;
//import Se2.MovieTicket.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/admin/cinemas")
//public class CinemaController {
//    @Autowired
//    private CinemaService cinemaService;
//
//    @Autowired
//    private UserService userService;
//
//    @GetMapping
//    public ResponseEntity<List<Cinema>> getAllCinemas() {
//        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
//        List<Cinema> cinemas = cinemaService.getAllCinemas();
//        return cinemas.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(cinemas, HttpStatus.OK);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Cinema> getCinemaById(@PathVariable("id") Long id) {
//        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
//        return cinemaService.getCinemaById(id)
//                .map(cinema -> new ResponseEntity<>(cinema, HttpStatus.OK))
//                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }
//
//    @PostMapping
//    public ResponseEntity<Cinema> createCinema(@RequestBody CinemaDTO cinemaDTO) {
//        if (!userService.hasRole("Admin")) {
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
//        Cinema newCinema = cinemaService.createCinema(cinemaDTO);
//        return new ResponseEntity<>(newCinema, HttpStatus.CREATED);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Cinema> updateCinema(@PathVariable("id") Long id, @RequestBody CinemaDTO cinemaDTO) {
//        if (!userService.hasRole("Admin")) {
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
//        Cinema updatedCinema = cinemaService.updateCinema(id, cinemaDTO);
//        return updatedCinema != null ? new ResponseEntity<>(updatedCinema, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<HttpStatus> deleteCinema(@PathVariable("id") Long id) {
//        if (!userService.hasRole("Admin")) {
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
//        cinemaService.deleteCinema(id);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<List<Cinema>> searchCinemas(@RequestParam(required = false) String name, @RequestParam(required = false) String address) {
//        List<Cinema> cinemas = cinemaService.searchCinemas(name, address);
//        return cinemas.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(cinemas, HttpStatus.OK);
//    }
//
//    @GetMapping("/filter")
//    public ResponseEntity<List<Cinema>> filterCinemas(@RequestParam(required = false) String name, @RequestParam(required = false) String address) {
//        List<Cinema> cinemas = cinemaService.filterCinemas(name, address);
//        return new ResponseEntity<>(cinemas, HttpStatus.OK);
//    }
//}
package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.CinemaDTO;
import Se2.MovieTicket.impl.UserDetailsImpl;
import Se2.MovieTicket.model.Cinema;
import Se2.MovieTicket.model.CinemaCluster;
import Se2.MovieTicket.model.Region;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.service.CinemaClusterService;
import Se2.MovieTicket.service.CinemaService;
import Se2.MovieTicket.service.RegionService;
import Se2.MovieTicket.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class CinemaController {
    @Autowired
    private CinemaService cinemaService;

    @Autowired
    private UserService userService;

    @Autowired
    private CinemaClusterService cinemaClusterService;

    @Autowired
    private RegionService regionService;

    @GetMapping("/manage-cinemas")
    public String manageCinemas(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String complex,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            Model model,
            HttpServletRequest request) {



        // Add user to model
        addUserToModel(model, request);

        // Get all complexes for the dropdown filter
        List<CinemaCluster> complexes = cinemaClusterService.getAllCinemaClusters();
        model.addAttribute("complexes", complexes);

        // Create pageable object for database pagination
        Pageable pageable = PageRequest.of(page - 1, size);

        // Get cinemas with pagination
        Page<Cinema> cinemasPage;

        try {
            if (search != null && !search.isEmpty()) {
                // Search cinemas by name or address
                cinemasPage = cinemaService.searchCinemasByNameOrAddressPaginated(search, pageable);
                model.addAttribute("searchTerm", search);
            } else if (complex != null && !complex.isEmpty()) {
                // Filter cinemas by complex
                cinemasPage = cinemaService.getCinemasByComplexNamePaginated(complex, pageable);
                model.addAttribute("selectedComplex", complex);
            } else {
                // Get all cinemas with pagination
                cinemasPage = cinemaService.getAllCinemasPaginated(pageable);
            }

            List<CinemaDTO> cinemaDTOs = mapCinemasWithComplexInfo(cinemasPage.getContent());

            model.addAttribute("cinemas", cinemaDTOs);

            // Add pagination parameters
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalCinemas", cinemasPage.getTotalElements());
            model.addAttribute("totalPages", cinemasPage.getTotalPages());
            model.addAttribute("pageSizes", Arrays.asList(5, 10, 20, 50));

        } catch (Exception e) {
            model.addAttribute("cinemas", new ArrayList<>());
            model.addAttribute("errorMessage", "Error fetching cinemas: " + e.getMessage());
            model.addAttribute("currentPage", 1);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalCinemas", 0);
            model.addAttribute("totalPages", 0);
        }

        // Add currPage attribute for sidebar active menu
        model.addAttribute("currPage", "manage-cinemas");

        return "manage-cinemas";
    }

//    private List<CinemaDTO> mapCinemasWithComplexInfo(List<Cinema> content) {
//    }

    private List<CinemaDTO> mapCinemasWithComplexInfo(List<Cinema> cinemas) {
        return cinemas.stream().map(cinema -> {
            CinemaDTO dto = new CinemaDTO();
            dto.setCinemaId(cinema.getCinemaId());
            dto.setCinemaName(cinema.getCinemaName());
            dto.setAddress(cinema.getAddress());

            // Set complex-related properties
            if (cinema.getCinemaCluster() != null) {
                dto.setClusterId(cinema.getCinemaCluster().getClusterId());
                dto.setComplexName(cinema.getCinemaCluster().getClusterName());
                dto.setComplexColor(getColorClassForCluster(cinema.getCinemaCluster()));
            }

            return dto;
        }).collect(Collectors.toList());
    }
    private String getColorClassForCluster(CinemaCluster cluster) {
        // Implement a simple hash-based color assignment or a predefined mapping
        String[] colors = {"blue", "green", "orange", "purple", "red"};
        return colors[Math.abs(cluster.getClusterId().hashCode()) % colors.length];
    }

    @PostMapping("/cinemas/delete")
    @Transactional
    public String deleteCinemas(@RequestParam("selectedIds") List<Long> cinemaIds,
                                RedirectAttributes redirectAttributes) {

        try {
            int deletedCount = cinemaService.deleteCinemasByIds(cinemaIds);
            redirectAttributes.addFlashAttribute("successMessage",
                    deletedCount + " cinema(s) successfully deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error deleting cinemas: " + e.getMessage());
        }

        return "redirect:/manage-cinemas";
    }

    @GetMapping("/cinemas/add")
    public String addCinemaForm(Model model, HttpServletRequest request) {

        // Add user to model
        addUserToModel(model, request);

        // Add necessary attributes for the form
        model.addAttribute("cinema", new Cinema());
        model.addAttribute("clusters", cinemaClusterService.getAllCinemaClusters());
        model.addAttribute("regions", regionService.getAllRegions());
        model.addAttribute("currPage", "manage-cinemas");

        return "add-cinema";
    }

    @PostMapping("/cinemas/save")
    public String saveCinema(@Valid @ModelAttribute("cinema") Cinema cinema,
                             BindingResult bindingResult,
                             Model model,
                             HttpServletRequest request) {


        // Validate input fields
        if (cinema.getCinemaName() == null || cinema.getCinemaName().trim().isEmpty()) {
            bindingResult.rejectValue("cinemaName", "error.cinema", "Cinema name cannot be empty");
        }

        if (cinema.getAddress() == null || cinema.getAddress().trim().isEmpty()) {
            bindingResult.rejectValue("address", "error.cinema", "Cinema address cannot be empty");
        }

        if (cinema.getCinemaCluster() == null || cinema.getCinemaCluster().getClusterId() == null) {
            bindingResult.rejectValue("cinemaCluster", "error.cinema", "Please select a cinema complex");
        }

        // Add validation for region
        if (cinema.getRegion() == null || cinema.getRegion().getRegionId() == null) {
            bindingResult.rejectValue("region", "error.cinema", "Please select a region");
        }

        // Check if there are validation errors
        if (bindingResult.hasErrors()) {
            // Add necessary attributes back to the form
            addUserToModel(model, request);
            model.addAttribute("clusters", cinemaClusterService.getAllCinemaClusters());
            model.addAttribute("regions", regionService.getAllRegions());
            model.addAttribute("currPage", "manage-cinemas");
            return "add-cinema";
        }

        // Save the cinema
        cinemaService.saveCinema(cinema);

        // Add success message to the same page to show the overlay
        model.addAttribute("successMessage", "Cinema has been added successfully.");

        // Return to the same page to show the success overlay
        addUserToModel(model, request);
        model.addAttribute("cinema", new Cinema()); // Reset form with new cinema object
        model.addAttribute("clusters", cinemaClusterService.getAllCinemaClusters());
        model.addAttribute("regions", regionService.getAllRegions());
        model.addAttribute("currPage", "manage-cinemas");

        return "add-cinema";
    }

    @GetMapping("/cinemas/edit")
    public String showEditCinemaForm(@RequestParam Long id,
                                     Model model,
                                     HttpServletRequest request) {

        // Add user to model
        addUserToModel(model, request);

        // Get the cinema by ID with eager loading of necessary relations
        Optional<Cinema> cinemaOptional = cinemaService.getCinemaByIdWithDetails(id);

        if (cinemaOptional.isEmpty()) {
            // Cinema not found, redirect with error message
            return "redirect:/manage-cinemas?error=Cinema+not+found";
        }

        // Add cinema to the model
        model.addAttribute("cinema", cinemaOptional.get());

        // Add cinema clusters and regions for the dropdown
        model.addAttribute("clusters", cinemaClusterService.getAllCinemaClusters());
        model.addAttribute("regions", regionService.getAllRegions());

        // Set current page for navigation
        model.addAttribute("currPage", "manage-cinemas");

        return "edit-cinema";
    }

    @PostMapping("/cinemas/update/{id}")
    public String updateCinema(@PathVariable("id") Long id,
                               @Valid @ModelAttribute("cinema") Cinema cinema,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model,
                               HttpServletRequest request) {
        cinema.setCinemaId(id);


        // Get full region object by ID before validation
        if (cinema.getRegion() != null && cinema.getRegion().getRegionId() != null) {
            Optional<Region> regionOptional = regionService.getRegionById(cinema.getRegion().getRegionId());
            if (regionOptional.isPresent()) {
                cinema.setRegion(regionOptional.get());
            }
        }

        // Get full cinema cluster object by ID before validation
        if (cinema.getCinemaCluster() != null && cinema.getCinemaCluster().getClusterId() != null) {
            Optional<CinemaCluster> clusterOptional = cinemaClusterService.getCinemaClusterById(cinema.getCinemaCluster().getClusterId());
            if (clusterOptional.isPresent()) {
                cinema.setCinemaCluster(clusterOptional.get());
            }
        }

        // Add user to model
        addUserToModel(model, request);

        // Add cinema clusters and regions for the dropdown (needed if returning to the form page)
        model.addAttribute("clusters", cinemaClusterService.getAllCinemaClusters());
        model.addAttribute("regions", regionService.getAllRegions());
        model.addAttribute("currPage", "manage-cinemas");

        // Simple validation - check required fields are not empty
        boolean hasCustomErrors = false;

        // Validate cinema name
        if (cinema.getCinemaName() == null || cinema.getCinemaName().trim().isEmpty()) {
            bindingResult.rejectValue("cinemaName", "error.cinema", "Cinema name cannot be empty");
            hasCustomErrors = true;
        }

        // Validate address
        if (cinema.getAddress() == null || cinema.getAddress().trim().isEmpty()) {
            bindingResult.rejectValue("address", "error.cinema", "Address cannot be empty");
            hasCustomErrors = true;
        }

        // Validate that a cinema cluster is selected
        if (cinema.getCinemaCluster() == null || cinema.getCinemaCluster().getClusterId() == null) {
            bindingResult.rejectValue("cinemaCluster", "error.cinema", "Cinema cluster must be selected");
            hasCustomErrors = true;
        }

        // Validate that a region is selected
        if (cinema.getRegion() == null || cinema.getRegion().getRegionId() == null) {
            bindingResult.rejectValue("region", "error.cinema", "Region must be selected");
            hasCustomErrors = true;
        }

        // Check if there are any errors (from @Valid annotation or custom validations)
        if (bindingResult.hasErrors() || hasCustomErrors) {
            model.addAttribute("errorMessage", "Please correct the errors in the form");
            return "edit-cinema";
        }

        try {
            // Update the cinema
            cinemaService.updateCinema(cinema);

            // Add success message and flag for overlay display
            model.addAttribute("successMessage", "Success! Cinema has been updated successfully.");
            model.addAttribute("showSuccessOverlay", true);

            return "edit-cinema";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to update cinema: " + e.getMessage());
            return "edit-cinema";
        }
    }

    // Add this method to your controller
    private void addUserToModel(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User sessionUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (sessionUser == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                sessionUser = userService.getUserById(userDetails.getId()).orElse(null);

                if (sessionUser != null && session != null) {
                    session.setAttribute("user", sessionUser);
                }
            }
        }

        if (sessionUser != null) {
            model.addAttribute("user", sessionUser);
        }
    }

}