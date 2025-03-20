package co.edu.javeriana.lms.videos.dtos;

import co.edu.javeriana.lms.videos.models.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    @NotBlank(message = "The comment is required")
    private String message;

    @NotNull(message = "The timestamp is required")
    private Long timestamp;

    public Comment toEntity() {
        return Comment.builder()
                .message(this.message)
                .timestamp(this.timestamp)
                .build();
    }
}
