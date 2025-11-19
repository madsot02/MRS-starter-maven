//Project Imports
package dk.easv.mrs.GUI.Controller;

import dk.easv.mrs.BE.Movie;
import dk.easv.mrs.GUI.Model.MovieModel;

//Java Imports
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class MovieViewController implements Initializable {


    public TextField txtMovieSearch;

    private MovieModel movieModel;
    @FXML
    private TextField txtTitle;
    @FXML
    private TextField txtYear;
    @FXML
    private Button btnClick;
    @FXML
    private TableView<Movie> tblMovies;
    @FXML
    private TableColumn<Movie, String> colTitle;
    @FXML
    private TableColumn<Movie, Integer> colYear;

    public MovieViewController()  {

        try {
            movieModel = new MovieModel();
        } catch (Exception e) {
            displayError(e);
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));

        tblMovies.setItems(movieModel.getObservableMovies());

        tblMovies.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, selectedMovie) -> {
            if(selectedMovie != null) {
                txtTitle.setText(selectedMovie.getTitle());
                txtYear.setText(selectedMovie.getYear() + "");
            }
        } );

        txtMovieSearch.textProperty().addListener((observableValue, oldValue, newValue) -> {
            try {
                movieModel.searchMovie(newValue);
            } catch (Exception e) {
                displayError(e);
                e.printStackTrace();
            }
        });

    }

    private void displayError(Throwable t)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(t.getMessage());
        alert.showAndWait();
    }

    @FXML
    private void btnHandleClick(ActionEvent actionEvent) throws Exception {
        String title = txtTitle.getText();
        int year = Integer.parseInt(txtYear.getText());

        Movie newMovie = new Movie(-1 ,year, title);

        movieModel.createMovie(newMovie);
    }

    @FXML
    private void btnHandleUpdate(ActionEvent actionEvent) throws Exception {
        Movie selectedMovie = tblMovies.getSelectionModel().getSelectedItem();

        if (selectedMovie != null){
            // update movie based on textfield inputs from user
            selectedMovie.setTitle(txtTitle.getText());
            selectedMovie.setYear(Integer.parseInt(txtYear.getText()));

            // Update movie in DAL layer (through the layers)
            movieModel.updateMovie(selectedMovie);

            // ask controls to refresh their content
            tblMovies.refresh();
            tblMovies.scrollTo(selectedMovie);
            tblMovies.getSelectionModel().select(tblMovies.getItems().indexOf(selectedMovie));
        }
    }

    @FXML
    private void btnHandleDelete(ActionEvent actionEvent) throws Exception {
        Movie selectedMovie = tblMovies.getSelectionModel().getSelectedItem();

        if(selectedMovie != null){
            try{
                movieModel.deleteMovie(selectedMovie);
            }
            catch (Exception err) {
                displayError(err);
            }
        }
    }
}
