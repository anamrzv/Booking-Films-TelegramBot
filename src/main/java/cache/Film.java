package cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Film {
    private String title;
    private String description;
    private String posterName;
    private String trailer;
    private boolean isInRent;
}
