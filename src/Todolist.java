import java.sql.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.AWTException;
import java.io.*;
import javax.imageio.*;
import java.util.concurrent.*;

public class Todolist {

  public static String currentTaskId;
  public static List<String> tasksId;
  public static List<String> tasks;
  public static List<String> tasksLabel;
  public static List<String> tasksDesc;
  public static List<String> tasksDate;
  public static List<String> tasksClock;
  public static List<String> tasksCategory;
  public static List<String> tasksNewCategory;
  private static int i = 0;

  // indicator for looping screenCapture
  public static boolean running = true;

  public static String getUsername() throws SQLException {
    Connection connection = Dbconnect.getConnect();
    String sql = "SELECT username FROM users WHERE id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, App.currentUserId);
    ResultSet resultSet = statement.executeQuery();

    String username = "";
    if (resultSet.next()) {
      username = resultSet.getString("username");
    }
    return username;
  }

  public static int getRandomInterval() {
    int min = 10;
    int max = 30;
    int duration = (int) (Math.random() * (max - min + 1)) + min;
    System.out.println("Interval: " + duration);
    return duration;
  }

  public static void screenCapture() throws AWTException, IOException, SQLException {
    Robot robot = new Robot();
    Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    BufferedImage image = robot.createScreenCapture(rectangle);

    String directoryPath = System.getProperty("user.dir") + "/src/screenshots";
    File directory = new File(directoryPath);

    if (!directory.exists()) {
      if (directory.mkdirs()) {
        System.out.println("Directory created successfully!");
      } else {
        System.out.println("Failed to create directory!");
        return;
      }
    }

    // File Name
    String username = getUsername();
    LocalDate currentDate = LocalDate.now();
    LocalTime currentTime = LocalTime.now();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH-mm-ss");

    String formattedDate = currentDate.format(dateFormatter);
    String formattedTime = currentTime.format(timeFormatter);

    String fileName = formattedDate + "_" + formattedTime + "_" + username + ".png";

    File file = new File(directory, fileName);
    ImageIO.write(image, "png", file);

    System.out.println("file berhasil disave di " + file.getAbsolutePath());
  }

