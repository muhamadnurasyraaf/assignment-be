package com.asyraaf.assignment.Company.dto;

import java.util.List;

public record DashboardResponse(
        String companyName,
        List<RecentTransactionsDto> recentTransactions,
        List<LatestProductsDto> latestProducts,
        List<ActiveUsersDto> activeUsers) {

}
