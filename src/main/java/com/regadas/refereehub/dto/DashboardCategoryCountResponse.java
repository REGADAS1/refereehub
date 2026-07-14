package com.regadas.refereehub.dto;


//DTO para respostas agrupadas por categoria.
public record DashboardCategoryCountResponse(
        String label,
        long count
) {
}