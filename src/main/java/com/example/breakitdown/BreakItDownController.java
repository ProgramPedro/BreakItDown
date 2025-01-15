package com.example.breakitdown;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.ArrayList;
import javafx.application.Platform;

public class BreakItDownController {
    @FXML
    private Label submitText;
    @FXML
    private TextField spotifyPlaylistURL;
    @FXML
    private PieChart pieChart;

    private String spotifyURL;

    @FXML
    protected void onSubmitButtonClick() {
        spotifyURL = spotifyPlaylistURL.getText(); //Retrieve spotifyURL from TextField (user input)
        //System.out.println(spotifyURL); //DEBUG
        submitText.setText("Playlist Submitted! loading...");

        new Thread(() -> {
            //Convert the String object to a JSON
            Gson gson = new Gson();
            String json = gson.toJson(spotifyURL);
            System.out.println("PLAYLIST LINK CONVERTED TO JSON");

            //Write the JSON to a file
            try (FileWriter file = new FileWriter("spotifyPlaylist.json")) {
                file.write(json);
                System.out.println("JSON FILE HAS BEEN MADE");
                System.out.println();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Big genre arraylist that will store all the genres pulled from the spotify playlist
            ArrayList<String> genreArrayList = new ArrayList<>();

            //Run the python script
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("python", "main.py", "spotifyPlaylist.json");

                processBuilder.redirectErrorStream(true);

                Process process = processBuilder.start();
                process.waitFor(); //This waits for the script to finish

                //Get input stream of process
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;

                // Read the output line by line and print it
                while ((line = reader.readLine()) != null) {

                    //Line clean up to allow entry into individual genres String array which will then be added to genreArrayList
                    line = line.replace("'", "");
                    line = line.replace("[", "");
                    line = line.replace("]", "");

                    String[] individualGenres = line.split(", ");

                    Collections.addAll(genreArrayList, individualGenres);
                }

                //SORT genreArrayList ALPHABETICALLY
                Collections.sort(genreArrayList);

                //Total number of genres in the arrayList
                int totalGenres = genreArrayList.size();

                //Arraylist Containing cycled words from the genreArrayList
                //Used to count the unique words in the genreArrayList
                ArrayList<String> cycledWords = new ArrayList<>();

                //List that will contain all PieChart data
                //Will hold genre names, and their percentage
                ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

                for (String genre : genreArrayList) {
                    if (!cycledWords.contains(genre)) {
                        cycledWords.add(genre);
                        int count = 0;

                        //Count the number of times that specific genre occurs
                        for (String g : genreArrayList) {
                            if (g.equals(genre)) {
                                count++;
                            }
                        }

                        //Calculate the percentage
                        double percentage = ((double)count/totalGenres) * 100;
                        System.out.printf("%s: %%%.2f", genre, percentage);

                        pieChartData.add(new PieChart.Data(genre, percentage));

                        System.out.println();
                    }
                }

                //Update the UI after processing is complete
                Platform.runLater(() -> {
                    pieChart.setTitle("The Genre Breakdown");
                    pieChart.setData(pieChartData);
                    pieChart.setPrefSize(600, 600);
                    submitText.setText("Your playlist just hit the floor - BREAK IT DOWN!");
                });

                //Wait for python script to finish and get the exit code to end the process
                int exitCode = process.waitFor();
                System.out.println("ENDED PROCESS");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}