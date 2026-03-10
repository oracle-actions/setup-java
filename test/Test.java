/*
 * Copyright (c) 2022, 2026, Oracle and/or its affiliates.
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
    checkOracleJDK("26", "latest");
    checkOracleJDK("25", "latest");
    checkOracleJDK("21", "latest");

    System.out.println();
    System.out.println("// oracle.com - archive");
    Stream.of("25", "25.0.1", "25.0.2").forEach(version -> checkOracleJDK("25", version));
    Stream.of("21", "21.0.1", "21.0.2", "21.0.10").forEach(version -> checkOracleJDK("21", version));
    /*
     * The Oracle Technology Network License Agreement for Oracle Java SE used for
     * JDK 17 updates 17.0.13 and greater is substantially different from prior
     * Oracle JDK 17 licenses.
     */
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
    System.out.println("// jdk.java.net - Project JExtract - latest");
    checkJavaNetJDK("jextract", "latest");

    System.out.println();
    System.out.println("// jdk.java.net - Project Leyden - latest");
    // checkJavaNetJDK("leyden", "latest"); // only the following binaries are available
    checkJDK("jdk.java.net", new Download.JDK("leyden", "latest", "linux", "aarch64", "tar.gz"));
    checkJDK("jdk.java.net", new Download.JDK("leyden", "latest", "linux", "x64", "tar.gz"));
    checkJDK("jdk.java.net", new Download.JDK("leyden", "latest", "macos", "aarch64", "tar.gz"));

    System.out.println();
    System.out.println("// jdk.java.net - Project Loom - latest");
    checkJavaNetJDK("loom", "latest");

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
    checkJDK("jdk.java.net", new Download.JDK(release, version, "linux", "aarch64", "tar.gz"));
    checkJDK("jdk.java.net", new Download.JDK(release, version, "linux", "x64", "tar.gz"));
    checkJDK("jdk.java.net", new Download.JDK(release, version, "macos", "aarch64", "tar.gz"));
    checkJDK("jdk.java.net", new Download.JDK(release, version, "macos", "x64", "tar.gz"));
    checkJDK("jdk.java.net", new Download.JDK(release, version, "windows", "x64", "zip"));
  }

  static void checkJDK(String website, Download.JDK jdk) {
    System.out.println(website + " << " + jdk);
    var finder = Download.Website.find(website).orElseThrow();
    var uri = finder.findUri(jdk).orElseThrow();
    try {
      var head = BROWSER.head(uri);
      if (head.statusCode() < 200 || head.statusCode() >= 400) ERRORS.add(head.toString());
      System.out.println(head);
      var sha = BROWSER.head(uri + ".sha256");
      System.out.println(sha);
      if (sha.statusCode() >= 200 && sha.statusCode() < 400) {
          System.out.println("  --> " + BROWSER.browse(uri + ".sha256"));
      } else {
          System.out.println("  --> <?>");
      }
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
