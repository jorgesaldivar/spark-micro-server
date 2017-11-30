package com.jorgesaldivar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static spark.Spark.*;

public class SparkApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparkApp.class);

    public static void main(String[] args) {

        get("/docker-running-images", (req, res) ->
                getDockerRunningImages()


        );
    }

    private static String getDockerRunningImages() {

        String result = "There was an error while trying to read the docker containers inside this instance";

        try {

            Process process = Runtime.getRuntime().exec("docker ps | awk -F ' ' '{print $2}' | tail -n+2");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                result = String.join(" ", "Image:", line);
            }

        } catch (Exception e) {
            LOGGER.error("Error while trying to read the docker command", e);
        }

        return result;

    }

}