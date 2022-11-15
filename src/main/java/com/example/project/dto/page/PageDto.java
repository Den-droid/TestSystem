package com.example.project.dto.page;

public abstract class PageDto {
    private int currentPage;
    private int totalPages;

    public int getCurrentPage() {
        return currentPage;
    }

    public PageDto(int currentPage, int totalPages) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
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
