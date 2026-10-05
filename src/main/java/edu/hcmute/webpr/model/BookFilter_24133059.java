package edu.hcmute.webpr.model;

/** Điều kiện tìm sách dùng chung giữa Controller, Service và DAO. */
public final class BookFilter_24133059 {
    private final String query;
    private final Integer authorId;
    private final String sort;
    private final boolean inStock;

    public BookFilter_24133059(String query, Integer authorId, String sort, boolean inStock) {
        String text = query == null ? "" : query.trim();
        this.query = text.length() > 100 ? text.substring(0, 100) : text;
        this.authorId = authorId != null && authorId > 0 ? authorId : null;
        this.sort = switch (sort == null ? "" : sort) {
            case "price_asc", "price_desc", "title" -> sort;
            default -> "latest";
        };
        this.inStock = inStock;
    }

    public String getQuery() { return query; }
    public Integer getAuthorId() { return authorId; }
    public String getSort() { return sort; }
    public boolean isInStock() { return inStock; }
    public boolean isFiltered() { return !query.isEmpty() || authorId != null || inStock; }
}
