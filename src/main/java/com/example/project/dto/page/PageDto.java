package com.example.project.dto.page;

import java.util.List;

public class PageDto<T> {
    List<T> elements;
    int currentPage;
    int totalPages;

    public PageDto(List<T> elements, int currentPage, int totalPages) {
        this.elements = elements;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    public List<T> getElements() {
        return elements;
    }

    public void setElements(List<T> elements) {
        this.elements = elements;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
