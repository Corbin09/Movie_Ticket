//package Se2.MovieTicket.controllers;
//
//import Se2.MovieTicket.dto.RoomDTO;
//import Se2.MovieTicket.model.Room;
//import Se2.MovieTicket.service.RoomService;
//import Se2.MovieTicket.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/admin/rooms")
//public class RoomController {
//    @Autowired
//    private RoomService roomService;
//
//    @Autowired
//    private UserService userService;
//
//    @GetMapping
//    public ResponseEntity<List<Room>> getAllRooms() {
//        if (!userService.hasRole("Admin") && !userService.hasRole("User  ")) {
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
//        List<Room> rooms = roomService.getAllRooms();
//        return rooms.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(rooms, HttpStatus.OK);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Room> getRoomById(@PathVariable("id") Long id) {
//        if (!userService.hasRole("Admin") && !userService.hasRole("User  ")) {
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
//        return roomService.getRoomById(id)
//                .map(room -> new ResponseEntity<>(room, HttpStatus.OK))
//                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }
//
//    @PostMapping
//    public ResponseEntity<Room> createRoom(@RequestBody RoomDTO roomDTO) {
//        if (!userService.hasRole("Admin")) {
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
//        Room newRoom = roomService.createRoom(roomDTO);
//        return new ResponseEntity<>(newRoom, HttpStatus.CREATED);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Room> updateRoom(@PathVariable("id") Long id, @RequestBody RoomDTO roomDTO) {
//        if (!userService.hasRole("Admin")) {
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
//        Room updatedRoom = roomService.updateRoom(id, roomDTO);
//        return updatedRoom != null ? new ResponseEntity<>(updatedRoom, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<HttpStatus> deleteRoom(@PathVariable("id") Long id) {
//        if (!userService.hasRole("Admin")) {
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
//        roomService.deleteRoom(id);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//    @GetMapping("/filter")
//    public ResponseEntity<List<Room>> filterRooms(@RequestParam(required = false) String name) {
//        List<Room> rooms = roomService.filterRooms(name);
//        return new ResponseEntity<>(rooms, HttpStatus.OK);
//    }
//}


