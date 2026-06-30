package com.hedi.payflow.transaction.dto;

public record ReversalResponse(
        Long originalTransactionId,
        String originalReference,
        String reversalReference,
        String status,
        String reason
) {
}