package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.models.Category;
import org.yearup.repository.CategoryRepository;

import java.util.List;

@Service
public class CategoryService
{
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository)
    {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories()
    {
        return categoryRepository.findAll();
    }

    public Category getById(int categoryId)
    {
        // findById gives back an Optional, just unwrapping it here, null if nothing's found
        return categoryRepository.findById(categoryId).orElse(null);
    }

    public Category create(Category category)
    {
        // forcing id to 0 so the db always generates a fresh one,
        // even if someone accidentally sends an id in the request body
        category.setCategoryId(0);
        return categoryRepository.save(category);
    }

    public Category update(int categoryId, Category category)
    {
        // grabbing the real entity first then copying fields onto it,
        // instead of just saving the incoming object directly
        // keeps us from overwriting stuff that wasn't even part of the request
        Category existing = categoryRepository.findById(categoryId).orElseThrow();
        existing.setName(category.getName());
        existing.setDescription(category.getDescription());
        return categoryRepository.save(existing);
    }

    public void delete(int categoryId)
    {
        categoryRepository.deleteById(categoryId);
    }
}