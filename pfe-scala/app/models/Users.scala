package models

object Users {

  val knownUsers = Map("Alice" -> "aaaa", "Bob" -> "aaaa", "Carol" -> "aaaa")

  def authenticate(username: String, password: String): Boolean =
    knownUsers.exists { case (name, pwd) => name == username && pwd == password }

}
