package co.com.bancolombia.usecase.response;

public class FilterResponse {
  private final int page;
  private final int size;
  private final String sortBy;
  private final String order;

  public FilterResponse(int page, int size, String sortBy, String order) {
    this.page = page;
    this.size = size;
    this.sortBy = sortBy;
    this.order = order;
  }

  public int getPage() { return page; }
  public int getSize() { return size; }
  public String getSortBy() { return sortBy; }
  public String getOrder() { return order; }
}


