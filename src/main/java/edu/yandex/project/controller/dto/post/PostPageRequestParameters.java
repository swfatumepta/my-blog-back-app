package edu.yandex.project.controller.dto.post;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PostPageRequestParameters(@NotNull String search,

                                        @NotNull(message = "pageNumber is required")
                                        @Min(value = 0, message = "pageNumber must be >= 0")
                                        Integer pageNumber,

                                        @NotNull(message = "pageSize is required")
                                        @Min(value = 1, message = "pageSize must be >= 0")
                                        @Max(value = 100, message = "pageSize must be <= 100")
                                        Integer pageSize) {
}
