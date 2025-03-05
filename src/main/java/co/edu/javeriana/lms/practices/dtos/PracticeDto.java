package co.edu.javeriana.lms.practices.dtos;

import co.edu.javeriana.lms.practices.models.Practice;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class PracticeDto {

    @NotBlank(message = "Practice name is mandatory")
    private String name;

    @NotBlank(message = "Practice description is mandatory")
    private String description;

    @NonNull
    private Boolean gradeable;

    @NonNull
    @Min(value = 1, message = "Number of groups must be greater than 0")
    private Integer numberOfGroups;

    @NonNull
    @Min(value = 1, message = "Max students per group must be greater than 0")
    private Integer maxStudentsGroup;

    public Practice toEntity(){
        return Practice.builder()
                .id(null)
                .name(this.name)
                .description(this.description)
                .gradeable(this.gradeable)
                .numberOfGroups(this.numberOfGroups)
                .maxStudentsGroup(this.maxStudentsGroup)
                .build();
    }
}
