package com.jorgesaldivar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static spark.Spark.get;
import static spark.Spark.port;

/**
 * Micro service using http://sparkjava.com/
 * Retrieves the image name of the running docker containers in the host
 *
 * @author JorgeSaldivar
 */

public class SparkApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparkApp.class);

    public static void main(String[] args) {

        port(4455);

        // Enabling HTTPS/SSL
        //secure(keystoreFilePath, keystorePassword, truststoreFilePath, truststorePassword);

        get("/docker-running-images", (req, res) ->
                {
                    res.type("application/json");
                    String output = getDockerRunningImages();
                    if (output.startsWith("There was an error")) {
                        res.status(500);
                        return "{\"message\":\"Internal Server Error\"}";
                    } else if (output.split(" & ").length > 1)
                        return "{\"images\":\"" + output + "\"}";
                    else
                        return "{\"image\":\"" + output + "\"}";
                }
        );
    }

    private static String getDockerRunningImages() {

        String result = "There are no images running using docker containers";

        try {

            Process process = Runtime.getRuntime().exec(new String[]{
                    "bash", "-c", "docker ps | awk -F ' ' '{print $2}' | tail -n+2"
            });
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            StringBuilder tempResult = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (StringUtils.isBlank(tempResult)) {
                    tempResult.append(line);
                } else {
                    tempResult.append(" & ").append(line);
                }
            }

            if (!StringUtils.isBlank(tempResult))
                result = tempResult.toString();

        } catch (Exception e) {
            result = "There was an error while trying to read the docker containers inside this instance";
            LOGGER.error("Error while trying to read the docker command", e);
        }

        return result.trim();

    }

}