package seng202.group6.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import seng202.group6.Models.Crime;
import seng202.group6.Services.Filter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Controller class for data screen in user interface, associated with dataScreen.fxml.
 * Is a child class of DataController
 */

public class DataController extends MasterController implements Initializable {

    private Set<String> types = new HashSet<String>();
    private Set<String> locations = new HashSet<String>();


    @FXML
    private Button homeButton;

    @FXML
    private Button mapButton;

    @FXML
    private Button dataButton;

    @FXML
    private Button importButton;

    @FXML
    protected TableView<Crime> tableView;

    @FXML
    private TableColumn<Crime, String> caseNumColumn;

    @FXML
    private TableColumn<Crime, String> primaryDescColumn;

    @FXML
    private TableColumn<Crime, String> locationColumn;

    @FXML
    private TableColumn<Crime, LocalDateTime> dateColumn;

    @FXML
    private Button viewCrime;

    @FXML
    private Text noDataText;

    @FXML
    private Text notSelectedText;

    @FXML
    private VBox filterBox;

    @FXML
    private DatePicker startDate;

    @FXML
    private DatePicker endDate;

    @FXML
    private MenuButton crimeTypeDropdown;

    @FXML
    private MenuButton locationDropdown;

    @FXML
    private TextField wardSearch;

    @FXML
    private TextField beatSearch;

    @FXML
    private CheckBox isArrest;

    @FXML
    private CheckBox isDomestic;

    @FXML
    private Button applyButton;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button resetButton;

    @FXML
    private MenuItem mostArea;

    @FXML
    private MenuItem leastArea;

    @FXML
    private MenuItem mostCrime;

    @FXML
    private MenuItem leastCrime;


    /**
     * Method to initialize data scene, checks if there has been data imported first,
     * if there has it will show a data with all crimes in a table, and give an optional button
     * to click to view a more detailed view of a specific crime. An error message is shown
     * rather than the table and button if there has been no data imported
     */

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        if (crimeData != null) {

            buildFilterSets();
            buildDropdowns();

            noDataText.setVisible(false);
            tableView.setVisible(true);
            viewCrime.setVisible(true);
            filterBox.setVisible(true);

            caseNumColumn.setCellValueFactory(new PropertyValueFactory<Crime, String>("caseNumber"));
            primaryDescColumn.setCellValueFactory(new PropertyValueFactory<Crime, String>("primaryDescription"));
            locationColumn.setCellValueFactory(new PropertyValueFactory<Crime, String>("locationDescription"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<Crime, LocalDateTime>("date"));

            tableView.setItems(FXCollections.observableArrayList(filteredCrimeData));
        } else {

            noDataText.setVisible(true);
            tableView.setVisible(false);
            viewCrime.setVisible(false);
            filterBox.setVisible(false);
        }

    }

    /**
     * Method to view detailed description of a specific crime, checks if a crime is selected
     * from the data table and returns error message if not. Calls function from MasterController
     * if a crime has been selected
     * @param event Button click event when view more info button is clicked
     * @throws IOException
     */
    public void selectCrime(ActionEvent event) throws IOException {
        Crime crime = tableView.getSelectionModel().getSelectedItem();
        if (crime != null) {
            notSelectedText.setVisible(false);
            launchViewScreen(crime);
        } else {
            notSelectedText.setVisible(true);
        }
    }

    /**
     * Method to call change to home screen method in MasterController when the home button
     * is clicked
     * @throws IOException
     */

    public void clickHome() throws IOException {
        changeToHomeScreen();
    }

    /**
     * Method to call change to map screen method in MasterController when the map button
     * is clicked
     * @throws IOException
     */

    public void clickMap() throws IOException {
        changeToMapScreen();
    }

    /**
     * Method to call change to import screen method in MasterController when the import button
     * is clicked
     * @throws IOException
     */

    public void clickImport() throws IOException {
        changeToImportScreen();
    }


    public void clickApply() {

        Filter filter = new Filter();
        filter.setStart(startDate.getValue());
        filter.setEnd(endDate.getValue());

        Set<String> selectedTypes = new HashSet<>();
        for (MenuItem item: crimeTypeDropdown.getItems()) {
            CheckBox box = (CheckBox) ((CustomMenuItem)item).getContent();
            if (box.isSelected()) {
                selectedTypes.add(box.getText());
            }
        }
        filter.setTypes(selectedTypes);

        Set<String> selectedLocations = new HashSet<>();
        for (MenuItem item: locationDropdown.getItems()) {
            CheckBox box = (CheckBox) ((CustomMenuItem)item).getContent();
            if (box.isSelected()) {
                selectedLocations.add(box.getText());
            }
        }
        filter.setLocations(selectedLocations);


        filter.setArrest(isArrest.isSelected());
        filter.setDomestic(isDomestic.isSelected());

        filter.setBeats(beatSearch.getText());
        filter.setWards(wardSearch.getText());


        filteredCrimeData = filter.applyFilter(crimeData);

        tableView.setItems(FXCollections.observableArrayList(filteredCrimeData));


    }


    public void clickAdd() throws IOException {
        Crime crime = new Crime();
        EditController.isNewCrime = true;
        launchEditScreen(crime);
    }

    public void clickEdit(ActionEvent event) throws IOException{
        Crime crime = tableView.getSelectionModel().getSelectedItem();
        if (crime != null) {
            notSelectedText.setVisible(false);
            EditController.isNewCrime = false;
            launchEditScreen(crime);
        } else {
            notSelectedText.setVisible(true);
        }
    }

    public void clickDelete() throws IOException {
        int index = tableView.getSelectionModel().getFocusedIndex();
        crimeData.remove(index);
        changeToDataScreen();

    }

    private void buildFilterSets() {
        for (Crime crime :crimeData) {
            types.add(crime.getPrimaryDescription());
            locations.add(crime.getLocationDescription());
        }
    }

    private void buildDropdowns() {
        for (String type: types) {
            if (type.equals("")) {
                type = "NO TYPE GIVEN";
            }
            CheckBox newBox = new CheckBox(type);
            CustomMenuItem newItem = new CustomMenuItem(newBox);
            newItem.setHideOnClick(false);

            crimeTypeDropdown.getItems().add(newItem);
        }

        for (String location: locations) {
            if (location.equals("")) {
                location = "NO LOCATION GIVEN";
            }
            CheckBox newBox = new CheckBox(location);
            CustomMenuItem newItem = new CustomMenuItem(newBox);
            newItem.setHideOnClick(false);
            locationDropdown.getItems().add(newItem);
        }
    }

    public void clickReset() {
        filteredCrimeData = crimeData;
        tableView.setItems(FXCollections.observableArrayList(crimeData));
    }

    public void clickMostArea() {

    }

    public void clickLeastArea() {

    }

    public void clickMostCrime() {

    }

    public void clickLeastCrime() {

    }

}
