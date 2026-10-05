package edu.hcmute.webpr.model;

import java.io.Serializable;
import java.util.List;

/**
 * Gói kết quả của một truy vấn CÓ PHÂN TRANG: danh sách phần tử của trang hiện
 * tại + các con số cần để vẽ thanh "Trang trước – 1 2 3 4 – Trang sau".
 *
 * Dùng chung cho cả Câu 3 (3 sách/trang theo tác giả), trang Sản phẩm và Câu 6
 * (CRUD Books có phân trang).
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 *
 * @param <T> kiểu phần tử của trang
 */
public class PageResult_24133059<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<T> items;
    private final int currentPage;
    private final int pageSize;
    private final int totalItems;

    public PageResult_24133059(List<T> items, int currentPage, int pageSize, int totalItems) {
        this.items = items;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
    }

    public List<T> getItems() {
        return items;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalItems() {
        return totalItems;
    }

    /** Tổng số trang, tối thiểu là 1 để view luôn vẽ được thanh phân trang. */
    public int getTotalPages() {
        if (pageSize <= 0) {
            return 1;
        }
        int pages = (totalItems + pageSize - 1) / pageSize;
        return Math.max(pages, 1);
    }

    public boolean isHasPrevious() {
        return currentPage > 1;
    }

    public boolean isHasNext() {
        return currentPage < getTotalPages();
    }

    public int getPreviousPage() {
        return isHasPrevious() ? currentPage - 1 : 1;
    }

    public int getNextPage() {
        return isHasNext() ? currentPage + 1 : getTotalPages();
    }
}
