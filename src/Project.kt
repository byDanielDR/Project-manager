// Project.kt
data class Project(
        var name: String,
        val tasks: MutableList<Task> = mutableListOf()
)