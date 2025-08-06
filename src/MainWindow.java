import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class MainWindow {
    private ProjectGraph projectGraph = new ProjectGraph();

    public MainWindow() {
        JFrame frame = new JFrame("Project Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel projectButtonPanel = new JPanel(new GridLayout(1, 3));
        JButton addProjectButton = new JButton("Add Project");
        JButton editProjectButton = new JButton("Edit Project");
        JButton deleteProjectButton = new JButton("Delete Project");
        projectButtonPanel.add(addProjectButton);
        projectButtonPanel.add(editProjectButton);
        projectButtonPanel.add(deleteProjectButton);

        JPanel projectListPanel = new JPanel(new BorderLayout());
        JList<String> projectList = new JList<>(projectGraph.getProjectsList());
        JScrollPane projectScrollPane = new JScrollPane(projectList);
        projectListPanel.add(projectScrollPane, BorderLayout.CENTER);

        JPanel taskAreaPanel = new JPanel(new BorderLayout());
        JList<String> taskList = new JList<>();
        JScrollPane taskScrollPane = new JScrollPane(taskList);
        taskAreaPanel.add(taskScrollPane, BorderLayout.CENTER);

        JButton addTaskButton = new JButton("Add Task");
        JButton upTaskButton = new JButton("⬆\uFE0F");
        JButton downTaskButton = new JButton("⬇\uFE0F");
        JPanel taskButtonsPanel = new JPanel();
        taskButtonsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        taskButtonsPanel.add(addTaskButton, gbc);


        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        taskButtonsPanel.add(upTaskButton, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        taskButtonsPanel.add(downTaskButton, gbc);

        taskAreaPanel.add(taskButtonsPanel, BorderLayout.SOUTH);


        //add projects using the addProject function in ProjectGraph class
        addProjectButton.addActionListener(e -> {
            String projectName = JOptionPane.showInputDialog(frame, "Enter project name:");
            if (projectName != null && !projectName.isEmpty()) {
                projectGraph.addProject(projectName);
                //add this for debugging reasons
                //projectGraph.addTask(projectName, "one", 11, "");
            }
        });

        // edit the project title... give last name and new name
        editProjectButton.addActionListener(e -> {
            int selectedIndex = projectList.getSelectedIndex();
            String selectedProject = projectList.getSelectedValue();
            if (selectedIndex != -1) {
                String editedProjectName = JOptionPane.showInputDialog(frame, "Enter edited project name:", selectedProject);
                if (editedProjectName != null && !editedProjectName.isEmpty()) {
                    projectGraph.editProject(selectedIndex, selectedProject, editedProjectName);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a project to edit.");
                }
            }
        });

        //delete project button click
        deleteProjectButton.addActionListener(e -> {
            int selectedIndex = projectList.getSelectedIndex();
            String selectedProject = projectList.getSelectedValue();
            if (selectedIndex != -1) {
                int confirmation = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this project?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    projectGraph.deleteProject(selectedIndex, selectedProject);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a project to delete.");
                }
            }
        });


        //action when user selects a project. plus this is must for adding tasks and printing adj matrix
        projectList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedProject = projectList.getSelectedValue();
                if (selectedProject != null) {
                    DefaultListModel<String> taskModel = AddTasks(selectedProject);
                    taskList.setModel(taskModel);
                }
            }
        });

        //button to add task
        addTaskButton.addActionListener(e -> {
            String selectedProject = projectList.getSelectedValue();

            if (selectedProject != null) {
                JPanel panel = new JPanel(new GridBagLayout());
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.insets = new Insets(5, 5, 5, 5); // Add some padding
                constraints.gridx = 0;
                constraints.gridy = 0;

                JTextField taskNameField = new JTextField();
                JTextField durationField = new JTextField();

                panel.add(new JLabel("Task for " + selectedProject + ":"), constraints);
                constraints.gridy++;
                panel.add(taskNameField, constraints);
                constraints.gridy++;
                panel.add(new JLabel("Task Duration (hours):"), constraints);
                constraints.gridy++;
                panel.add(durationField, constraints);

                // to give list of parent tasks to be selected. ie-> the task we have to
                constraints.gridx = 1;
                constraints.gridy = 0;
                constraints.gridwidth = 2;
                panel.add(new JLabel("Parent Task:"), constraints);
                constraints.gridy++;
                constraints.gridwidth = 1;
                constraints.gridheight = 2;
                JList<String> tasks = new JList<String>();
                tasks.setModel(AddTasks(selectedProject));
                JScrollPane taskScrollPane2 = new JScrollPane(tasks);
                panel.add(taskScrollPane2, constraints);

                int result = JOptionPane.showConfirmDialog(
                        frame, panel, "Add Task",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
                );

                if (result == JOptionPane.OK_OPTION) {
                    String taskName = taskNameField.getText();
                    String durationInput = durationField.getText();
                    String selectedParent = tasks.getSelectedValue();
                    if (selectedParent == null && selectedParent.isEmpty()) {
                        selectedParent = "";
                    }

                    if (!taskName.isEmpty() && !durationInput.isEmpty() ) {
                        try {
                            int duration = Integer.parseInt(durationInput);
                            projectGraph.addTask(selectedProject, taskName, duration, selectedParent);
                            taskList.setModel(AddTasks(selectedProject));
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(frame, "Invalid duration. Please enter a valid number.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Please enter both task name and duration.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a project to add a task.");
            }
        });

        //move task one up
        upTaskButton.addActionListener(e -> {
            String selectedProject = projectList.getSelectedValue();
            if (selectedProject != null) {
                int selectedTaskIndex = taskList.getSelectedIndex();
                if (selectedTaskIndex != -1) {
                    String selectedTask = taskList.getModel().getElementAt(selectedTaskIndex);
                    projectGraph.moveTaskSequence(selectedProject, selectedTask, -1);
                    DefaultListModel<String> taskModel = AddTasks(selectedProject);
                    taskList.setModel(taskModel);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a task to delete.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a project to delete a task.");
            }
        });

        //move task one up
        downTaskButton.addActionListener(e -> {
            String selectedProject = projectList.getSelectedValue();
            if (selectedProject != null) {
                int selectedTaskIndex = taskList.getSelectedIndex();
                if (selectedTaskIndex != -1) {
                    String selectedTask = taskList.getModel().getElementAt(selectedTaskIndex);
                    projectGraph.moveTaskSequence(selectedProject, selectedTask, 1);
                    DefaultListModel<String> taskModel = AddTasks(selectedProject);
                    taskList.setModel(taskModel);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a task to delete.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a project to delete a task.");
            }
        });




        //button to show AdjacencyMatrix
        JButton showAdjacencyMatrixButton = new JButton("Show Adjacency Matrix");
        showAdjacencyMatrixButton.addActionListener(e -> {
            String selectedProject = projectList.getSelectedValue();
            if (selectedProject != null) {
                projectGraph.makeAdjacencyMatrix(selectedProject);
                int[][] adjacencyMatrix = projectGraph.getAdjacencyMatrix(selectedProject);

                boolean isZeroMatrix = false;

                if (!isZeroMatrix) {
                    StringBuilder matrixString = new StringBuilder("Adjacency Matrix:\n");

                    matrixString.append("      ");
                    for (int i = 0; i < adjacencyMatrix.length; i++) {
                        matrixString.append(i).append(" ");
                    }
                    matrixString.append("\n");

                    for (int i = 0; i < adjacencyMatrix.length; i++) {
                        matrixString.append(i).append(" | ");
                        for (int value : adjacencyMatrix[i]) {
                            matrixString.append(value).append(" ");
                        }
                        matrixString.append("\n");
                    }

                    JOptionPane.showMessageDialog(null, matrixString.toString(), "Adjacency Matrix", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "The adjacency matrix is all zeros.", "No Data to Print", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        JPanel adjacencyButtonPanel = new JPanel();
        adjacencyButtonPanel.add(showAdjacencyMatrixButton);

        frame.add(projectButtonPanel, BorderLayout.NORTH);
        frame.add(projectListPanel, BorderLayout.CENTER);
        frame.add(taskAreaPanel, BorderLayout.EAST);
        frame.add(adjacencyButtonPanel, BorderLayout.SOUTH);

        // Displaying the frame
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }

    public DefaultListModel<String> AddTasks(String selectedProject) {
        DefaultListModel<String> taskListModel = new DefaultListModel<>();
        List<Project> projects = projectGraph.getProjects();

        // finding the project with selected name
        Optional<Project> selectedProjectObj = projects.stream()
                .filter(project -> project.getName().equals(selectedProject))
                .findFirst();

        // we add the tasks of selected project to the TaskList
        selectedProjectObj.ifPresent(project -> {
            for (Task task : project.getTasks()) {
                taskListModel.addElement(task.getName());
            }
        });
        return taskListModel;
    }
}