package edu.yandex.project.controller.dto.post;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants
public record PostPageRequestParameters(@NotNull(message = "request parameter 'search' is required")
                                        String search,

                                        @NotNull(message = "request parameter 'pageNumber' is required")
                                        @Min(value = 1, message = "pageNumber must be > 0")
                                        Integer pageNumber,

                                        @NotNull(message = "request parameter 'pageSize' is required")
                                        @Min(value = 1, message = "pageSize must be >= 0")
                                        @Max(value = 100, message = "pageSize must be <= 100")
                                        Integer pageSize) {
}