  private static void startScreenCaptureLoop() {
    new Thread(() -> {
      while (running) {
        try {
          screenCapture();
          // Delay between captures
          TimeUnit.SECONDS.sleep(getRandomInterval());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      System.out.println("Loop stopped.");
    }).start();
  }

  public static String[] getTaskId() throws SQLException {

    Connection connection = Dbconnect.getConnect();
    String sql = "SELECT task_id FROM collaborators WHERE user_id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, App.currentUserId);

    ResultSet resultSet = statement.executeQuery();

    List<String> tasksIdList = new ArrayList<>();
    Todolist.tasksId = tasksIdList;

    while (resultSet.next()) {
      tasksIdList.add(resultSet.getString(1));
    }

    // Convert the list to a String array (if needed)
    String[] tasksIdArray = tasksIdList.toArray(new String[tasksIdList.size()]);

    resultSet.close();
    statement.close();
    connection.close();

    return tasksIdArray;
  }

  public static String[] getTask() throws SQLException {
    Connection connection = Dbconnect.getConnect();
    String sql = "SELECT title FROM tasks RIGHT JOIN collaborators ON tasks.id = collaborators.task_id WHERE collaborators.user_id = ?";
    // String sql = "SELECT title FROM tasks WHERE created_by = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, App.currentUserId);

    ResultSet resultSet = statement.executeQuery();

    List<String> tasksList = new ArrayList<>();
    Todolist.tasks = tasksList;

    while (resultSet.next()) {
      tasksList.add(resultSet.getString(1));
    }

    // Convert the list to a String array (if needed)
    String[] tasksArray = tasksList.toArray(new String[tasksList.size()]);
    System.out.println("tasksArray: " + tasksArray);

    resultSet.close();
    statement.close();
    connection.close();

    return tasksArray;
  }

  public static String[] getTasksLabel() throws SQLException {
    Connection connection = Dbconnect.getConnect();
    String sql = "SELECT label FROM tasks RIGHT JOIN collaborators ON tasks.id = collaborators.task_id WHERE collaborators.user_id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, App.currentUserId);

    ResultSet resultSet = statement.executeQuery();

    List<String> tasksLabelList = new ArrayList<>();
    Todolist.tasksLabel = tasksLabelList;

    while (resultSet.next()) {
      tasksLabelList.add(resultSet.getString(1));
    }

    // Convert the list to a String array (if needed)
    String[] tasksLabelArray = tasksLabelList.toArray(new String[tasksLabelList.size()]);

    resultSet.close();
    statement.close();
    connection.close();

    return tasksLabelArray;
  }

  public static String[] getTasksDesc() throws SQLException {
    Connection connection = Dbconnect.getConnect();
    String sql = "SELECT description FROM tasks RIGHT JOIN collaborators ON tasks.id = collaborators.task_id WHERE collaborators.user_id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, App.currentUserId);

    ResultSet resultSet = statement.executeQuery();

    List<String> tasksDescList = new ArrayList<>();
    Todolist.tasksDesc = tasksDescList;

    while (resultSet.next()) {
      tasksDescList.add(resultSet.getString(1));
    }

    // Convert the list to a String array (if needed)
    String[] tasksDescArray = tasksDescList.toArray(new String[tasksDescList.size()]);

    resultSet.close();
    statement.close();
    connection.close();

    return tasksDescArray;
  }

  public static String[] getTasksCategory() throws SQLException {
    Connection connection = Dbconnect.getConnect();
    String sql = "SELECT priority_id FROM tasks RIGHT JOIN collaborators ON tasks.id = collaborators.task_id WHERE collaborators.user_id = ?";
    // String sql = "SELECT priority.name FROM task INNER JOIN priority ON
    // task.priority_id = priority.id WHERE task.created_by = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, App.currentUserId);

    ResultSet resultSet = statement.executeQuery();

    List<String> tasksCategoryList = new ArrayList<>();
    Todolist.tasksCategory = tasksCategoryList;

    while (resultSet.next()) {
      tasksCategoryList.add(resultSet.getString(1));
    }

    // Convert the list to a String array (if needed)
    String[] tasksCategoryArray = tasksCategoryList.toArray(new String[tasksCategoryList.size()]);

    resultSet.close();
    statement.close();
    connection.close();

    return tasksCategoryArray;
  }

  public static String[] getNewTasksCategory() throws SQLException {
    Connection connection = Dbconnect.getConnect();
    String sql = "SELECT new_priority_id FROM tasks RIGHT JOIN collaborators ON tasks.id = collaborators.task_id WHERE collaborators.user_id = ?";
    // String sql = "SELECT priority.name FROM task INNER JOIN priority ON
    // task.priority_id = priority.id WHERE task.created_by = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, App.currentUserId);

    ResultSet resultSet = statement.executeQuery();

    List<String> tasksNewCategoryList = new ArrayList<>();
    Todolist.tasksNewCategory = tasksNewCategoryList;

    while (resultSet.next()) {
      tasksNewCategoryList.add(resultSet.getString(1));
    }

    // Convert the list to a String array (if needed)
    String[] tasksNewCategoryArray = tasksNewCategoryList.toArray(new String[tasksNewCategoryList.size()]);

    resultSet.close();
    statement.close();
    connection.close();

    return tasksNewCategoryArray;
  }

  public static String[] getTasksDate() throws SQLException {
    Connection connection = Dbconnect.getConnect();
    String sql = "SELECT deadline FROM tasks RIGHT JOIN collaborators ON tasks.id = collaborators.task_id WHERE collaborators.user_id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, App.currentUserId);

    ResultSet resultSet = statement.executeQuery();

    List<String> tasksDateList = new ArrayList<>();
    Todolist.tasksDate = tasksDateList;

    while (resultSet.next()) {
      tasksDateList.add(resultSet.getString(1));
    }

    // Convert the list to a String array (if needed)
    String[] tasksDateArray = tasksDateList.toArray(new String[tasksDateList.size()]);

    resultSet.close();
    statement.close();
    connection.close();

    return tasksDateArray;
  }

  public static void add(Stage addStage) throws Exception {
    BorderPane borderPane = new BorderPane();
    borderPane.getStylesheets().add("/assets/addTaskStyle.css");
    borderPane.getStyleClass().add("bgColor");

    VBox box = new VBox();
    box.setAlignment(Pos.CENTER);

    GridPane addPane = new GridPane();
    addPane.setAlignment(Pos.CENTER);
    addPane.setVgap(5);

    Text newText = new Text("Add New Task");
    addPane.add(newText, 0, 1);
    newText.getStyleClass().add("addText");

    Label taskLabel = new Label("Task Name");
    addPane.add(taskLabel, 0, 2);
    taskLabel.getStyleClass().add("labelColor");

    TextField taskInput = new TextField();
    addPane.add(taskInput, 0, 3);
    taskInput.setPrefWidth(250);
    taskInput.setPrefHeight(35);

    Label descLabel = new Label("Description");
    addPane.add(descLabel, 0, 4);
    descLabel.getStyleClass().add("labelColor");

    TextArea descInput = new TextArea();
    addPane.add(descInput, 0, 5);
    descInput.setPrefWidth(250);
    descInput.setPrefHeight(50);

    Label dateLabel = new Label("Date to end the task");
    addPane.add(dateLabel, 0, 6);
    dateLabel.getStyleClass().add("labelColor");

    DatePicker dateInput = new DatePicker();
    addPane.add(dateInput, 0, 7);
    dateInput.setPrefWidth(120);
    dateInput.setPrefHeight(35);

    Label timeLabel = new Label("Time");
    addPane.add(timeLabel, 0, 8);
    timeLabel.getStyleClass().add("labelColor");

    ComboBox<String> hourInput = new ComboBox<>();
    for (int hour = 0; hour < 24; hour++) {
      hourInput.getItems().add(String.format("%02d", hour));
    }
    hourInput.setValue("00");

    ComboBox<String> minuteInput = new ComboBox<>();
    for (int minute = 0; minute < 60; minute++) {
      minuteInput.getItems().add(String.format("%02d", minute));
    }
    minuteInput.setValue("00");

    HBox timePicker = new HBox(hourInput, minuteInput);
    addPane.add(timePicker, 0, 9);

    Label priorityLabel = new Label("Priority");
    addPane.add(priorityLabel, 0, 10);
    priorityLabel.getStyleClass().add("labelColor");

    ChoiceBox<String> priorityInput = new ChoiceBox<>();
    priorityInput.getItems().addAll("Low", "Medium", "High");
    priorityInput.setValue("Low");
    addPane.add(priorityInput, 0, 11);
    priorityInput.setPrefWidth(120);
    priorityInput.setPrefHeight(35);

    Label tagLabel = new Label("Tag");
    addPane.add(tagLabel, 0, 12);
    tagLabel.getStyleClass().add("labelColor");

    TextField tagInput = new TextField();
    addPane.add(tagInput, 0, 13);
    tagInput.setPrefWidth(120);
    tagInput.setPrefHeight(35);

    Label colabLabel = new Label("Collaborators (Emails)");
    addPane.add(colabLabel, 0, 14);
    colabLabel.getStyleClass().add("labelColor");

    TextField colabInput = new TextField();
    addPane.add(colabInput, 0, 15);
    colabInput.setPrefWidth(250);
    colabInput.setPrefHeight(35);

    Button plusBtn = new Button("plus");
    addPane.add(plusBtn, 1, 15);
    plusBtn.setPrefWidth(50);
    plusBtn.setPrefHeight(35);

    List<TextField> additionalColabFields = new ArrayList<>();

    plusBtn.setOnAction(event -> {
      i++;
      TextField plusText = new TextField();
      Button minusBtn = new Button("minus");
      addPane.add(plusText, 0, 15 + i);
      addPane.add(minusBtn, 1, 15 + i);
      additionalColabFields.add(plusText);

      minusBtn.setOnAction(minusEvent -> {
        i--;
        addPane.getChildren().remove(plusText);
        addPane.getChildren().remove(minusBtn);
        additionalColabFields.remove(plusText);
      });
    });

    Button addBtn = new Button("Add Task");
    addPane.add(addBtn, 0, 20);
    addBtn.setPrefWidth(80);
    addBtn.setPrefHeight(35);
    addBtn.getStyleClass().add("btn");

    HBox btnBox = new HBox(addBtn);
    btnBox.setSpacing(5);
    addPane.add(btnBox, 0, 17);

    addBtn.setOnAction(event -> {
      try {
        String taskName = taskInput.getText();
        String description = descInput.getText();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy/MM/dd");
        LocalDate taskDate = dateInput.getValue();
        String formatDate = taskDate.format(formatter);
        String priority = priorityInput.getValue();
        String hour = hourInput.getValue();
        String minute = minuteInput.getValue();
        int hourInt = Integer.parseInt(hour);
        int minuteInt = Integer.parseInt(minute);
        LocalTime time = LocalTime.of(hourInt, minuteInt);
        String tag = tagInput.getText();
        List<String> collaboratorEmails = new ArrayList<>();
        collaboratorEmails.add(colabInput.getText());
        additionalColabFields.forEach(field -> collaboratorEmails.add(field.getText()));

        int priorityId;
        switch (priority) {
          case "Low":
            priorityId = 3;
            break;
          case "Medium":
            priorityId = 2;
            break;
          case "High":
            priorityId = 1;
            break;
          default:
            System.err.println("Invalid priority: " + priorityInput);
            priorityId = 3;
        }

        Connection connection = Dbconnect.getConnect();
        String getUserIdQuery = "SELECT id FROM users WHERE email = ?";
        PreparedStatement getUserIdStatement = connection.prepareStatement(getUserIdQuery);
        List<Integer> collaboratorIds = new ArrayList<>();

        for (String email : collaboratorEmails) {
          getUserIdStatement.setString(1, email);
          try (ResultSet rs = getUserIdStatement.executeQuery()) {
            if (rs.next()) {
              int userId = rs.getInt("id");
              collaboratorIds.add(userId);
            } else {
              Alert errorAlert = new Alert(Alert.AlertType.ERROR);
              errorAlert.setTitle("Failed to add collaborators");
              errorAlert.setHeaderText(null);
              errorAlert.setContentText("User with email " + email + " not found.");
              errorAlert.showAndWait();
              return; // Exit the method, do not add the task
            }
          }
        }

        String insertTaskQuery = "INSERT INTO tasks (created_by, priority_id, title, description, deadline, label, new_priority_id) VALUES (?,?,?,?,?,?,?)";
        PreparedStatement statement = connection.prepareStatement(insertTaskQuery, new String[] { "task_id" });

        statement.setInt(1, App.currentUserId);
        statement.setInt(2, priorityId);
        statement.setString(3, taskName);
        statement.setString(4, description);
        statement.setString(5, formatDate);
        statement.setString(6, tag);
        statement.setInt(7, priorityId);

        statement.executeUpdate();

        // Retrieve the generated task_id
        int taskId;
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            taskId = generatedKeys.getInt(1);
          } else {
            throw new SQLException("Creating task failed, no ID obtained.");
          }
        }

        String insertUserCobalQuery = "INSERT INTO collaborators (user_id, task_id) VALUES (?, ?)";
        PreparedStatement userColabStatement = connection.prepareStatement(insertUserCobalQuery);
        userColabStatement.setInt(1, App.currentUserId);
        userColabStatement.setInt(2, taskId);
        userColabStatement.executeUpdate();

        // Insert collaborators
        String insertColabQuery = "INSERT INTO collaborators (task_id, user_id) VALUES (?, ?)";
        PreparedStatement colabStatement = connection.prepareStatement(insertColabQuery);

        for (int userId : collaboratorIds) {
          colabStatement.setInt(1, taskId);
          colabStatement.setInt(2, userId);
          colabStatement.addBatch();
        }
        colabStatement.executeBatch();

        Platform.runLater(() -> {
          try {
            Todolist.show(new Stage());
            addStage.close();
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
      } catch (SQLException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    box.getChildren().addAll(addPane);
    BorderPane.setAlignment(box, Pos.CENTER);
    borderPane.setCenter(box);

    Scene scene = new Scene(borderPane, 500, 768);
    addStage.setTitle("Add Task");
    addStage.setScene(scene);
    addStage.show();
  }

  // untuk menampilkan tugas
  public static void show(Stage showStage) throws Exception {
    BorderPane borderPane = new BorderPane();
    borderPane.setPadding(new Insets(20, 20, 20, 20));
    borderPane.getStylesheets().add("/assets/showTaskStyle.css");

    borderPane.getStyleClass().add("bgColor");

    VBox box = new VBox();
    box.setAlignment(Pos.CENTER);

    // Header
    Text holaText = new Text("Hola, ");
    holaText.getStyleClass().add("nameText");
    Text nameText = new Text();
    // nameText.setText("Hola,Josh");
    nameText.getStyleClass().add("nameText");
    try {

      Connection connection = Dbconnect.getConnect();
      String sql = "SELECT username FROM users WHERE id =?";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setInt(1, App.currentUserId);
      ResultSet rs = statement.executeQuery();
      rs.next();
      nameText.setText(rs.getString("username"));
    } catch (Exception e) {
      e.printStackTrace();
    }

    HBox nameBox = new HBox(holaText, nameText);
    Text dateText = new Text();
    dateText.setText("15 Mar 2024");
    dateText.getStyleClass().add("dateText");
    Text timeText = new Text();
    timeText.setText("08:00");
    timeText.getStyleClass().add("dateText");

    HBox dateBox = new HBox(dateText, timeText);
    dateBox.setSpacing(3);
    VBox profileBox = new VBox(nameBox, dateBox);

    BorderPane.setAlignment(profileBox, Pos.TOP_LEFT);

    Image profileImg = new Image(Todolist.class.getResourceAsStream("/assets/Image/User_alt.png"));
    Button profileBtn = new Button();
    profileBtn.setGraphic(new ImageView(profileImg));
    profileBtn.setPrefWidth(51);
    profileBtn.setPrefHeight(51);
    profileBtn.getStyleClass().add("btn");

    profileBtn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        try {
          User user = new User();
          user.profile(new Stage());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    Image clockImg = new Image(Todolist.class.getResourceAsStream("/assets/Image/Clock.png"));
    Button clockBtn = new Button();
    clockBtn.setGraphic(new ImageView(clockImg));
    clockBtn.setPrefWidth(51);
    clockBtn.setPrefHeight(51);
    clockBtn.getStyleClass().add("btn");

    clockBtn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        try {
          Time time = new Time();
          time.showTime(new Stage()); // Call the Todolist's add method to display the stage
          // primaryStage.close(); // Close the login stage after successful login
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    Image newTaskImg = new Image(Todolist.class.getResourceAsStream("/assets/Image/Add_ring.png"));
    Button newTaskBtn = new Button("New Task");
    newTaskBtn.setGraphic(new ImageView(newTaskImg));
    newTaskBtn.setPrefWidth(149);
    newTaskBtn.setPrefHeight(51);
    newTaskBtn.getStyleClass().add("taskBtn");

    HBox btnBox = new HBox(profileBtn, clockBtn, newTaskBtn);
    btnBox.setSpacing(10);
    BorderPane.setAlignment(btnBox, Pos.TOP_RIGHT);

    newTaskBtn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        try {
          add(new Stage());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    Text progressText = new Text("Task Progress");
    progressText.getStyleClass().add("pTask");
    ProgressBar taskProgressBar = new ProgressBar(0.4);
    taskProgressBar.setPrefWidth(1280);
    taskProgressBar.setPrefHeight(15);
    VBox progressBox = new VBox(progressText, taskProgressBar);
    progressBox.setSpacing(15);
    HBox topBox = new HBox(profileBox, btnBox);
    topBox.setSpacing(750);
    VBox taskTopBox = new VBox(topBox, progressBox);
    taskTopBox.setSpacing(20);
    borderPane.setTop(taskTopBox);

    // untuk active dan button filter
    Text activeText = new Text("Active");
    taskTopBox.setPadding(new Insets(0, 0, 20, 0));
    activeText.getStyleClass().add("pTask");
    Button allBtn = new Button("All");
    allBtn.setPrefWidth(51);
    allBtn.setPrefHeight(42);
    allBtn.getStyleClass().add("filterBtn");
    Button lowBtn = new Button("Low");
    lowBtn.setPrefWidth(63);
    lowBtn.setPrefHeight(42);
    lowBtn.getStyleClass().add("filterBtn");

    Button medBtn = new Button("Medium");
    medBtn.setPrefWidth(92);
    medBtn.setPrefHeight(42);
    medBtn.getStyleClass().add("filterBtn");
    Button highBtn = new Button("High");
    highBtn.setPrefWidth(68);
    highBtn.setPrefHeight(42);
    highBtn.getStyleClass().add("filterBtn");

    HBox filterBox = new HBox(allBtn, lowBtn, medBtn, highBtn);
    filterBox.setSpacing(10);
    VBox activeBox = new VBox(activeText, filterBox);
    activeBox.setSpacing(12);

    VBox taskBox = new VBox();
    taskBox.getChildren().addAll(activeBox);
    BorderPane.setAlignment(activeBox, Pos.CENTER);
    // borderPane.setCenter(taskBox);
    taskBox.setSpacing(35);

    Button targetButton = allBtn;

    Platform.runLater(() -> {
      targetButton.fire(); // Simulate a button click after UI setup
    });

    // Retrieve task data
    String[] tasksId = getTaskId();
    String[] tasks = getTask();
    String[] tasksLabel = getTasksLabel();
    String[] tasksDesc = getTasksDesc();
    String[] tasksDate = getTasksDate();
    // String[] tasksClock = getTasksClock();
    String[] tasksCategory = getTasksCategory();
    String[] tasksNewCategory = getNewTasksCategory();

    allBtn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        try {
          VBox taskMainBox = new VBox(10);
          Text priorityText = new Text("Priority");
          priorityText.getStyleClass().add("pTask");
          VBox taskBoxMain = new VBox(10);
          taskMainBox.getChildren().clear();

          // Ensure all arrays have the same length
          int minLength = Math.min(tasksId.length,
              Math.min(tasks.length, Math.min(tasksLabel.length, Math.min(tasksDesc.length,
                  Math.min(tasksDate.length, Math.min(tasksCategory.length, tasksNewCategory.length))))));

          for (int i = 0; i < minLength; i++) {
            int index = i;
            if (tasksNewCategory[i].equals("1") || tasksNewCategory[i].equals("2") || tasksNewCategory[i].equals("3")) {
              CheckBox cb = new CheckBox();
              cb.getStyleClass().add("check-box");
              cb.setAlignment(Pos.CENTER);
              Text task1Text = new Text(tasks[i]);
              task1Text.getStyleClass().add("pTask");
              Label taskLabel = new Label(tasksLabel[i]);
              taskLabel.getStyleClass().add("taskLabel");
              Text desText = new Text(tasksDesc[i]);
              desText.getStyleClass().add("descText");
              Image clockTime = new Image(Todolist.class.getResourceAsStream("/assets/Image/Calendar.png"));
              ImageView timeImg = new ImageView(clockTime);
              timeImg.setFitHeight(24);
              timeImg.setFitWidth(24);
              Text time1Text = new Text(tasksDate[i]);
              time1Text.getStyleClass().add("timeText");
              HBox timeImgTextBox = new HBox(timeImg, time1Text);
              timeImgTextBox.setSpacing(3);
              Image calTime = new Image(Todolist.class.getResourceAsStream("/assets/Image/AlarmClock.png"));
              ImageView calImg = new ImageView(calTime);
              calImg.setFitHeight(24);
              calImg.setFitWidth(24);
              // Text clock1Text = new Text(tasksClock[i]);
              // clock1Text.getStyleClass().add("timeText");
              // HBox clockImgTextBox = new HBox(calImg,clock1Text);
              // clockImgTextBox.setSpacing(3);

              HBox taskPBox = new HBox(10);
              HBox taskDescBox = new HBox(10);
              VBox priorityTaskBox = new VBox();
              HBox priorityBox = new HBox(10);
              priorityBox.setAlignment(Pos.CENTER_LEFT);

              taskPBox.getChildren().addAll(task1Text, taskLabel);
              taskDescBox.getChildren().addAll(desText, timeImgTextBox);
              priorityTaskBox.getChildren().addAll(taskPBox, taskDescBox);
              priorityBox.getChildren().addAll(cb, priorityTaskBox);
              taskBoxMain.getChildren().add(priorityBox);

              task1Text.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                  // Get details of the task based on tasksId[index]
                  currentTaskId = tasksId[index];
                  try {
                    detailTask(new Stage());
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                }
              });

              cb.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                  // Update UI based on current selection
                  boolean isSelected = cb.isSelected();
                  if (isSelected) {
                    tasksNewCategory[index] = "4";

                    Connection connection = Dbconnect.getConnect();
                    String sql = "UPDATE tasks SET new_priority_id = '4' WHERE id = '" + tasksId[index] + "'";
                    try {
                      PreparedStatement statement = connection.prepareStatement(sql);
                      statement.executeUpdate();
                      showStage.close();
                      Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                          try {
                            Todolist.show(new Stage());
                          } catch (Exception e) {
                            e.printStackTrace();
                          }
                        }
                      });

                    } catch (SQLException e) {
                      e.printStackTrace();
                    }
                  }
                }
              });

            }
          }
          // untuk menampilkan semua tugas dengan tingkat priority low, medium, high
          taskBox.getChildren().clear();
          taskBox.getChildren().addAll(activeBox);
          taskBox.getChildren().addAll(taskBoxMain);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    lowBtn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        try {

          VBox taskMainBox = new VBox(10);
          Text priorityText = new Text("Priority");
          priorityText.getStyleClass().add("pTask");
          VBox taskBoxMain = new VBox(10);
          taskMainBox.getChildren().clear();
          // **Execute the for loop only when lowBtn is clicked**
          for (int i = 0; i < tasks.length; i++) {
            int index = i;
            if (tasksNewCategory[i].equals("3")) {
              CheckBox cb = new CheckBox();
              cb.getStyleClass().add("check-box");
              cb.setAlignment(Pos.CENTER);
              Text task1Text = new Text(tasks[i]);
              task1Text.getStyleClass().add("pTask");
              Label taskLabel = new Label(tasksLabel[i]);
              taskLabel.getStyleClass().add("taskLabel");
              Text desText = new Text(tasksDesc[i]);
              desText.getStyleClass().add("descText");
              Image clockTime = new Image(Todolist.class.getResourceAsStream("/assets/Image/Calendar.png"));
              ImageView timeImg = new ImageView(clockTime);
              timeImg.setFitHeight(24);
              timeImg.setFitWidth(24);
              Text time1Text = new Text(tasksDate[i]);
              time1Text.getStyleClass().add("timeText");
              HBox timeImgTextBox = new HBox(timeImg, time1Text);
              timeImgTextBox.setSpacing(3);
              Image calTime = new Image(Todolist.class.getResourceAsStream("/assets/Image/AlarmClock.png"));
              ImageView calImg = new ImageView(calTime);
              calImg.setFitHeight(24);
              calImg.setFitWidth(24);
              // Text clock1Text = new Text(tasksClock[i]);
              // clock1Text.getStyleClass().add("timeText");
              // HBox clockImgTextBox = new HBox(calImg,clock1Text);
              // clockImgTextBox.setSpacing(3);

              HBox taskPBox = new HBox(10);
              HBox taskDescBox = new HBox(10);
              VBox priorityTaskBox = new VBox();
              HBox priorityBox = new HBox(10);
              priorityBox.setAlignment(Pos.CENTER_LEFT);

              taskPBox.getChildren().addAll(task1Text, taskLabel);
              taskDescBox.getChildren().addAll(desText, timeImgTextBox);
              priorityTaskBox.getChildren().addAll(taskPBox, taskDescBox);
              priorityBox.getChildren().addAll(cb, priorityTaskBox);
              taskBoxMain.getChildren().add(priorityBox);

              cb.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                  // Update UI based on current selection
                  boolean isSelected = cb.isSelected();
                  if (isSelected) {
                    tasksNewCategory[index] = "4";

                    Connection connection = Dbconnect.getConnect();
                    String sql = "UPDATE tasks SET new_priority_id = '4' WHERE id = '" + tasksId[index] + "'";
                    try {
                      PreparedStatement statement = connection.prepareStatement(sql);
                      statement.executeUpdate();
                      showStage.close();
                      Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                          try {
                            Todolist.show(new Stage());
                          } catch (Exception e) {
                            e.printStackTrace();
                          }
                        }
                      });

                    } catch (SQLException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                    }
                  }
                }
              });
            }
          }
          // untuk menghapus semua isi taskBox
          taskBox.getChildren().clear();
          taskBox.getChildren().addAll(activeBox);
          taskBox.getChildren().addAll(taskBoxMain);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    // untuk menampilkan tugas dengan tingkat priority medium
    medBtn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        try {
          Text priorityText = new Text("Priority");
          priorityText.getStyleClass().add("pTask");
          VBox taskBoxMain = new VBox(10);

          // **Execute the for loop only when lowBtn is clicked**
          for (int i = 0; i < tasks.length; i++) {
            int index = i;
            if (tasksNewCategory[i].equals("2")) {
              CheckBox cb = new CheckBox();
              cb.getStyleClass().add("check-box");
              cb.setAlignment(Pos.CENTER);
              Text task1Text = new Text(tasks[i]);
              task1Text.getStyleClass().add("pTask");
              Label taskLabel = new Label(tasksLabel[i]);
              taskLabel.getStyleClass().add("taskLabel");
              Text desText = new Text(tasksDesc[i]);
              desText.getStyleClass().add("descText");
              Image clockTime = new Image(Todolist.class.getResourceAsStream("/assets/Image/Calendar.png"));
              ImageView timeImg = new ImageView(clockTime);
              timeImg.setFitHeight(24);
              timeImg.setFitWidth(24);
              Text time1Text = new Text(tasksDate[i]);
              time1Text.getStyleClass().add("timeText");
              HBox timeImgTextBox = new HBox(timeImg, time1Text);
              timeImgTextBox.setSpacing(3);
              Image calTime = new Image(Todolist.class.getResourceAsStream("/assets/Image/AlarmClock.png"));
              ImageView calImg = new ImageView(calTime);
              calImg.setFitHeight(24);
              calImg.setFitWidth(24);
              // Text clock1Text = new Text(tasksClock[i]);
              // clock1Text.getStyleClass().add("timeText");
              // HBox clockImgTextBox = new HBox(calImg,clock1Text);
              // clockImgTextBox.setSpacing(3);

              HBox taskPBox = new HBox(10);
              HBox taskDescBox = new HBox(10);
              VBox priorityTaskBox = new VBox();
              HBox priorityBox = new HBox(10);
              priorityBox.setAlignment(Pos.CENTER_LEFT);

              taskPBox.getChildren().addAll(task1Text, taskLabel);
              taskDescBox.getChildren().addAll(desText, timeImgTextBox);
              priorityTaskBox.getChildren().addAll(taskPBox, taskDescBox);
              priorityBox.getChildren().addAll(cb, priorityTaskBox);
              taskBoxMain.getChildren().add(priorityBox);

              cb.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                  // Update UI based on current selection
                  boolean isSelected = cb.isSelected();
                  if (isSelected) {
                    tasksNewCategory[index] = "4";
                    Connection connection = Dbconnect.getConnect();
                    String sql = "UPDATE tasks SET new_priority_id = '4' WHERE id = '" + tasksId[index] + "'";
                    try {
                      PreparedStatement statement = connection.prepareStatement(sql);
                      statement.executeUpdate();
                      showStage.close();
                      Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                          try {
                            Todolist.show(new Stage());
                          } catch (Exception e) {
                            e.printStackTrace();
                          }
                        }
                      });
                    } catch (SQLException e) {
                      e.printStackTrace();
                    }
                  }
                }
              });
            }
          }
          // untuk menghapus semua isi taskBox
          taskBox.getChildren().clear();
          taskBox.getChildren().addAll(activeBox);
          taskBox.getChildren().addAll(taskBoxMain);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    // untuk menampilkan tugas dengan tingkat priority high
    highBtn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        try {

          VBox taskMainBox = new VBox(10);
          Text priorityText = new Text("Priority");
          priorityText.getStyleClass().add("pTask");
          VBox taskBoxMain = new VBox(10);

          taskMainBox.getChildren().clear();

          // **Execute the for loop only when lowBtn is clicked**
          for (int i = 0; i < tasks.length; i++) {
            int index = i;
            if (tasksNewCategory[i].equals("1")) {
              CheckBox cb = new CheckBox();
              cb.getStyleClass().add("check-box");
              cb.setAlignment(Pos.CENTER);
              Text task1Text = new Text(tasks[i]);
              task1Text.getStyleClass().add("pTask");
              Label taskLabel = new Label(tasksLabel[i]);
              taskLabel.getStyleClass().add("taskLabel");
              Text desText = new Text(tasksDesc[i]);
              desText.getStyleClass().add("descText");
              Image clockTime = new Image(Todolist.class.getResourceAsStream("/assets/Image/Calendar.png"));
              ImageView timeImg = new ImageView(clockTime);
              timeImg.setFitHeight(24);
              timeImg.setFitWidth(24);
              Text time1Text = new Text(tasksDate[i]);
              time1Text.getStyleClass().add("timeText");
              HBox timeImgTextBox = new HBox(timeImg, time1Text);
              timeImgTextBox.setSpacing(3);
              Image calTime = new Image(Todolist.class.getResourceAsStream("/assets/Image/AlarmClock.png"));
              ImageView calImg = new ImageView(calTime);
              calImg.setFitHeight(24);
              calImg.setFitWidth(24);
              // Text clock1Text = new Text(tasksClock[i]);
              // clock1Text.getStyleClass().add("timeText");
              // HBox clockImgTextBox = new HBox(calImg,clock1Text);
              // clockImgTextBox.setSpacing(3);

              HBox taskPBox = new HBox(10);
              HBox taskDescBox = new HBox(10);
              VBox priorityTaskBox = new VBox();
              HBox priorityBox = new HBox(10);
              priorityBox.setAlignment(Pos.CENTER_LEFT);

              taskPBox.getChildren().addAll(task1Text, taskLabel);
              taskDescBox.getChildren().addAll(desText, timeImgTextBox);
              priorityTaskBox.getChildren().addAll(taskPBox, taskDescBox);
              priorityBox.getChildren().addAll(cb, priorityTaskBox);
              taskBoxMain.getChildren().add(priorityBox);

              cb.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                  // Update UI based on current selection
                  boolean isSelected = cb.isSelected();
                  if (isSelected) {
                    tasksNewCategory[index] = "4";

                    Connection connection = Dbconnect.getConnect();
                    String sql = "UPDATE tasks SET new_priority_id = '4' WHERE id = '" + tasksId[index] + "'";
                    try {
                      PreparedStatement statement = connection.prepareStatement(sql);
                      statement.executeUpdate();
                      showStage.close();
                      Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                          try {
                            Todolist.show(new Stage());
                          } catch (Exception e) {
                            e.printStackTrace();
                          }
                        }
                      });

                    } catch (SQLException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                    }
                  }
                }
              });
            }
          }
          // untuk menghapus semua isi taskBox
          taskBox.getChildren().clear();
          taskBox.getChildren().addAll(activeBox);
          taskBox.getChildren().addAll(taskBoxMain);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    Text doneText = new Text("Done");
    doneText.getStyleClass().add("pTask");

    VBox taskMainBox = new VBox(10);
    VBox taskBoxMain = new VBox(10);

    for (int i = 0; i < tasks.length; i++) {
      int index = i;
      if (tasksNewCategory[i].equals("4")) {
        CheckBox cb = new CheckBox();
        cb.setSelected(true);
        cb.getStyleClass().add("check-box");
        cb.setAlignment(Pos.CENTER);
        Text task1Text = new Text(tasks[i]);
        task1Text.getStyleClass().add("taskDoneText");
        Text desText = new Text(tasksDesc[i]);
        desText.getStyleClass().add("descText");
        Image clockTime = new Image(Todolist.class.getResourceAsStream("/assets/Image/Alarmclockgrey.png"));
        ImageView timeImg = new ImageView(clockTime);
        timeImg.setFitHeight(24);
        timeImg.setFitWidth(24);
        Text time1Text = new Text(tasksDate[i]);
        time1Text.getStyleClass().add("timeDoneText");
        HBox timeImgTextBox = new HBox(timeImg, time1Text);
        timeImgTextBox.setSpacing(3);
        Image calTime = new Image(Todolist.class.getResourceAsStream("/assets/Image/Alarmclockgrey.png"));
        ImageView calImg = new ImageView(calTime);
        calImg.setFitHeight(24);
        calImg.setFitWidth(24);

        HBox taskPBox = new HBox(10);
        HBox taskDescBox = new HBox(10);
        VBox priorityTaskBox = new VBox();
        HBox priorityBox = new HBox(10);
        priorityBox.setAlignment(Pos.CENTER_LEFT);

        taskPBox.getChildren().addAll(task1Text);
        taskDescBox.getChildren().addAll(desText, timeImgTextBox);
        priorityTaskBox.getChildren().addAll(taskPBox, taskDescBox);
        priorityBox.getChildren().addAll(cb, priorityTaskBox);
        taskBoxMain.getChildren().add(priorityBox);

        cb.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            // Update UI based on current selection
            boolean isSelected = cb.isSelected();
            if (!isSelected) {
              // tasksNewCategory[index] = "4";

              Connection connection = Dbconnect.getConnect();
              String sql = "UPDATE tasks SET new_priority_id = '" + tasksCategory[index] + "' WHERE id = '"
                  + tasksId[index] + "'";
              try {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.executeUpdate();
                showStage.close();
                Platform.runLater(new Runnable() {
                  @Override
                  public void run() {
                    try {
                      Todolist.show(new Stage());
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  }
                });

              } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            }
          }
        });

      }
    }

    taskMainBox.getChildren().addAll(taskBoxMain);

    VBox centerTaskBox = new VBox(taskBox, doneText, taskMainBox);
    borderPane.setCenter(centerTaskBox);

    Scene scene = new Scene(borderPane, 1280, 768);
    showStage.setTitle("Task");
    showStage.setScene(scene);
    showStage.show();
  }

  public static void detailTask(Stage detailTaskStage) throws Exception {
    BorderPane borderPane = new BorderPane();
    borderPane.getStylesheets().add("/assets/addTaskStyle.css");

    Connection connection = Dbconnect.getConnect();
    String sql = "SELECT * FROM tasks WHERE created_by = ? AND id = ?";

    String sqlColab = "SELECT u.email FROM collaborators c JOIN users u ON c.user_id = u.id WHERE c.task_id = ?";

    try {
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setInt(1, App.currentUserId);
      statement.setString(2, currentTaskId);
      ResultSet rs = statement.executeQuery();
      List<String> task = new ArrayList<>();

      if (rs.next()) {
        task.add(rs.getString("title"));
        task.add(rs.getString("description"));
        task.add(rs.getString("deadline"));
        task.add(rs.getString("new_priority_id"));
        task.add(rs.getString("label"));
      }

      PreparedStatement colabStatement = connection.prepareStatement(sqlColab);
      colabStatement.setString(1, currentTaskId);
      ResultSet rsColab = colabStatement.executeQuery();
      List<String> collaborators = new ArrayList<>();

      while (rsColab.next()) {
        collaborators.add(rsColab.getString("email"));
      }

      borderPane.getStyleClass().add("bgColor");

      VBox box = new VBox();
      box.setAlignment(Pos.CENTER);

      GridPane addPane = new GridPane();
      addPane.setAlignment(Pos.CENTER);
      addPane.setVgap(5);

      Text newText = new Text("Task Detail");
      addPane.add(newText, 0, 1);
      newText.getStyleClass().add("addText");

      Label taskLabel = new Label("Task Name");
      addPane.add(taskLabel, 0, 2);
      taskLabel.getStyleClass().add("labelColor");

      TextField taskInput = new TextField();
      taskInput.setText(task.get(0));
      addPane.add(taskInput, 0, 3);
      taskInput.setPrefWidth(250);
      taskInput.setPrefHeight(35);

      Label descLabel = new Label("Description");
      addPane.add(descLabel, 0, 4);
      descLabel.getStyleClass().add("labelColor");

      TextArea descInput = new TextArea();
      descInput.setText(task.get(1));
      addPane.add(descInput, 0, 5);
      descInput.setPrefWidth(250);
      descInput.setPrefHeight(50);

      Label dateLabel = new Label("Date to end the task");
      addPane.add(dateLabel, 0, 6);
      dateLabel.getStyleClass().add("labelColor");

      DatePicker dateInput = new DatePicker();
      dateInput.setValue(LocalDate.parse(task.get(2)));
      addPane.add(dateInput, 0, 7);
      dateInput.setPrefWidth(120);
      dateInput.setPrefHeight(35);

      Label timeLabel = new Label("Time");
      addPane.add(timeLabel, 0, 8);
      timeLabel.getStyleClass().add("labelColor");

      ComboBox<String> hourInput = new ComboBox<>();
      for (int hour = 0; hour < 24; hour++) {
        hourInput.getItems().add(String.format("%02d", hour));
      }

      ComboBox<String> minuteInput = new ComboBox<>();
      for (int minute = 0; minute < 60; minute++) {
        minuteInput.getItems().add(String.format("%02d", minute));
      }

      String taskTimeWork = task.get(3);
      String[] timeParts = taskTimeWork.split(":");

      if (timeParts.length == 2) {
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        hourInput.setValue(String.format("%02d", hour));
        minuteInput.setValue(String.format("%02d", minute));
      }

      HBox timePicker = new HBox(hourInput, minuteInput);
      addPane.add(timePicker, 0, 9);

      Label priorityLabel = new Label("Priority");
      addPane.add(priorityLabel, 0, 10);
      priorityLabel.getStyleClass().add("labelColor");

      ChoiceBox<String> priorityInput = new ChoiceBox<>();
      priorityInput.getItems().addAll("Low", "Medium", "High");
      if (task.get(4).equals("1")) {
        priorityInput.setValue("High");
      } else if (task.get(4).equals("2")) {
        priorityInput.setValue("Medium");
      } else {
        priorityInput.setValue("Low");
      }
      addPane.add(priorityInput, 0, 11);
      priorityInput.setPrefWidth(120);
      priorityInput.setPrefHeight(35);

      Label tagLabel = new Label("Tag");
      addPane.add(tagLabel, 0, 12);
      tagLabel.getStyleClass().add("labelColor");

      TextField tagInput = new TextField();
      tagInput.setText(task.get(4));
      addPane.add(tagInput, 0, 13);
      tagInput.setPrefWidth(120);
      tagInput.setPrefHeight(35);

      Label colabLabel = new Label("Collaborators");
      addPane.add(colabLabel, 0, 14);
      colabLabel.getStyleClass().add("labelColor");

      VBox colabBox = new VBox();
      colabBox.setSpacing(5);

      for (String email : collaborators) {
        TextField colabInput = new TextField(email);
        Button colabInputBtn = new Button("Remove Collaborator");
        colabBox.getChildren().addAll(colabInput, colabInputBtn);
      }

      addPane.add(colabBox, 0, 15);

      Button addColabBtn = new Button("Add Collaborator");
      addPane.add(addColabBtn, 0, 16);

      addColabBtn.setOnAction(event -> {
        TextField newColabInput = new TextField();
        newColabInput.setPromptText("Enter collaborator email");
        colabBox.getChildren().add(newColabInput);
      });

      Button startBtn = new Button("Start Task");
      startBtn.setPrefWidth(100);
      startBtn.setPrefHeight(51);
      startBtn.getStyleClass().add("btn");

      startBtn.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          try {
            Time time = new Time();
            time.showTime(new Stage());
            startScreenCaptureLoop();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });

      HBox editTaskbox = new HBox(startBtn);
      editTaskbox.setSpacing(40);
      addPane.add(editTaskbox, 0, 17);

      box.getChildren().addAll(addPane);
      BorderPane.setAlignment(box, Pos.CENTER);
      borderPane.setCenter(box);

      Scene scene = new Scene(borderPane, 500, 768);
      detailTaskStage.setTitle("Detail Task");
      detailTaskStage.setScene(scene);
      detailTaskStage.show();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}