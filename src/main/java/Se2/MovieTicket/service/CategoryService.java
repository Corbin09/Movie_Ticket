package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.CategoryDTO;
import Se2.MovieTicket.model.Category;
import Se2.MovieTicket.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager em;

    public List<Category> filterCategories(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Category> cq = cb.createQuery(Category.class);
        Root<Category> category = cq.from(Category.class);

        List<Predicate> predicates = new ArrayList<>();
        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(category.get("categoryName"), "%" + name + "%"));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    public List<Category> searchCategories(String name) {
        return categoryRepository.findByCategoryNameContaining(name);
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category createCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setCategoryName(categoryDTO.getCategoryName());
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, CategoryDTO categoryDTO) {
        Optional<Category> categoryData = categoryRepository.findById(id);
        if (categoryData.isPresent()) {
            Category category = categoryData.get();
            category.setCategoryName(categoryDTO.getCategoryName());
            return categoryRepository.save(category);
        }
        return null;
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}