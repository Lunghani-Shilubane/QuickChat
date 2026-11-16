package com.app;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.random.RandomGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path MESSAGES_FILE = Path.of("messages.json");

    private record MessageReport(
            long messageID, String messageHash, String recipient, String message) {}

    private final String[][] users = new String[2][3];
    private int userCount = 0;
    private final RandomGenerator random = RandomGenerator.getDefault();
    private final ArrayList<MessageReport> sentMessages = new ArrayList<>();
    private final ArrayList<MessageReport> discardedMessages = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        showRegistrationForm(stage);
    }

    private Scene createScene(VBox root, double width, double height) {
        Scene scene = new Scene(root, width, height);
        var stylesheet = App.class.getResource("/com/app/app.css");
        if (stylesheet == null) {
            throw new IllegalStateException("App stylesheet /com/app/app.css was not found.");
        }
        scene.getStylesheets().add(stylesheet.toExternalForm());
        return scene;
    }

    private void showRegistrationForm(Stage stage) {
        Label status = new Label("Welcome to the Chatty Chat App!");

        TextField username = new TextField();
        Label usernameLabel = new Label("Username");

        PasswordField password = new PasswordField();
        Label passwordLabel = new Label("Password");

        TextField number = new TextField();
        Label numberLabel = new Label("Phone number");

        Button submitButton = new Button("Submit");

        submitButton.setOnAction(event -> {
            StringBuilder errors = new StringBuilder();

            if (username.getText().isBlank()) {
                errors.append("Username is required.\n");
            } else if (username.getText().length() > 5) {
                errors.append("Username must be no more than 5 characters.\n");
            } else if (!username.getText().contains("_")) {
                errors.append("Please ensure that your username contains an underscore.\n");
            }

            System.out.println("Length >= 8: " + (password.getText().length() >= 8));
            System.out.println("Has uppercase: " + password.getText().matches(".*[A-Z].*"));
            System.out.println("Has digit: " + password.getText().matches(".*[0-9].*"));
            System.out.println("Has allowed special character: "
        + password.getText().matches(".*[!@#$%^&*()].*"));

            if (password.getText().isBlank()) {
                errors.append("Password is required.\n");
            } else if (password.getText().length() < 8
                    || !password.getText().matches(".*[A-Z].*")
                    || !password.getText().matches(".*[0-9].*")
                    || !password.getText().matches(".*[!@#$%^&*()].*")) {
                errors.append(
                        "Password must be at least 8 characters long and contain an uppercase letter, a number, and a special character.\n");
            }

            if (number.getText().isBlank()) {
                errors.append("Number is required.\n");
            } else if (!number.getText().matches("\\+27\\d{9}")
                    || number.getText().length() != 12) {
                errors.append(
                        "Number must contain exactly 12 digits and contain the country code (+27).\n");
            }

            if (errors.length() > 0) {
                status.setWrapText(true);
                status.setText(errors.toString());
                return;
            }

            if (userCount < users.length) {
                users[userCount][0] = username.getText();
                users[userCount][1] = password.getText();
                users[userCount][2] = number.getText();
                userCount++;
            }

            status.setText("Registration successful. Welcome " + username.getText());
            PauseTransition pause = new PauseTransition(Duration.millis(1000));
            pause.setOnFinished(ignored -> {
                showLoginForm(stage);
            });
            pause.play();
        });

        VBox layout = new VBox(
                10,
            status,
                new VBox(5, usernameLabel, username),
                new VBox(5, passwordLabel, password),
                new VBox(5, numberLabel, number),
                submitButton
        );
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        stage.setTitle("Chatty Chat");
        stage.setScene(createScene(layout, 400, 300));
        stage.show();
    }

    private void showLoginForm(Stage stage) {
        Label loginMessage = new Label("Please login to proceed");

        TextField username = new TextField();
        Label usernameLabel = new Label("Please enter your username");

        PasswordField password = new PasswordField();
        Label passwordLabel = new Label("Please enter your password");

        Button submitButton = new Button("Login");

        submitButton.setOnAction(event -> {
            boolean validUser = false;
            for (int i = 0; i < userCount; i++) {
                if (username.getText().equals(users[i][0])
                        && password.getText().equals(users[i][1])) {
                    validUser = true;
                    break;
                }
            }

            if (validUser) {
                loginMessage.setText("Login successful.");
                 PauseTransition pause = new PauseTransition(Duration.millis(1000));
            pause.setOnFinished(ignored -> {
                showAppMenu(stage);
            });
            pause.play();
                
            } else {
                loginMessage.setText("Invalid username or password.");
            }
        });

         VBox layout = new VBox(
                10,
              loginMessage,
                new VBox(5, usernameLabel, username),
                new VBox(5, passwordLabel, password),
                submitButton
        );
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        stage.setScene(createScene(layout, 400, 220));
    }

    private void showAppMenu(Stage stage) {

         Label appMessage = new Label("Please select an option to proceed");

         Button chatBtn = new Button("Chat");
            chatBtn.setOnAction(event -> {
                handleChat(stage);
            });
         Button recentBtn = new Button("All Messages");
            recentBtn.setOnAction(event -> {
                handleRecentMessages(stage);
            });
         Button exitBtn = new Button("Exit");
            exitBtn.setOnAction(event -> {
                stage.close();
            });
           
         



         VBox layout = new VBox(10, appMessage, chatBtn, recentBtn, exitBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

         stage.setScene(createScene(layout, 400, 220));
    }

    
    //Button functionality
    private void handleChat(Stage stage) {
        
        TextField messageCountField = new TextField();
        Label messageCountLabel = new Label("How many messages would you like to send?");

        Button proceedButton = new Button("Proceed");
        proceedButton.setOnAction(event -> {
            int messageCount = messageCountField.getText().isEmpty() ? 0 : Integer.parseInt(messageCountField.getText());
            if (messageCount <= 0) {
                Label errorLabel = new Label("Please enter a valid number of messages.");
                VBox errorLayout = new VBox(10, errorLabel);
                errorLayout.setAlignment(Pos.CENTER);
                errorLayout.setPadding(new Insets(20));
                stage.setScene(createScene(errorLayout, 400, 220));
                return;
            }
            showMessageForm(stage, messageCount);
        });

        VBox layout = new VBox(10, messageCountLabel, messageCountField, proceedButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        stage.setScene(createScene(layout, 400, 220));

    }

    private void showMessageForm(Stage stage, int messageCount) {
        final int[] totalMessages = {0};
        final int[] messageIndex = {0};

        Label messageNumberLabel = new Label("Message 1 of " + messageCount);
        Label status = new Label("Messages sent: 0/" + messageCount);
        Label feedback = new Label();
        feedback.setWrapText(true);

        TextField recipientField = new TextField();
        Label recipientLabel = new Label("Please enter the recipient's phone number (or enter 0 to go back to the main menu)");

        TextField messageField = new TextField();
        Label messageLabel = new Label("Please enter your message");

        Button sendButton = new Button("Send");
        Button storeMessagesButton = new Button("Save Message");
        Consumer<Boolean> processMessage =
                saveToJson -> {
                    String recipient = recipientField.getText().trim();
                    String message = messageField.getText().trim();

                    if (recipient.equals("0") || message.equals("0")) {
                        feedback.setText("Discarding message and going back to the main menu...");
                        if (!recipient.isBlank() && !message.isBlank()) {
                            discardedMessages.add(new MessageReport(0, "", recipient, message));
                        }

                        PauseTransition pause = new PauseTransition(Duration.millis(1000));
                        pause.setOnFinished(ignored -> showAppMenu(stage));
                        pause.play();
                        return;
                    }
                    if (recipient.isBlank()) {
                        feedback.setText("Please enter a recipient before sending.");
                        return;
                    }
                    if (message.isBlank()) {
                        feedback.setText("Please enter a message before sending.");
                        return;
                    }
                    if (messageField.getText().length() > 250) {
                        feedback.setText("Please enter a message of less than 250 characters.");
                        return;
                    }

                    long messageID = 1_000_000_000L + random.nextLong(90_000_000L);
                    String idString = String.valueOf(messageID);
                    String firstTwoDigits = idString.substring(0, 2);
                    String[] words = message.split("\\s+");
                    String firstWord = words[0];
                    String lastWord = words[words.length - 1];
                    String messageHash =
                            (firstTwoDigits + ":" + messageIndex[0] + ":" + firstWord + lastWord)
                                    .toUpperCase();

                    sentMessages.add(new MessageReport(messageID, messageHash, recipient, message));
                    totalMessages[0]++;
                    messageIndex[0]++;
                    status.setText("Messages sent: " + totalMessages[0] + "/" + messageCount);

                    if (saveToJson && !storeMessages(feedback)) {
                        sentMessages.remove(sentMessages.size() - 1);
                        totalMessages[0]--;
                        messageIndex[0]--;
                        status.setText("Messages sent: " + totalMessages[0] + "/" + messageCount);
                        return;
                    }

                    if (!saveToJson) {
                        feedback.setText("");
                    }

                    if (totalMessages[0] == messageCount) {
                        if (saveToJson) {
                            feedback.setText("All messages have been sent. " + feedback.getText());
                        } else {
                            feedback.setText("All messages have been sent.");
                        }
                        recipientField.setDisable(true);
                        messageField.setDisable(true);
                        sendButton.setDisable(true);
                        storeMessagesButton.setDisable(true);

                        PauseTransition pause = new PauseTransition(Duration.millis(1000));
                        pause.setOnFinished(ignored -> showMessageReport(stage, sentMessages, 0));
                        pause.play();
                    } else {
                        messageNumberLabel.setText(
                                "Message " + (messageIndex[0] + 1) + " of " + messageCount);
                        recipientField.clear();
                        messageField.clear();
                        if (saveToJson) {
                            recipientField.requestFocus();
                        }
                    }
                };
        sendButton.setOnAction(event -> processMessage.accept(false));
        storeMessagesButton.setOnAction(event -> processMessage.accept(true));

        VBox layout = new VBox(
                10,
                messageNumberLabel,
                status,
                feedback,
                new VBox(5, recipientLabel, recipientField),
                new VBox(5, messageLabel, messageField),
                sendButton,
                storeMessagesButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        stage.setScene(createScene(layout, 400, 360));
    }

    private boolean storeMessages(Label feedback) {
        try {
            writeMessagesToJson(sentMessages);
            feedback.setText(
                    "Saved "
                            + sentMessages.size()
                            + " messages to "
                            + MESSAGES_FILE.toAbsolutePath());
            return true;
        } catch (IOException exception) {
            feedback.setText("Could not save messages.json: " + exception.getMessage());
            return false;
        }
    }

    private void writeMessagesToJson(List<MessageReport> messages) throws IOException {
        JsonArray jsonMessages = new JsonArray();
        for (MessageReport report : messages) {
            JsonObject jsonMessage = new JsonObject();
            jsonMessage.addProperty("messageID", report.messageID());
            jsonMessage.addProperty("messageHash", report.messageHash());
            jsonMessage.addProperty("recipient", report.recipient());
            jsonMessage.addProperty("message", report.message());
            jsonMessages.add(jsonMessage);
        }

        Files.writeString(MESSAGES_FILE, GSON.toJson(jsonMessages), StandardCharsets.UTF_8);
    }

    private List<MessageReport> loadMessagesFromJson() throws IOException {
        if (!Files.exists(MESSAGES_FILE)) {
            return List.of();
        }

        try (Reader reader = Files.newBufferedReader(MESSAGES_FILE, StandardCharsets.UTF_8)) {
            JsonElement root;
            try {
                root = JsonParser.parseReader(reader);
            } catch (com.google.gson.JsonParseException exception) {
                throw new IOException("messages.json contains invalid JSON.", exception);
            }

            if (!root.isJsonArray()) {
                throw new IOException("messages.json must contain a JSON array.");
            }

            JsonArray jsonMessages = root.getAsJsonArray();
            List<MessageReport> loadedMessages = new ArrayList<>();
            for (int i = 0; i < jsonMessages.size(); i++) {
                JsonElement element = jsonMessages.get(i);
                if (!element.isJsonObject()) {
                    throw new IOException("Message entry " + (i + 1) + " is not a JSON object.");
                }

                JsonObject jsonMessage = element.getAsJsonObject();
                long messageID = readJsonLong(jsonMessage, "messageID", i + 1);
                String messageHash = readJsonString(jsonMessage, "messageHash", i + 1);
                String recipient = readJsonString(jsonMessage, "recipient", i + 1);
                String message = readJsonString(jsonMessage, "message", i + 1);
                loadedMessages.add(new MessageReport(messageID, messageHash, recipient, message));
            }
            return loadedMessages;
        }
    }

    private long readJsonLong(JsonObject jsonObject, String field, int entryNumber)
            throws IOException {
        JsonElement value = jsonObject.get(field);
        if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) {
            throw new IOException(
                    "Message entry " + entryNumber + " has an invalid " + field + ".");
        }
        try {
            return value.getAsLong();
        } catch (NumberFormatException exception) {
            throw new IOException(
                    "Message entry " + entryNumber + " has an invalid " + field + ".",
                    exception);
        }
    }

    private String readJsonString(JsonObject jsonObject, String field, int entryNumber)
            throws IOException {
        JsonElement value = jsonObject.get(field);
        if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
            throw new IOException(
                    "Message entry " + entryNumber + " has an invalid " + field + ".");
        }
        return value.getAsString();
    }

    private boolean deleteMessageFromAllSources(MessageReport target) throws IOException {
        List<MessageReport> jsonMessages = loadMessagesFromJson();
        boolean foundInJson =
                jsonMessages.stream()
                        .anyMatch(report -> report.messageID() == target.messageID());
        boolean foundInMemory =
                sentMessages.contains(target) || discardedMessages.contains(target);

        if (!foundInJson && !foundInMemory) {
            return false;
        }

        if (foundInJson) {
            List<MessageReport> remainingJsonMessages =
                    jsonMessages.stream()
                            .filter(report -> report.messageID() != target.messageID())
                            .toList();
            writeMessagesToJson(remainingJsonMessages);
        }

        if (!sentMessages.remove(target)) {
            discardedMessages.remove(target);
        }
        return true;
    }

    private void showMessageReport(
            Stage stage, ArrayList<MessageReport> messages, int reportIndex) {
        MessageReport report = messages.get(reportIndex);

        Label reportLabel = new Label("Message Report " + (reportIndex + 1) + " of " + messages.size());
        Label messageIDLabel = new Label("Message ID: " + report.messageID());
        Label messageHashLabel = new Label("Message Hash: " + report.messageHash());
        Label recipientLabel = new Label("Recipient: " + report.recipient());
        Label messageLabel = new Label("Message: " + report.message());
        Button nextButton =
                new Button(reportIndex + 1 < messages.size() ? "Next Message" : "Done");
                    nextButton.setOnAction(
                            event -> {
                                if (reportIndex + 1 < messages.size()) {
                                    showMessageReport(stage, messages, reportIndex + 1);
                                } else {
                                    showAppMenu(stage);
                                }
                            });

        Button menuButton = new Button("Exit to Menu");
        menuButton.setOnAction(event -> showAppMenu(stage));

        VBox layout = new VBox(
                10,
                reportLabel,
                messageIDLabel,
                messageHashLabel,
                recipientLabel,
                messageLabel,
                nextButton,
                menuButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        stage.setScene(createScene(layout, 400, 300));
    }

    private void handleRecentMessages(Stage stage) {
        handleRecentMessages(stage, "");
    }

    private void handleRecentMessages(Stage stage, String initialSearch) {
        List<MessageReport> sentWithJson = new ArrayList<>(sentMessages);
        Set<Long> sentMessageIDs = new HashSet<>();
        for (MessageReport report : sentMessages) {
            sentMessageIDs.add(report.messageID());
        }

        String fileError = "";
        try {
            for (MessageReport report : loadMessagesFromJson()) {
                if (sentMessageIDs.add(report.messageID())) {
                    sentWithJson.add(report);
                }
            }
        } catch (IOException exception) {
            fileError = "Could not load messages.json: " + exception.getMessage();
        }

        List<MessageReport> allMessages = new ArrayList<>(sentWithJson);
        allMessages.addAll(discardedMessages);

        Label searchInstructions =
                new Label(
                        "Search by message ID or recipient, or type \"longest message\" or "
                                + "\"shortest message\".");
        TextField searchField = new TextField();
        searchField.setPromptText("Enter a message ID, recipient, or length search");
        searchField.setText(initialSearch);
        Button searchButton = new Button("Search");
        Label deleteStatus = new Label();
        deleteStatus.setWrapText(true);
        Label fileStatus = new Label(fileError);
        fileStatus.setWrapText(true);

        VBox messageList = new VBox(10);
        messageList.setPadding(new Insets(10));
        Consumer<MessageReport> deleteMessageAction =
                report -> {
                    try {
                        if (!deleteMessageFromAllSources(report)) {
                            deleteStatus.setText("This message is no longer available.");
                            return;
                        }
                        handleRecentMessages(stage, searchField.getText());
                    } catch (IOException exception) {
                        deleteStatus.setText(
                                "Could not delete message: " + exception.getMessage());
                    }
                };
        Runnable refreshMessages =
                () -> {
                    Predicate<MessageReport> filter =
                            createMessageFilter(searchField.getText(), allMessages);
                    List<MessageReport> filteredSent =
                            sentWithJson.stream().filter(filter).toList();
                    List<MessageReport> filteredDiscarded =
                            discardedMessages.stream().filter(filter).toList();

                    messageList.getChildren().clear();
                    addMessageCategory(
                            messageList,
                            "Sent Messages (including JSON-only messages)",
                            filteredSent,
                            !searchField.getText().isBlank(),
                            deleteMessageAction);
                    addMessageCategory(
                            messageList,
                            "Discarded Messages",
                            filteredDiscarded,
                            !searchField.getText().isBlank(),
                            deleteMessageAction);
                };
        searchButton.setOnAction(event -> refreshMessages.run());
        searchField.setOnAction(event -> refreshMessages.run());
        refreshMessages.run();

        ScrollPane messageScroll = new ScrollPane(messageList);
        messageScroll.setFitToWidth(true);
        VBox.setVgrow(messageScroll, javafx.scene.layout.Priority.ALWAYS);

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(event -> showAppMenu(stage));
        VBox layout =
                new VBox(
                        10,
                        searchInstructions,
                        searchField,
                        searchButton,
                        deleteStatus,
                        fileStatus,
                        messageScroll,
                        backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        stage.setScene(createScene(layout, 500, 700));
    }

    private Predicate<MessageReport> createMessageFilter(
            String searchText, List<MessageReport> allMessages) {
        String query = searchText.trim();
        if (query.isEmpty()) {
            return report -> true;
        }

        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        if (normalizedQuery.equals("longest message")) {
            int longestLength =
                    allMessages.stream()
                            .mapToInt(report -> report.message().length())
                            .max()
                            .orElse(-1);
            return report -> report.message().length() == longestLength;
        }
        if (normalizedQuery.equals("shortest message")) {
            int shortestLength =
                    allMessages.stream()
                            .mapToInt(report -> report.message().length())
                            .min()
                            .orElse(-1);
            return report -> report.message().length() == shortestLength;
        }

        return report ->
                Long.toString(report.messageID()).equals(query)
                        || report.recipient().equalsIgnoreCase(query);
    }

    private void addMessageCategory(
            VBox parent,
            String title,
            List<MessageReport> messages,
            boolean hasSearch,
            Consumer<MessageReport> deleteMessageAction) {
        Label categoryHeader = new Label(title + " (" + messages.size() + ")");
        categoryHeader.getStyleClass().add("category-header");
        parent.getChildren().add(categoryHeader);
        if (messages.isEmpty()) {
            parent.getChildren().add(
                    new Label(hasSearch ? "No matching messages." : "No messages."));
            return;
        }

        for (MessageReport report : messages) {
            Label messageIDLabel = new Label("Message ID: " + report.messageID());
            Label messageHashLabel = new Label("Message Hash: " + report.messageHash());
            Label recipientLabel = new Label("Recipient: " + report.recipient());
            Label messageLabel = new Label("Message: " + report.message());
            for (Label detailLabel :
                    List.of(messageIDLabel, messageHashLabel, recipientLabel, messageLabel)) {
                detailLabel.setMaxWidth(Double.MAX_VALUE);
                detailLabel.setAlignment(Pos.CENTER);
            }
            messageLabel.setWrapText(true);
            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(event -> deleteMessageAction.accept(report));
            VBox messageCard =
                    new VBox(
                            10,
                            messageIDLabel,
                            messageHashLabel,
                            recipientLabel,
                            messageLabel,
                            deleteButton);
            messageCard.setAlignment(Pos.CENTER);
            messageCard.getStyleClass().add("message-card");
            parent.getChildren().add(messageCard);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
