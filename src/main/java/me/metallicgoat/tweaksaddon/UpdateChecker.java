package me.metallicgoat.tweaksaddon;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateChecker {

  public static void checkForUpdate(String currentVersion) {
    try {
      final URL url = new URL("https://api.github.com/repos/MetallicGoat/MBedwarsTweaks/releases/latest");
      final HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");

      final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      final StringBuilder response = new StringBuilder();

      String inputLine;

      while ((inputLine = in.readLine()) != null)
        response.append(inputLine);

      in.close();
      con.disconnect();

      final String latestVersion = parseVersion(response.toString());

      if (latestVersion == null) {
        printFail();
        return;
      }

      if (!latestVersion.equals(currentVersion))
        printNewUpdate(latestVersion);

    } catch (Exception e) {
      printFail();
    }
  }

  private static void printNewUpdate(String newVersion) {
    Console.printWarn(
        "-----------------------------------------------------------------",
        "We found an update! Version " + newVersion + " is available for download here:",
        "https://github.com/MetallicGoat/MBedwarsTweaks/releases/latest",
        "-----------------------------------------------------------------"
    );
  }

  private static void printFail() {
    Console.printWarn(
        "-----------------------------------------------------------------",
        "Failed to check for Tweaks updates! Check your server's internet connection.",
        "Update checks on startup can be disabled in the config.yml",
        "-----------------------------------------------------------------"
    );
  }

  private static String parseVersion(String jsonResponse) {
    final Pattern pattern = Pattern.compile("\"tag_name\":\"([^\"]+)\"");
    final Matcher matcher = pattern.matcher(jsonResponse);

    if (matcher.find())
      return matcher.group(1).replace("v", "");

    return null;
  }
}
