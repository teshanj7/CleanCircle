package com.example.example.models

data class User(var fullName: String? = null, var email: String? = null, val nic: String? = null,
                val password: String? = null, var address: String? = null, var accountType: String? = null,) {

}
//var phone: String? = null,