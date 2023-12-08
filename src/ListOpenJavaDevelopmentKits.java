/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * This source code is licensed under the UPL license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * List Java Development Kit builds hosted at: {@code https://jdk.java.net}.
 *
 * <p>Example output:
 *
 * <pre>{@code
 * 17,17.0.2,linux,x64=https://[...]/GPL/openjdk-17.0.2_linux-x64_bin.tar.gz
 * 17,latest,linux,x64=https://[...]/GPL/openjdk-17.0.2_linux-x64_bin.tar.gz
 * ga,latest,linux,x64=https://[...]/GPL/openjdk-17.0.2_linux-x64_bin.tar.gz
 * }</pre>
 *
 * Keys are composed of {@code RELEASE,VERSION,OS-NAME,OS-ARCH} with:
 *
 * <ul>
 *   <li>{@code RELEASE}: Either a release number or a name of an early-access project
 *   <li>{@code VERSION}: Either a specific version or `latest`
 *   <li>{@code OS-NAME}: An operating system name, usually one of: `linux`, `macos`, `windows`
 *   <li>{@code OS-ARCH}: An operating system architecture, like: `aarch64`, `x64`, or `x64-musl`
 * </ul>
 */
class ListOpenJavaDevelopmentKits {

  /** Current General-Availability release number. */
  static final String GA = System.getProperty("GA", "21");

  /** Current Soon-Archived release number. */
  static final String SA = System.getProperty("SA", "20");

  /** Early-Access Releases, as comma separated names. */
  static final String EA = System.getProperty("EA", "23,22,jextract,valhalla");

  /** Include archived releases flag. */
  static final boolean ARCHIVES = Boolean.getBoolean("ARCHIVES");

  /** Shared HTTP client instance. */
  static final HttpClient HTTP_CLIENT =
      HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();

  /** Regex-based pattern used to find a download URI in a HTML code line. */
  static final Pattern URI_PATTERN =
      Pattern.compile(
          ".+?href=\""
              + "(\\Qhttps://download.java.net/\\E.+?/openjdk-.+?_bin\\.(tar\\.gz|zip))"
              + "\".+?");

  /** Regex-based pattern used to find all named groups of a download key. */
  static final Pattern KEY_PATTERN =
      Pattern.compile(".+?openjdk-(?<version>.+?)_(?<os>.+?)-(?<arch>.+?)_bin\\.(?<type>.+?)");

  /** Main entry-point. */
  public static void main(String... args) {
    if (args.length == 0) {
      listGeneralAvailabilityRelease();
      listSoonArchivedRelease();
      listEarlyAccessReleases();
      if (ARCHIVES) listArchivedReleases();
    } else {
      for (var name : args) {
        var page = "https://jdk.java.net/" + name.toLowerCase() + "/";
        var html = browse(page);
        print(page, parse(html));
      }
    }
  }

  static void listGeneralAvailabilityRelease() {
    var html = browse("https://jdk.java.net/" + GA + "/");
    var directs = parse(html);
    print("General-Availability Release", directs);

    var aliases = alias(directs, ListOpenJavaDevelopmentKits::generateGeneralAvailabilityAliasKeys);
    print("General-Availability Release (Alias)", aliases);
  }

  static List<String> generateGeneralAvailabilityAliasKeys(String[] components) {
    components[1] = "latest";
    var alias1 = String.join(",", components);
    components[0] = "ga";
    var alias2 = String.join(",", components);
    return List.of(alias1, alias2);
  }

  static void listSoonArchivedRelease() {
    var html = browse("https://jdk.java.net/" + SA + "/");
    var directs = parse(html);
    print("Soon-Archived Release", directs);

    var aliases = alias(directs, ListOpenJavaDevelopmentKits::generateSoonArchivedAliasKeys);
    print("Soon-Archived Release (Alias)", aliases);
  }

  static List<String> generateSoonArchivedAliasKeys(String[] components) {
    components[1] = "latest";
    var alias1 = String.join(",", components);
    return List.of(alias1);
  }

  static void listEarlyAccessReleases() {
    var names = List.of(EA.split(","));
    if (names.isEmpty()) return;
    var html =
        names.stream()
            .map(String::toLowerCase)
            .map(name -> browse("https://jdk.java.net/" + name + "/"))
            .collect(Collectors.joining());
    var directs = parse(html);
    print("Early-Access Releases", directs);

    var aliases = alias(directs, ListOpenJavaDevelopmentKits::generateEarlyAccessAliasKeys);
    print("Early-Access Releases (Alias)", aliases);
  }

  static List<String> generateEarlyAccessAliasKeys(String[] components) {
    var release = components[0];
    var version = components[1];
    try {
      // extract named project or take version as-is
      var from = version.indexOf('-');
      var till = version.indexOf('+');
      var project = from >= 0 && from < till ? version.substring(from + 1, till) : version;
      components[0] = project;
      components[1] = "latest";
      var alias = String.join(",", components);
      if (!project.equals("ea")) return List.of(alias);
      components[0] = release; // 18-latest-...
      return List.of(alias, String.join(",", components));
    } catch (IndexOutOfBoundsException exception) {
      System.err.println("Early-Access version without `-` and `+`: " + version);
      return List.of();
    }
  }

  static void listArchivedReleases() {
    var html = browse("https://jdk.java.net/archive/");
    var directs = parse(html);
    print("Archived Releases", directs);

    var aliases = alias(directs, ListOpenJavaDevelopmentKits::generateArchivedAliasKeys);
    print("Archived Releases (Alias)", aliases);
  }

  static List<String> generateArchivedAliasKeys(String[] components) {
    if (components[0].equals(GA)) return List.of(); // "latest" is covered by GA
    if (components[0].equals(SA)) return List.of(); // "latest" is covered by SA
    components[1] = "latest";
    var alias = String.join(",", components);
    return List.of(alias);
  }

  static String browse(String uri) {
    try {
      var request = HttpRequest.newBuilder(URI.create(uri)).build();
      return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString()).body();
    } catch (Exception exception) {
      return exception.toString();
    }
  }

  static TreeMap<String, String> parse(String html) {
    var map = new TreeMap<String, String>();

    for (var line : html.lines().toList()) {
      var uriMatcher = URI_PATTERN.matcher(line);
      if (uriMatcher.matches()) {
        var uri = uriMatcher.group(1);
        var keyMatcher = KEY_PATTERN.matcher(uri);
        if (!keyMatcher.matches()) {
          System.err.println("// no match -> " + uri);
          continue;
        }
        var version = Runtime.Version.parse(keyMatcher.group("version"));
        var os = keyMatcher.group("os");
        var joiner =
            new StringJoiner(",")
                .add(Integer.toString(version.feature()))
                .add(version.toString())
                .add(os.equals("osx") ? "macos" : os)
                .add(keyMatcher.group("arch"));
        var key = joiner.toString();
        map.put(key, uri);
      }
    }
    return map;
  }

  static TreeMap<String, String> alias(
      TreeMap<String, String> map, Function<String[], List<String>> generator) {
    var alternates = new TreeMap<String, String>();
    for (var ga : map.entrySet()) {
      var key = ga.getKey();
      var uri = ga.getValue();
      var elements = key.split(",");
      generator.apply(elements).forEach(alias -> alternates.put(alias, uri));
    }
    return alternates;
  }

  static void print(String title, TreeMap<String, String> map) {
    System.out.println("#");
    System.out.println("# " + title);
    System.out.println("#");
    map.forEach((key, uri) -> System.out.printf("%s=%s%n", key, uri));
  }
}
