/*
 * Copyright (c) 2022, 2024, Oracle and/or its affiliates.
 *
 * This source code is licensed under the UPL license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

import java.util.ArrayList;
import java.util.stream.Stream;

public class Test {

  static Download.Browser BROWSER = new Download.Browser();
  static ArrayList<String> ERRORS = new ArrayList<>();

  public static void main(String[] args) {
    checkAllOracleJDKs();
    checkAllJavaNetJDKs();
    checkUnsupportedInputs();

    if (ERRORS.isEmpty()) return;

    System.err.println();
    System.err.printf("// %d error%s detected%n", ERRORS.size(), ERRORS.size() == 1 ? "" : "s");
    ERRORS.forEach(System.err::println);
    System.exit(1);
  }

  static void checkAllOracleJDKs() {
    System.out.println();
    System.out.println("// oracle.com - latest");
    checkOracleJDK("22", "latest");
    checkOracleJDK("21", "latest");
    checkOracleJDK("17", "latest");

    System.out.println();
    System.out.println("// oracle.com - archive");
    Stream.of("23").forEach(version -> checkOracleJDK("23", version));
    Stream.of("22", "22.0.1", "22.0.2").forEach(version -> checkOracleJDK("22", version));
    Stream.of("21", "21.0.1", "21.0.2", "21.0.4").forEach(version -> checkOracleJDK("21", version));
    Stream.of("20", "20.0.1", "20.0.2").forEach(version -> checkOracleJDK("20", version));
    Stream.of("19", "19.0.1", "19.0.2").forEach(version -> checkOracleJDK("19", version));
    Stream.of("18", "18.0.1", "18.0.1.1").forEach(version -> checkOracleJDK("18", version));
    Stream.of("17", "17.0.1", "17.0.2", "17.0.12").forEach(version -> checkOracleJDK("17", version));
  }

  static void checkAllJavaNetJDKs() {
    System.out.println();
    System.out.println("// jdk.java.net - GA - latest");
    checkJavaNetJDK("ga", "latest");

    System.out.println();
    System.out.println("// jdk.java.net - EA - latest");
    checkJavaNetJDK("ea", "latest");
    System.out.println("// jdk.java.net - EA - stable");
    checkJavaNetJDK("ea", "stable");

    System.out.println();
    System.out.println("// jdk.java.net - Project Valhalla - latest");
    checkJavaNetJDK("valhalla", "latest");
  }

  static void checkOracleJDK(String release, String version) {
    checkJDK("oracle.com", new Download.JDK(release, version, "linux", "aarch64", "tar.gz"));
    checkJDK("oracle.com", new Download.JDK(release, version, "linux", "x64", "tar.gz"));
    checkJDK("oracle.com", new Download.JDK(release, version, "macos", "aarch64", "tar.gz"));
    checkJDK("oracle.com", new Download.JDK(release, version, "macos", "x64", "tar.gz"));
    checkJDK("oracle.com", new Download.JDK(release, version, "windows", "x64", "zip"));
  }

  static void checkJavaNetJDK(String release, String version) {
    checkJDK("jdk.java.net", new Download.JDK(release, version, "linux", "x64", "tar.gz"));
    checkJDK("jdk.java.net", new Download.JDK(release, version, "macos", "x64", "tar.gz"));
    checkJDK("jdk.java.net", new Download.JDK(release, version, "windows", "x64", "zip"));
  }

  static void checkJDK(String website, Download.JDK jdk) {
    var uri = Download.Website.find(website).orElseThrow().findUri(jdk).orElseThrow();
    try {
      var head = BROWSER.head(uri);
      if (head.statusCode() < 200 || head.statusCode() >= 400) ERRORS.add(head.toString());
      System.out.println(head);
    } catch (Exception exception) {
      ERRORS.add(jdk + "\n" + exception);
    }
  }

  static void checkUnsupportedInputs() {
    System.out.println();
    System.out.println("// Check unsupported inputs");
    assertThrows(() -> Download.main(true), "Usage:");
    assertThrows(() -> Download.main(true, "website"), "Could not find website for website");
    assertThrows(() -> Download.main(true, "oracle.com", "0"), "Could not find URI of JDK");
  }

  static void assertThrows(Runnable runnable, String snippet) {
    try {
      runnable.run();
    } catch (Throwable throwable) {
      var message = throwable.toString();
      if (message.contains(snippet)) return;
      var format = "Caught %s, but expected snippet '%s' not found in message: %s";
      throw new AssertionError(String.format(format, throwable.getClass(), snippet, message));
    }
    throw new AssertionError("Caught nothing?");
  }
}
