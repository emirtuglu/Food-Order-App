package com.example;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class SwitchButton extends StackPane {
    private final Rectangle back = new Rectangle(30, 10, Color.RED);
    private final Button button = new Button();
    private String buttonStyleOff = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 0.2, 0.0, 0.0, 2); -fx-background-color: WHITE;";
    private String buttonStyleOn = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 0.2, 0.0, 0.0, 2); -fx-background-color: #00893d;";
    private boolean state;

    private void init() {
        getChildren().addAll(back, button);
        setMinSize(30, 15);
        back.maxWidth(30);
        back.minWidth(30);
        back.maxHeight(10);
        back.minHeight(10);
        back.setArcHeight(back.getHeight());
        back.setArcWidth(back.getHeight());
        back.setFill(Color.valueOf("#ced5da"));
        Double r = 2.0;
        button.setShape(new Circle(r));
        setAlignment(button, Pos.CENTER_LEFT);
        button.setMaxSize(15, 15);
        button.setMinSize(15, 15);
        button.setStyle(buttonStyleOff);

        button.setOnMouseClicked(event -> {
            // Toggle the switch state
            if (state) {
                button.setStyle(buttonStyleOff);
                back.setFill(Color.valueOf("#ced5da"));
                setAlignment(button, Pos.CENTER_LEFT);
                state = false;
            } else {
                button.setStyle(buttonStyleOn);
                back.setFill(Color.valueOf("#80C49E"));
                setAlignment(button, Pos.CENTER_RIGHT);
                state = true;
            }
        });
    }

    public SwitchButton() {
        init();
        setOnMouseClicked(event -> {
            // Skip handling the click if it was on the button itself
            if (event.getTarget() == button) {
                event.consume();
            }
        });
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
