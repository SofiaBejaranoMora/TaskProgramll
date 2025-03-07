module cr.ac.una.taskprogramll {
    requires javafx.controls;
    requires javafx.fxml;

    opens cr.ac.una.taskprogramll to javafx.fxml;
    exports cr.ac.una.taskprogramll;
}
