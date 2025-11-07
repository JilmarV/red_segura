import com.example.taller1.data.User

object CurrentUser {
    var id: String? = null
    var name: String? = null
    var city: String? = null
    var address: String? = null
    var email: String? = null
    var password: String? = null
    var Role: String? = null

    fun setFromUser(user: User) {
        id = user.id
        name = user.name
        address = user.address
        email = user.email
        password = user.password
        Role = user.role.toString()
    }

    fun clear() {
        id = null
        name = null
        city = null
        address = null
        email = null
        password = null
        Role = null
    }
}
