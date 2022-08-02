/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * This source code is licensed under the UPL license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import java.util.stream.Stream;

class Validate {

  public static void main(String... args) throws Exception {
    var validate = new Validate();
    var paths = args.length == 0 ? findProperties() : Stream.of(args).map(Path::of).toList();
    for (var path : paths) validate.validateProperties(path);
  }

  static List<Path> findProperties() throws Exception {
    var syntaxAndPattern = "glob:*.properties";
    System.out.println(syntaxAndPattern);
    var directory = Path.of("");
    var matcher = directory.getFileSystem().getPathMatcher(syntaxAndPattern);
    try (var stream = Files.find(directory, 9, (path, attributes) -> matcher.matches(path))) {
      return stream.toList();
    }
  }

  final HttpClient http = HttpClient.newBuilder().followRedirects(Redirect.NORMAL).build();

  void validateProperties(Path path) throws Exception {
    if (Files.notExists(path)) throw new IllegalArgumentException("no such file: " + path);
    System.out.println();
    System.out.println(path);
    var properties = new Properties();
    properties.load(Files.newBufferedReader(path));
    for (var key : new TreeSet<>(properties.stringPropertyNames())) {
      var value = properties.getProperty(key);
      validateProperty(key, value);
    }
  }

  private void validateProperty(String key, String value) throws Exception {
    if (value.startsWith("http")) {
      var uri = URI.create(value);
      var request = HttpRequest.newBuilder(uri).method("HEAD", BodyPublishers.noBody()).build();
      var response = http.send(request, BodyHandlers.discarding());
      if (response.statusCode() == 200) {
        var size = response.headers().firstValueAsLong("Content-Length").orElse(-1);
        System.out.printf("%s%n", key);
        System.out.printf("  uri  = %s%n", uri);
        System.out.printf("  size = %,11d bytes%n", size);
      } else {
        response
            .headers()
            .map()
            .forEach((header, entry) -> System.err.printf("%s -> %s%n", header, entry));
        throw new AssertionError("Status for '%s' not OK: %s".formatted(key, response));
      }
      return;
    }
    System.err.printf("Unknown property protocol %s=%s%n", key, value);
  }
}
