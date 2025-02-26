package co.edu.javeriana.lms.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaginationMetadataDto {
    private Integer page;
    private Integer size;
    private Long total;
    private Integer totalPages;
    private String next;
    private String previous;
}
