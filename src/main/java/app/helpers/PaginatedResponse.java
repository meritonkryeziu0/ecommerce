package app.helpers;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginatedResponse<T> {
  private int totalEntities;
  private int returnedEntities;
  private int totalPages;
  private int currentPage;
  private List<T> data;

}