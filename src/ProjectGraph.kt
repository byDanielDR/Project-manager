import javax.swing.DefaultListModel

//function to change sequence of tasks
fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val tmp = this[index1] // 'this' corresponds to the list
    this[index1] = this[index2]
    this[index2] = tmp
}

//main project Graph classes where we have methods and data objects to store and manipulate the app data
class ProjectGraph {
    public val projects = mutableListOf<Project>()
    public val projectsList = DefaultListModel<String>()

    //add project function
    fun addProject(projectName: String) {
        println("Hello, World!")

        val newProject = Project(projectName)
        projects.add(newProject)
        projectsList.addElement(projectName);

        println(this.projects);
    }
    //edit project name function
    fun editProject(index: Int, oldName: String, newName: String) {
        val project = projects.find { it.name == oldName }
        if (project != null) {
            project.name = newName
        }
        projectsList.set(index, newName);
    }
    //delete project function -> tasks are deleted with it
    fun deleteProject(index: Int, projectName: String) {
        val project = projects.find { it.name == projectName }
        if (project != null) {
            projects.remove(project)
        }
        projectsList.remove(index)
    }

    //add tasks in project, also the parent tasks edited to include this task as successor task  function
    fun addTask(projectName: String, taskName: String, duration: Int, selectedParent: String) {
        println("Hello, World!22 $selectedParent")
        val project = projects.find { it.name == projectName }
        println( project )
        val task = Task(taskName, duration)
        project?.tasks?.add(task)
        val index = project?.tasks?.indexOfFirst { it.name == selectedParent }

        if (index != -1 && index != null) {
            project.tasks[index].successor.add(task);
        }

        println(project)
    }

    //single function to move task up and down ie. to change its sequence
    fun moveTaskSequence(projectName: String, taskName: String, direction: Int) {
        val project = projects.find { it.name == projectName }
        if (project != null) {
            val index = project.tasks.indexOfFirst { it.name == taskName }
            if (index != -1) {
                val newIndex = index + direction
                if (newIndex >= 0 && newIndex < project.tasks.size) {
                    project.tasks.swap(index, newIndex)
                }
            }
        }
    }

    //it doesnt make adjacencyMatrix but prints the relationship in human-readable form... check the console...
    fun makeAdjacencyMatrix(projectName: String) {
        val project = projects.find { it.name == projectName }
        if (project != null) {
            val tasks = project.tasks;
            for (task in tasks) {
                print(task.name+ " -> ");
                val subTasks = task.successor;
                for (subTask in subTasks) {
                    print(subTask.name + " - ");
                }
                println("!")
            }
        }
    }

    //AdjacencyMatrix is made here and returned to java swing to be printed. also logged in console
    fun getAdjacencyMatrix(selectedProject: String?): Array<IntArray> {
        val currProject = projects.find { it.name == selectedProject }
        if (currProject != null) {
            val tasks: List<Task> = currProject.tasks
            val adjacencyMatrix = Array(tasks.size) { IntArray(tasks.size) }
            for (i in tasks.indices) {
                val task = tasks[i]
                val dependencies: List<Task> = task.successor
                for (dependency in dependencies) {
                    val dependencyIndex = tasks.indexOfFirst { it.name == dependency.name }
                    adjacencyMatrix[i][dependencyIndex] = 1
                }
            }
            print("matrix");
            for (matrix in adjacencyMatrix) {
                for ( m in matrix ) {
                    print(m)
                    print("   ")
                }
                println(" ")
            }
            return adjacencyMatrix
        } else {
            return Array(0) { IntArray(0) }
        }
    }
}
