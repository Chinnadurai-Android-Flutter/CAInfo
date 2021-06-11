package com.example.profile.datamodel

import java.io.Serializable

object Model {

    open class BaseModel : Serializable

    data class User(
        var name: String? = null,
        var id: Int? = null,
        var mobile: String? = null,
        var email: String? = null,
        var password: String? = null,
        var address: String? = null,
        var imagePath: String? = null,
        var latlng: String? = null,
    ) : BaseModel()
}