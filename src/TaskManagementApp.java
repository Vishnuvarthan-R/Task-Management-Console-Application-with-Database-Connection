import java.util.*;
import java.sql.*;
import java.util.Date;
import java.util.GregorianCalendar;

class Task implements Comparable<Task> {
    private int id;
    private String title;
    private String description;
    private int priority;
    private Date dueDate;

    public Task(int id, String title, String description, int priority, Date dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getPriority() {
        return priority;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public int compareTo(Task other) {
        if (this.priority != other.priority) {
            return Integer.compare(other.priority, this.priority); // Higher priority first
        }
        return this.dueDate.compareTo(other.dueDate); // Earlier due date first
    }

    @Override
    public String toString() {
        return "Task ID: " + id + ", Title: " + title + ", Priority: " + priority + ", Due: " + dueDate;
    }
}

class TaskManager {
    private Connection connection;

    // Constructor to establish a database connection
    public TaskManager() {
        try {
            // Connect to the database
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/TaskDB", "root", "Vishnu@3");
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add Task to Database
    public void addTask(String title, String description, int priority, Date dueDate) {
        String query = "INSERT INTO Tasks (title, description, priority, due_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, title);
            statement.setString(2, description);
            statement.setInt(3, priority);
            statement.setDate(4, new java.sql.Date(dueDate.getTime()));
            statement.executeUpdate();
            System.out.println("Task added successfully to the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete Task from Database
    public void deleteTask(int id) {
        String query = "DELETE FROM Tasks WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Task deleted successfully from the database.");
            } else {
                System.out.println("Task not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update Task in Database
    public void updateTask(int id, String title, String description, int priority, Date dueDate) {
        String query = "UPDATE Tasks SET title = ?, description = ?, priority = ?, due_date = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, title);
            statement.setString(2, description);
            statement.setInt(3, priority);
            statement.setDate(4, new java.sql.Date(dueDate.getTime()));
            statement.setInt(5, id);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Task updated successfully in the database.");
            } else {
                System.out.println("Task not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieve and Display Tasks from Database
    public void displayTasks() {
        String query = "SELECT * FROM Tasks ORDER BY priority DESC, due_date ASC";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                int priority = resultSet.getInt("priority");
                Date dueDate = resultSet.getDate("due_date");

                System.out.println("Task ID: " + id + ", Title: " + title + ", Priority: " + priority + ", Due Date: " + dueDate);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Close Database Connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


public class TaskManagementApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskManager taskManager = new TaskManager();

        while (true) {
            System.out.println("Task Management System:");
            System.out.println("1. Add Task");
            System.out.println("2. Delete Task");
            System.out.println("3. Update Task");
            System.out.println("4. View Tasks");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter description: ");
                    String description = scanner.nextLine();
                    System.out.print("Enter priority (1-5): ");
                    int priority = scanner.nextInt();
                    System.out.print("Enter due date (yyyy-mm-dd): ");
                    String dateStr = scanner.next();
                    Date dueDate = new GregorianCalendar(
                            Integer.parseInt(dateStr.substring(0, 4)),
                            Integer.parseInt(dateStr.substring(5, 7)) - 1,
                            Integer.parseInt(dateStr.substring(8))
                    ).getTime();

                    taskManager.addTask(title, description, priority, dueDate);
                    break;

                case 2:
                    System.out.print("Enter task ID to delete: ");
                    int deleteId = scanner.nextInt();
                    taskManager.deleteTask(deleteId);
                    break;

                case 3:
                    System.out.print("Enter task ID to update: ");
                    int updateId = scanner.nextInt();
                    scanner.nextLine();  // Consume newline
                    System.out.print("Enter new title: ");
                    String newTitle = scanner.nextLine();
                    System.out.print("Enter new description: ");
                    String newDescription = scanner.nextLine();
                    System.out.print("Enter new priority (1-5): ");
                    int newPriority = scanner.nextInt();
                    System.out.print("Enter new due date (yyyy-mm-dd): ");
                    String newDateStr = scanner.next();
                    Date newDueDate = new GregorianCalendar(
                            Integer.parseInt(newDateStr.substring(0, 4)),
                            Integer.parseInt(newDateStr.substring(5, 7)) - 1,
                            Integer.parseInt(newDateStr.substring(8))
                    ).getTime();

                    taskManager.updateTask(updateId, newTitle, newDescription, newPriority, newDueDate);
                    break;

                case 4:
                    taskManager.displayTasks();
                    break;

                case 5:
                    taskManager.closeConnection();
                    System.out.println("Exiting Task Management System.");
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
