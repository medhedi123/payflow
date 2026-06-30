package com.hedi.payflow.transaction.dto;

import jakarta.validation.constraints.NotBlank;

public record ReversalRequest(
        @NotBlank(message = "Reversal reason is required")
        String reason
) {
}