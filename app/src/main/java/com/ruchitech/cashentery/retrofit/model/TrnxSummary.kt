package com.ruchitech.cashentery.retrofit.model

data class TrnxSummary(
    val averageCreditAmount: Double, // 782.5
    val averageDebitAmount: Double, // 116.66666666666667
    val creditsByStatus: CreditsByStatus,
    val dateRange: DateRange,
    val debitsByStatus: DebitsByStatus,
    val maxCreditAmount: Int?, // 1500
    val maxDebitAmount: Int?, // 150
    val minCreditAmount: Int?, // 10
    val minDebitAmount: Int?, // 100
    val totalCashCount: Int, // 7
    val totalCreditCount: Int, // 8
val totalCredits: Double, // 6260
    val totalDebitCount: Int, // 3
    val totalDebits: Double/**/, // 350
    val totalOnlineCount: Int, // 4
    val totalTransactionCount: Int, // 11
    val uniqueTagCount: Int, // 4
) {
    data class CreditsByStatus(
        val CLEARED: Double?, // 4660
        val OVERDUE: Double?, // 0
        val PENDING: Double?, // 100
        val VOID: Double?, // 1500
    )

    data class DateRange(
        val firstDate: String, // 2024-07-23
        val lastDate: String, // 2024-07-30
    )

    data class DebitsByStatus(
        val CLEARED: Double?, // 0
        val OVERDUE: Double?, // 250
        val PENDING: Double?, // 100
        val VOID: Double?, // 0
    )
}