package com.example.gameapp;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

    @FXML
    public GridPane myGridPane;
    @FXML
    public Pane insertPane;
    @FXML
    public Label playerturn;
    @FXML
    public TextField playerOneTextField , playerTwoTextField;
    @FXML
    public Button setNamesButton;

    private boolean isAllowedToInsert = true; //flag to avoid same disk again in fast click
    public static final int Column = 7;
    public static final int Row = 6;
    public static final int Circle_Diameter =80;
    public static final String disc_color = "#24303E";
    public static final String disc2_color = "#4CAA88";
    public static String player1 = "Player One";
    public static String player2 = "Player Two";
    private static boolean isPlayerOneTurn = true;
    private final Disk [] []  DiskArray = new Disk[Row][Column];

        public void createPlayground() {

            Platform.runLater(() -> setNamesButton.requestFocus());

            Shape rectangleWithHole = createGameStructuralGrid();
            myGridPane.add(rectangleWithHole, 0, 1);

           List<Rectangle> rectangleList = createClickableColumn();

            for (Rectangle rectangle: rectangleList) {
                myGridPane.add(rectangle,0,1);
            }

            // Assignment
            setNamesButton.setOnAction(Event -> {
                player1 = playerOneTextField.getText();
                player2 = playerTwoTextField.getText();
                playerturn.setText(isPlayerOneTurn ? player1 : player2 );

            });

        }
        private Shape createGameStructuralGrid(){

            Shape rectangleWithHole = new Rectangle((Column + 1) * Circle_Diameter, (Row + 1) * Circle_Diameter);
            for (int row = 0; row < Row; row++ ) {
                for (int col = 0; col < Column; col++) {
                    Circle circle = new Circle();
                    circle.setRadius(Circle_Diameter / 2);
                    circle.setCenterX(Circle_Diameter / 2);
                    circle.setCenterY(Circle_Diameter / 2);
                    circle.setSmooth(true);

                    circle.setTranslateX(col*(Circle_Diameter + 5)+Circle_Diameter /  4);
                    circle.setTranslateY(row*(Circle_Diameter + 5)+ Circle_Diameter / 4);

                    rectangleWithHole = Shape.subtract(rectangleWithHole, circle);
                }
            }
            rectangleWithHole.setFill(Color.WHITE);
            return rectangleWithHole;
        }
        private List<Rectangle> createClickableColumn() {

            List<Rectangle> rectangleList = new ArrayList<>();
            for (int col = 0; col < Column; col++) {

                Rectangle rectangle = new Rectangle(Circle_Diameter, (Row + 1) * Circle_Diameter);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setTranslateX(col*(Circle_Diameter + 5) + Circle_Diameter / 4);
                rectangle.setOnMouseEntered(Event-> rectangle.setFill(Color.valueOf("#eeeeee26")));
                rectangle.setOnMouseExited(Event-> rectangle.setFill(Color.TRANSPARENT));

                final int column = col ;
                rectangle.setOnMouseClicked(Event ->{
                    if (isAllowedToInsert) {
                        isAllowedToInsert = false; // if the disk is added no more disk will be inserted
                        insetPaneDisk(new Disk(isPlayerOneTurn), column);

                    }
                });
                rectangleList.add(rectangle);

            }
            return rectangleList;
        }
        private void insetPaneDisk(Disk disk, int column){
            int row = Row - 1;
            while (row >= 0){
                if (DiskIfPresent(row , column)==null)
                    break;
                row--;
            }
            if (row< 0) //the column full then no disk will Enter
                return;

            DiskArray[row][column] = disk;        //backend
            insertPane.getChildren().add(disk);   //frontend

            disk.setTranslateX(column*(Circle_Diameter + 5) + Circle_Diameter / 4);
            int CurrentRow = row;

            TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disk);
            translateTransition.setToY(row*(Circle_Diameter + 5)+ Circle_Diameter / 4);
            translateTransition.setOnFinished(Event -> {

                isAllowedToInsert = true; // finally when the disk is dropped allow next player to insert disk
                if (gameEnds(CurrentRow, column)) {
                    GameOver();
                }

                isPlayerOneTurn = ! isPlayerOneTurn;
                playerturn.setText(isPlayerOneTurn ? player1 : player2);
            });
            translateTransition.play();
        }

    private void GameOver() {

            String Winner = isPlayerOneTurn? player1 : player2;
            System.out.println("THE WINNER IS : "+ Winner);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
               alert.setTitle("Connect Four");
               alert.setHeaderText("The Winner is : "+ Winner);
               alert.setContentText("Want to play again ?");

        ButtonType yesBtn = new ButtonType("yes");
        ButtonType noBtn = new ButtonType("No, Exit");
        alert.getButtonTypes().setAll(yesBtn,noBtn);

        Platform.runLater(() -> {
            Optional<ButtonType> clickedBtn = alert.showAndWait();
            if (clickedBtn.isPresent() && clickedBtn.get() == yesBtn){
                //yes so reset
                resetGame();
            }
            else {
                // no then EXIT
                Platform.exit();
                System.exit(0);
            }
        });

    }

    public void resetGame() {

            insertPane.getChildren().clear(); //remove all inserted disk
        for (int row =0; row< DiskArray.length; row++) {
            for (int col = 0; col < DiskArray[row].length; col++) {
                DiskArray[row][col] = null;
            }
        }
        isPlayerOneTurn = true; //let player start the game
        playerturn.setText(player1);

        createPlayground();
    }

    private boolean gameEnds(int row, int column) {

        List<Point2D>verticalPoint = IntStream.rangeClosed(row-3 , row +3)
                .mapToObj(r -> new Point2D(r , column))
                .collect(Collectors.toList());

        List<Point2D> horizontalPoint = IntStream.rangeClosed(column-3 , column+3)
                .mapToObj(col -> new Point2D(row , col))
                .collect(Collectors.toList());

        Point2D startPoint1 = new Point2D(row - 3, column + 3);
        List<Point2D>diagonal1Points = IntStream.rangeClosed(0 , 6)
                .mapToObj(i -> startPoint1
                .add(i, -i)).collect(Collectors.toList());

        Point2D startPoint2 = new Point2D(row - 3, column - 3);
        List<Point2D>diagonal2Points = IntStream.rangeClosed(0 , 6)
                .mapToObj(i -> startPoint2
                        .add(i, i)).collect(Collectors.toList());

        boolean isEnded = checkCombination(verticalPoint) || checkCombination(horizontalPoint)
                 || checkCombination(diagonal1Points) || checkCombination(diagonal2Points);
        return isEnded;
    }

    private boolean checkCombination(List<Point2D> points){

            int chain = 0;
        for ( Point2D point : points) {

            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();

            Disk disk = DiskIfPresent(rowIndexForArray,columnIndexForArray);
            if (disk != null && disk.isPlayerOneMove == isPlayerOneTurn){
                    chain++;

            if (chain == 4){
                return true;
            }}
            else {
                chain = 0;
            }
        }

        return false;
    }
    private Disk DiskIfPresent(int row, int column){// to prevent array index out of bound Exception


            if (row>= Row || row < 0 || column >= Column || column<  0)

               return null;

            return DiskArray[row][column];
    }

    private static class Disk extends Circle{

            private final boolean isPlayerOneMove;
            public Disk(boolean isPlayerOneMove){

                this.isPlayerOneMove = isPlayerOneMove;
                setRadius(Circle_Diameter/2);
                setFill(isPlayerOneMove? Color.valueOf(disc_color) : Color.valueOf(disc2_color));
                setCenterX(Circle_Diameter/2);
                setCenterY(Circle_Diameter/2);

            }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

}
