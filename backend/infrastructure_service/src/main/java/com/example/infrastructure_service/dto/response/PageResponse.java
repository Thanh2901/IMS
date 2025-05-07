package com.example.infrastructure_service.dto.response;

import java.util.List;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class PageResponse<T> {

  private List<T> pageData;
  private int pageNumber;
  private int pageSize;
  private long totalElements;
  private int totalPages;

  public PageResponse(Page<T> page) {
    this.pageData = page.getContent();
    this.pageNumber = page.getNumber();
    this.pageSize = page.getSize();
    this.totalElements = page.getTotalElements();
    this.totalPages = page.getTotalPages();
  }
}
