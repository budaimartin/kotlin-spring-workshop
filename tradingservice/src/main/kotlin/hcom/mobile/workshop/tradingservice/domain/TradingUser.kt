package hcom.mobile.workshop.tradingservice.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class TradingUser(@Id var id: String?, var userName: String, var fullName: String) {
    constructor(userName: String, fullName: String) : this(null, userName, fullName)
}
