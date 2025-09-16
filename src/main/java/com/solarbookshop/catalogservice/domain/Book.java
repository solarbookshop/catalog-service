package com.solarbookshop.catalogservice.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record Book(
        @NotBlank(message = "{isbn.notBlank}")
				@Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "{isbn.invalid}")
				String isbn,

				@NotBlank(message = "{title.notBlank}")
				String title,

				@NotBlank(message = "{author.notBlank}")
				String author,

				@NotNull(message = "{price.notNull}")
				@Positive(message = "{price.positive}")
				Double price
) {
}
