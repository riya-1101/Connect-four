package com.example.gameapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    public Controller controller;

    @Override
    public void init() throws Exception {
        super.init();
        System.out.println("START");
    }

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        GridPane rootGridPane = loader.load();

        controller = loader.getController();
        controller.createPlayground();

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(stage.widthProperty());

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);

        Scene scene = new Scene(rootGridPane);
        stage.setScene(scene);
        stage.setTitle("Connect Four");
        stage.setResizable(false);
        stage.show();

    }

    public static void main(String[] args) {

        launch(args);
    }

    public MenuBar createMenu() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(Event -> controller.resetGame());

        MenuItem resetGame = new MenuItem("Reset Game");
        resetGame.setOnAction(Event -> controller.resetGame());

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("Exit Game");
        exitGame.setOnAction(Event -> exit());

        fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItem, exitGame);
        menuBar.getMenus().add(fileMenu);

        //help menu

        Menu helpMenu = new Menu("Help");
        MenuItem aboutHelp = new MenuItem("About Connect4");
        aboutHelp.setOnAction(Event -> about());

        SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();
        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(Event-> aboutMyself());

        helpMenu.getItems().addAll(aboutHelp, separatorMenuItem1, aboutMe);
        menuBar.getMenus().add(helpMenu);

        return menuBar;
    }

    private void aboutMyself() {
        Alert alert= new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Developer");
        alert.setHeaderText("Why i develop this game");
        alert.setContentText("Hi , I'm Riya Choudhary I created this game ,and its my Official first Project. ");
        alert.showAndWait();

    }

    private void about() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect4");
        alert.setHeaderText("How to play Connect Four");
        alert.setContentText(" Connect Four is a two-player connection game in which the players first choose a color and then take turns dropping colored discs from the"+
                " top into a seven-column, six-row vertically suspended grid.\n" +
                        " The pieces fall straight down, occupying the next available space within the column. \n" +
                "The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs. Connect Four is a solved game. \n" +
                "The first player can always win by playing the right moves.");
        alert.showAndWait();

    }

    private void exit() {
        Platform.exit();
        System.exit(0);

    }

}