package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.RoomDTO;
import Se2.MovieTicket.impl.UserDetailsImpl;
import Se2.MovieTicket.model.Cinema;
import Se2.MovieTicket.model.Room;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.service.CinemaService;
import Se2.MovieTicket.service.RoomService;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @Autowired
    private CinemaService cinemaService;

    @GetMapping("/manage-rooms")
    public String manageRooms(
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String searchField,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            Model model,
            HttpServletRequest request) {

        // Check if user has admin role
//        if (!userService.hasRole("ROLE_ADMIN")) {
//            return "redirect:/access-denied";
//        }

        // Add user to model
        addUserToModel(model, request);

        // Get all cinemas for the dropdown filter
        List<Cinema> cinemas = cinemaService.getAllCinemas();
        model.addAttribute("cinemas", cinemas);

        // Create pageable object for database pagination
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        // Get rooms with pagination directly from database
        Page<Room> roomsPage;

        try {
            if (searchText != null && !searchText.isEmpty() && searchField != null && !searchField.isEmpty()) {
                // Search rooms by specific field with pagination
                roomsPage = roomService.searchRoomsByFieldPaginated(searchField, searchText, pageable);
            } else {
                // Get all rooms with pagination
                roomsPage = roomService.getAllRoomsPaginated(pageable);
            }

            // Get seat counts for displayed rooms in a single query
            Map<Long, Long> seatCounts = roomService.getSeatCountsForRooms(
                    roomsPage.getContent().stream()
                            .map(Room::getRoomId)
                            .collect(Collectors.toList())
            );

            model.addAttribute("seatCounts", seatCounts);
            model.addAttribute("rooms", roomsPage.getContent());

            // Add pagination parameters
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalItems", roomsPage.getTotalElements());
            model.addAttribute("totalPages", roomsPage.getTotalPages());

        } catch (Exception e) {
            model.addAttribute("rooms", new ArrayList<>());
            model.addAttribute("errorMessage", "Error fetching rooms: " + e.getMessage());
            model.addAttribute("currentPage", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalItems", 0);
            model.addAttribute("totalPages", 0);
        }

        // Pass the selected search options to the view
        model.addAttribute("currentSearchField", searchField);
        model.addAttribute("currentSearchText", searchText);

        // Add currPage attribute for sidebar active menu
        model.addAttribute("currPage", "manage-rooms");

        return "manage-rooms";
    }


    @PostMapping("/delete-rooms")
    @Transactional
    public String deleteRooms(@RequestParam("roomIds") List<Long> roomIds,
                              RedirectAttributes redirectAttributes) {
        // Check if user has admin role
//        if (!userService.hasRole("ROLE_ADMIN")) {
//            return "redirect:/access-denied";
//        }

        try {
            int deletedCount = roomService.deleteRoomsByIds(roomIds);
            redirectAttributes.addFlashAttribute("successMessage",
                    deletedCount + " room(s) successfully deleted.");

            // This will set the deleteSuccess variable directly in the model
            redirectAttributes.addFlashAttribute("deleteSuccess", true);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error deleting rooms: " + e.getMessage());
        }

        return "redirect:/manage-rooms";
    }


    @GetMapping("/add-room")
    public String addRoomForm(Model model, HttpServletRequest request) {
        // Check if user has admin role
//        if (!userService.hasRole("ROLE_ADMIN")) {
//            return "redirect:/access-denied";
//        }

        // Add user to model
        addUserToModel(model, request);

        // Add necessary attributes for the form
        model.addAttribute("room", new Room());
        model.addAttribute("cinemas", cinemaService.getAllCinemas());
        model.addAttribute("currPage", "manage-rooms");

        return "addroom";
    }



    @PostMapping("/rooms/save")
    public String saveRoom(@Valid @ModelAttribute("room") Room room,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model,
                           HttpServletRequest request) {
        // Get user from session or SecurityContext
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

        // Check if user has admin role
//        if (!userService.hasRole("ROLE_ADMIN")) {
//            return "redirect:/access-denied";
//        }

        // Validate the input
        if (bindingResult.hasErrors()) {
            model.addAttribute("cinemas", cinemaService.getAllCinemas());
            model.addAttribute("currPage", "manage-rooms");
            return "addroom";
        }

        try {
            // Save the room
            roomService.saveRoom(room);
            model.addAttribute("successMessage", "Success! Room has been added successfully.");
            model.addAttribute("cinemas", cinemaService.getAllCinemas());
            model.addAttribute("currPage", "manage-rooms");
            return "addroom";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to add room: " + e.getMessage());
            model.addAttribute("cinemas", cinemaService.getAllCinemas());
            model.addAttribute("currPage", "manage-rooms");
            return "addroom";
        }
    }

    @GetMapping("/edit-room")
    public String showEditRoomForm(@RequestParam Long id,
                                   Model model,
                                   HttpServletRequest request) {
        // Check if user has admin role
//        if (!userService.hasRole("ROLE_ADMIN")) {
//            return "redirect:/access-denied";
//        }

        // Add user to model
        addUserToModel(model, request);

        // Get the room by ID with eager loading of necessary relations
        Optional<Room> roomOptional = roomService.getRoomByIdWithDetails(id);

        if (roomOptional.isEmpty()) {
            // Room not found, redirect with error message
            return "redirect:/manage-rooms?error=Room+not+found";
        }

        // Add room to the model
        model.addAttribute("room", roomOptional.get());

        // Add cinemas for the dropdown (optimize by fetching only necessary fields)
        model.addAttribute("cinemas", cinemaService.getCinemasBasicInfo());

        // Set current page for navigation
        model.addAttribute("currPage", "manage-rooms");

        return "editroom";
    }

    @PostMapping("/edit-room")
    public String updateRoom(@Valid @ModelAttribute("room") Room room,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model,
                             HttpServletRequest request) {
        // Check if user has admin role
//        if (!userService.hasRole("ROLE_ADMIN")) {
//            return "redirect:/access-denied";
//        }

        // Add user to model
        addUserToModel(model, request);

        // Add cinemas for the dropdown (needed if returning to the form page)
        model.addAttribute("cinemas", cinemaService.getCinemasBasicInfo());
        model.addAttribute("currPage", "manage-rooms");

        // Validate the input
        if (bindingResult.hasErrors()) {
            return "editroom";
        }

        try {
            // Update the room - use a specialized method to avoid unnecessary operations
            roomService.updateRoomDirect(room);

            // Set success attributes
            model.addAttribute("successMessage", "Success! Room has been updated successfully.");
            model.addAttribute("showSuccessOverlay", true);

            return "editroom";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to update room: " + e.getMessage());
            return "editroom";
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
