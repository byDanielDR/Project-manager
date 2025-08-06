// Task.kt
data class Task(
        var name: String,
        var duration: Int,
        var successor: MutableList<Task> = mutableListOf()
)
