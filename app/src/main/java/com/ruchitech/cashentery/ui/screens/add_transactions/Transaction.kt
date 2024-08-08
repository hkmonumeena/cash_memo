package com.ruchitech.cashentery.ui.screens.add_transactions

import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction.Account
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction.Status
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction.Type
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class representing a financial transaction.
 *
 * @property id A unique identifier for the transaction.
 * @property date The date of the transaction in a string format (e.g., "2024-07-23").
 * @property timeInMiles The timestamp of the transaction in milliseconds since epoch.
 * @property type The type of transaction, which can be CREDIT or DEBIT.
 * @property account The account type involved in the transaction, which can be ONLINE or CASH.
 * @property transactionNumber A unique number assigned to the transaction.
 * @property amount The amount of money involved in the transaction.
 * @property remarks Additional notes or comments regarding the transaction.
 * @property tag A tag associated with the transaction for categorization.
 * @property status The current status of the transaction, which can be PENDING, CLEARED, OVERDUE, or VOID.
 */
@Serializable
data class Transaction(
    /**
     * A unique identifier for the transaction.
     */
    val id: String? = null,
    val authId: String? = null,

    /**
     * The date of the transaction in a string format (e.g., "2024-07-23").
     */
    val date: String? = null,

    /**
     * The timestamp of the transaction in milliseconds since epoch.
     */
    @SerialName("timeInMiles")
    val timeInMiles: Long? = null,

    /**
     * The type of transaction.
     * Can be either [Type.CREDIT] or [Type.DEBIT].
     */
    val type: Type? = null,

    /**
     * The account type involved in the transaction.
     * Can be either [Account.ONLINE] or [Account.CASH].
     */
    val account: Account? = null,

    /**
     * A unique number assigned to the transaction.
     */
    val transactionNumber: String? = null,

    /**
     * The amount of money involved in the transaction.
     */
    val amount: Double? = null,

    /**
     * Additional notes or comments regarding the transaction.
     */
    val remarks: String? = null,

    /**
     * A tag associated with the transaction for categorization.
     */
    val tag: String? = null,

    /**
     * The current status of the transaction.
     * Can be [Status.PENDING], [Status.CLEARED], [Status.OVERDUE], or [Status.VOID].
     */
    val status: Status = Status.CLEARED
) {
    /**
     * Enum representing the type of transaction.
     */
    @Serializable
    enum class Type {
        CREDIT,
        DEBIT
    }

    /**
     * Enum representing the account type involved in the transaction.
     */
    @Serializable
    enum class Account {
        ONLINE,
        CASH
    }

    /**
     * Enum representing the status of the transaction.
     */
    @Serializable
    enum class Status {
        /**
         * The transaction is pending and has not been completed yet.
         */
        PENDING,

        /**
         * The transaction has been completed successfully.
         */
        CLEARED,

        /**
         * The transaction is overdue and requires attention.
         */
        OVERDUE,

        /**
         * The transaction has been voided and is no longer valid.
         */
        VOID
    }
}
