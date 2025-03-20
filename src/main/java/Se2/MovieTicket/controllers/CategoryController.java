package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.CategoryDTO;
import Se2.MovieTicket.model.Category;
import Se2.MovieTicket.service.CategoryService;
import Se2.MovieTicket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Category> categories = categoryService.getAllCategories();
        return categories.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return categoryService.getCategoryById(id)
                .map(category -> new ResponseEntity<>(category, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDTO categoryDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Category newCategory = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable("id") Long id, @RequestBody CategoryDTO categoryDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Category updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return updatedCategory != null ? new ResponseEntity<>(updatedCategory, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCategory(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Category>> searchCategories(@RequestParam(required = false) String name) {
        List<Category> categories = categoryService.searchCategories(name);
        return categories.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Category>> filterCategories(@RequestParam(required = false) String name) {
        List<Category> categories = categoryService.filterCategories(name);
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }
}