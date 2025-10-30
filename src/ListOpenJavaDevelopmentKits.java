/*
 * Copyright (c) 2022, 2025, Oracle and/or its affiliates.
 *
 * This source code is licensed under the UPL license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
 *   <li>{@code VERSION}: Either a specific version or `latest` or `stable`
 *   <li>{@code OS-NAME}: An operating system name, usually one of: `linux`, `macos`, `windows`
 *   <li>{@code OS-ARCH}: An operating system architecture, like: `aarch64`, `x64`, or `x64-musl`
 * </ul>
 */
class ListOpenJavaDevelopmentKits {
  /** List of pages to visit and parse for JDK archives. */
  static final List<Page> PAGES =
      List.of(
          // JDK: General-Availability Release
          Page.of("25") // https://jdk.java.net/25
              .withAlias("25,latest")
              .withAlias("ga,latest"),
          // JDK: Early-Access Releases
          Page.of("26") // https://jdk.java.net/26
              .withAlias("26,latest")
              .withAlias("ea,latest")
              .withAlias("ea,stable"),
          // Named projects, usually in EA phase
          Page.of("jextract") // https://jdk.java.net/jextract
              .withAlias("jextract,latest")
              .withAlias("jextract,ea"),
          Page.of("loom") // https://jdk.java.net/loom
              .withAlias("loom,latest")
              .withAlias("loom,ea"),
          Page.of("leyden") // https://jdk.java.net/leyden
              .withAlias("leyden,latest")
              .withAlias("leyden,ea"),
          Page.of("valhalla") // https://jdk.java.net/valhalla
              .withAlias("valhalla,latest")
              .withAlias("valhalla,ea"));

  /** Regex-based pattern used to find a download URI in an HTML code line. */
  static final Pattern URI_PATTERN =
      Pattern.compile(
          ".+?href=\""
              + "(\\Qhttps://download.java.net/\\E.+?/openjdk-.+?_bin\\.(tar\\.gz|zip))"
              + "\".+?");

  /** Regex-based pattern used to find all named groups of a download key. */
  static final Pattern KEY_PATTERN =
      Pattern.compile(".+?openjdk-(?<version>.+?)_(?<os>.+?)-(?<arch>.+?)_bin\\.(?<type>.+?)");

  public static void main(String... args) {
    var pages = args.length == 1 ? List.of(Page.of(args[0])) : PAGES;
    var parser = new Parser();
    for (var page : pages) {
      var section = parser.parse(page);
      System.out.println("#");
      System.out.println("# " + page.address());
      System.out.println("#");
      section.map().forEach((key, uri) -> System.out.printf("%s=%s%n", key, uri));
    }
  }

  record Page(String name, List<String> aliases) {
    static Page of(String name) {
      return new Page(name, List.of());
    }

    Page withAlias(String alias) {

      return new Page(name, Stream.concat(aliases.stream(), Stream.of(alias)).toList());
    }

    String address() {
      return "https://jdk.java.net/" + name.toLowerCase() + "/";
    }
  }

  record Section(Page page, Map<String, String> map) {}

  record Parser(HttpClient http) {
    Parser() {
      this(HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build());
    }

    String browse(String uri) {
      try {
        var request = HttpRequest.newBuilder(URI.create(uri)).build();
        return http.send(request, HttpResponse.BodyHandlers.ofString()).body();
      } catch (Exception exception) {
        return exception.toString();
      }
    }

    Section parse(Page page) {
      var map = new TreeMap<String, String>();
      var html = browse(page.address());
      for (var line : html.lines().toList()) {
        var uriMatcher = URI_PATTERN.matcher(line);
        if (!uriMatcher.matches()) {
          continue;
        }
        var uri = uriMatcher.group(1);
        var keyMatcher = KEY_PATTERN.matcher(uri);
        if (!keyMatcher.matches()) {
          System.err.println("// no match -> " + uri);
          continue;
        }
        var version = Runtime.Version.parse(keyMatcher.group("version"));
        var os = keyMatcher.group("os");
        var arch = keyMatcher.group("arch");
        var platform = os.equals("osx") ? "macos" : os + "," + arch;
        map.put(version.feature() + "," + version + "," + platform, uri);
        for (var alias : page.aliases()) {
          map.put(alias + "," + platform, uri);
        }
      }
      return new Section(page, map);
    }
  }
}
