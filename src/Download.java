import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.regex.Pattern;

/** Download a JDK build. */
public class Download {
  /** Main entry-point. */
  public static void main(String... args) {
    // Pre-allocate action outputs
    var outputs = new TreeMap<String, String>();
    outputs.put("archive", "NOT-SET");
    outputs.put("version", "NOT-SET");
    try {
      if (args.length == 0) {
        throw new IllegalArgumentException("Usage: Download URI or WEBSITE FEATURE VERSION");
      }
      var deque = new ArrayDeque<>(List.of(args));
      var first = deque.removeFirst(); // URI or WEBSITE

      // Determine website from first argument
      var website = Website.find(first).orElseGet(Website::defaultWebsite);
      GitHub.debug("website: " + website);

      // Create JDK descriptor
      var jdk =
          new JDK(
              deque.isEmpty() ? "ga" : deque.removeFirst().toLowerCase(),
              deque.isEmpty() ? "latest" : deque.removeFirst().toLowerCase(),
              deque.isEmpty() ? JDK.computeOsName() : deque.removeFirst(),
              deque.isEmpty() ? JDK.computeOsArch() : deque.removeFirst(),
              deque.isEmpty() ? JDK.computeFileType() : deque.removeFirst());
      GitHub.debug("jdk: " + jdk);

      // Select or find URI based on the JDK descriptor
      var uri = args.length == 1 ? first : website.findUri(jdk).orElseThrow();
      GitHub.debug("uri: " + uri);
      if (!(uri.endsWith(".tar.gz") || uri.endsWith(".zip"))) {
        throw new IllegalArgumentException("URI must end with `.tar.gz` or `.zip`: " + uri);
      }

      // Emit warning when using an archived JDK build
      if (website.isArchivedUri(uri)) {
        GitHub.warn(
            "JDK resolved to an archived build!\n"
                + "These older versions of the JDK are provided\n"
                + " to help developers debug issues in older systems.\n"
                + "They are not updated with the latest security patches\n"
                + " and are not recommended for use in production.");
      }

      // Acquire JDK archive
      var archive = website.computeArchivePath(uri);
      GitHub.debug("archive: " + archive);
      var downloader = new Downloader(archive, uri);
      if (website.isMovingResourceUri(uri)) {
        downloader.checkSizeAndDeleteIfDifferent();
      }
      downloader.downloadArchive(Boolean.getBoolean(/*-D*/ "ry-run"));
      downloader.verifyChecksums(website.getChecksum(uri));
      System.out.printf("Archive %s in %s%n", archive.getFileName(), archive.getParent().toUri());

      // Set outputs
      outputs.put("archive", archive.toString());
      outputs.put("version", website.parseVersion(uri).orElse("UNKNOWN-VERSION"));
    } catch (Exception exception) {
      exception.printStackTrace(System.err);
      GitHub.error("Error detected: " + exception);
    } finally {
      outputs.forEach(GitHub::setOutput);
    }
  }

  static class JDK {

    final String feature;
    final String version;
    final String os;
    final String arch;
    final String type;

    JDK(String feature, String version, String os, String arch, String type) {
      this.feature = feature;
      this.version = version;
      this.os = os;
      this.arch = arch;
      this.type = type;
    }

    @Override
    public String toString() {
      return String.format(
          "JDK{feature='%s', version='%s', os='%s', arch='%s', type='%s'}",
          feature, version, os, arch, type);
    }

    static String computeOsName() {
      var name = System.getProperty("os.name").toLowerCase();
      if (name.contains("win")) return "windows";
      if (name.contains("mac")) return "macos";
      return "linux";
    }

    static String computeOsArch() {
      var arch = System.getProperty("os.arch", "x64");
      if (arch.equals("amd64")) return "x64";
      if (arch.equals("x86_64")) return "x64";
      return arch;
    }

    static String computeFileType() {
      var name = System.getProperty("os.name").toLowerCase();
      return name.contains("win") ? "zip" : "tar.gz";
    }
  }

  /** Download helper. */
  static class Downloader {

    final Path archive;
    final String uri;
    final Browser browser;

    Downloader(Path archive, String uri) {
      this.archive = archive;
      this.uri = uri;
      this.browser = new Browser();
    }

    void checkSizeAndDeleteIfDifferent() throws Exception {
      if (Files.notExists(archive)) return;
      var cachedSize = Files.size(archive);
      GitHub.debug("Cached size: " + cachedSize);
      var remoteSize = browser.head(uri).headers().firstValueAsLong("content-length").orElse(-1);
      GitHub.debug("Remote size: " + remoteSize);
      if (cachedSize == remoteSize) return;
      Files.delete(archive);
    }

    void downloadArchive(boolean dryRun) throws Exception {
      if (Files.exists(archive)) return;
      var head = browser.head(uri);
      GitHub.debug(head.toString());
      if (dryRun) {
        return;
      }
      var response = browser.download(uri, archive);
      GitHub.debug(response.toString());
    }

    void verifyChecksums(String checksum) throws Exception {
      if (Files.notExists(archive)) return;
      var cached = computeChecksum(archive);
      GitHub.debug("Cached checksum: " + cached);

      var remoteChecksum = findRemoteChecksum(checksum);
      if (remoteChecksum.isEmpty()) {
        GitHub.warn("Checksum not available for: " + uri);
        return;
      }

      var remote = remoteChecksum.get();
      GitHub.debug("Remote checksum: " + remote);
      if (cached.equals(remote)) return;
      var message = "Checksum verification failed, deleting cached archive";
      Files.delete(archive);
      GitHub.error(message);
      throw new AssertionError(message);
    }

    String computeChecksum(Path path) {
      try {
        var md = MessageDigest.getInstance("SHA-256");
        try (var input = new BufferedInputStream(new FileInputStream(path.toFile()));
            var output = new DigestOutputStream(OutputStream.nullOutputStream(), md)) {
          input.transferTo(output);
        }
        var length = md.getDigestLength() * 2;
        return String.format("%0" + length + "x", new BigInteger(1, md.digest()));
      } catch (IOException exception) {
        throw new UncheckedIOException(exception);
      } catch (NoSuchAlgorithmException exception) {
        var algorithms = Security.getAlgorithms("MessageDigest");
        throw new IllegalArgumentException(exception + ": " + algorithms);
      }
    }

    Optional<String> findRemoteChecksum(String checksum) throws Exception {
      if (!checksum.startsWith("https://")) return Optional.of(checksum);
      if (browser.head(checksum).statusCode() == 200) return Optional.of(browser.browse(checksum));
      return Optional.empty();
    }
  }

  /**
   * GitHub Actions helper.
   *
   * @see <a
   *     href="https://docs.github.com/en/actions/learn-github-actions/workflow-commands-for-github-actions">Workflow
   *     commands for GitHub Actions</a>
   */
  static class GitHub {
    /** Sets an action's output parameter. */
    static void setOutput(String name, Object value) {
      System.out.printf("::set-output name=%s::%s%n", name, value);
    }

    /** Creates a debug message and prints the message to the log. */
    static void debug(String message) {
      System.out.printf("::debug::%s%n", message.replaceAll("\\R", "%0A"));
    }

    /** Creates a warning message and prints the message to the log. */
    static void warn(String message) {
      System.out.printf("::warning::%s%n", message.replaceAll("\\R", "%0A"));
    }

    /** Creates an error message and prints the message to the log. */
    static void error(String message) {
      System.out.printf("::error::%s%n", message.replaceAll("\\R", "%0A"));
    }
  }

  /** HTTP-related helper. */
  static class Browser {
    final HttpClient client;

    Browser() {
      this.client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
    }

    String browse(String uri) throws Exception {
      var request = HttpRequest.newBuilder(URI.create(uri)).build();
      return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    HttpResponse<Path> download(String uri, Path file) throws Exception {
      var parent = file.getParent();
      if (parent != null) Files.createDirectories(parent);
      var request = HttpRequest.newBuilder(URI.create(uri)).build();
      return client.send(request, HttpResponse.BodyHandlers.ofFile(file));
    }

    HttpResponse<?> head(String uri) throws Exception {
      var request =
          HttpRequest.newBuilder(URI.create(uri))
              .method("HEAD", HttpRequest.BodyPublishers.noBody())
              .build();
      return client.send(request, HttpResponse.BodyHandlers.discarding());
    }
  }

  /** A website hosting JDK builds. */
  interface Website {

    /** Try to instantiate a website implementation for the given hint. */
    static Optional<Website> find(String hint) {
      if (hint.equals(OracleComWebsite.NAME) || hint.startsWith(OracleComWebsite.URI_PREFIX)) {
        return Optional.of(new OracleComWebsite());
      }
      if (hint.equals(JavaNetWebsite.NAME) || hint.startsWith(JavaNetWebsite.URI_PREFIX)) {
        return Optional.of(new JavaNetWebsite());
      }
      return Optional.empty();
    }

    static Website defaultWebsite() {
      return new OracleComWebsite();
    }

    Optional<String> findUri(JDK jdk);

    default Path computeArchivePath(String uri) {
      var file = uri.substring(uri.lastIndexOf('/') + 1);
      var home = System.getProperty("user.home");
      var hash = Integer.toHexString(uri.hashCode());
      var cache = Path.of(home, ".oracle-actions", "setup-java", hash);
      return cache.resolve(file);
    }

    /** Try to parse version information from the given uri. */
    default Optional<String> parseVersion(String uri) {
      for (var versionPattern : parseVersionPatterns()) {
        var matcher = versionPattern.matcher(uri);
        if (matcher.matches()) return Optional.of(matcher.group(1));
      }
      return Optional.empty();
    }

    /** A list of patterns with each has at least one version-defining capture group. */
    default List<Pattern> parseVersionPatterns() {
      return List.of();
    }

    /** Test the given uri for potentially pointing to different resources over time. */
    default boolean isMovingResourceUri(String uri) {
      return false;
    }

    /** Test the given uri for pointing to an archived JDK build. */
    default boolean isArchivedUri(String uri) {
      return false;
    }

    /** The checksum for the given uri, possibly a uri pointing to a remote file. */
    default String getChecksum(String uri) {
      return uri + ".sha256";
    }
  }

  /** JDK builds hosted at {@code https://oracle.com}. */
  static class OracleComWebsite implements Website {

    static String NAME = "oracle.com";
    static String URI_PREFIX = "https://download.oracle.com/java/";

    @Override
    public List<Pattern> parseVersionPatterns() {
      return List.of(Pattern.compile("\\Q" + URI_PREFIX + "\\E.+?/jdk-([\\d.]+).+"));
    }

    @Override
    public boolean isMovingResourceUri(String uri) {
      return uri.contains("/latest/");
    }

    @Override
    public boolean isArchivedUri(String uri) {
      return uri.contains("/archive/");
    }

    @Override
    public Optional<String> findUri(JDK jdk) {
      if (Integer.parseInt(jdk.feature) < 17) return Optional.empty();
      if (jdk.version.equals("latest")) return Optional.of(computeLatestUri(jdk));
      return Optional.of(computeArchiveUri(jdk));
    }

    String computeLatestUri(JDK jdk) {
      var format = URI_PREFIX + "%s/latest/jdk-%s_%s-%s_bin.%s";
      return String.format(format, jdk.feature, jdk.feature, jdk.os, jdk.arch, jdk.type);
    }

    String computeArchiveUri(JDK jdk) {
      var format = URI_PREFIX + "%s/archive/jdk-%s_%s-%s_bin.%s";
      return String.format(format, jdk.feature, jdk.version, jdk.os, jdk.arch, jdk.type);
    }
  }

  /** JDK builds hosted at {@code https://jdk.java.net}. */
  static class JavaNetWebsite implements Website {
    static String NAME = "java.net";
    static String URI_PREFIX = "https://download.java.net";

    @Override
    public List<Pattern> parseVersionPatterns() {
      return List.of(Pattern.compile("\\Q" + URI_PREFIX + "\\E.+?/openjdk-([\\d.]+).+"));
    }

    @Override
    public Optional<String> findUri(JDK jdk) {
      var key =
          new StringJoiner(",")
              .add(jdk.feature)
              .add(jdk.version)
              .add(jdk.os)
              .add(jdk.arch)
              .toString();
      try {
        var defaultProperties = new Properties();
        var file = Path.of("jdk.java.net-uri.properties");
        defaultProperties.load(new StringReader(Files.readString(file)));
        var properties = new Properties(defaultProperties);
        var browser = new Browser();
        var s =
            browser.browse(
                "https://raw.githubusercontent.com"
                    + "/oracle-actions/setup-java/main" // user/repo/branch
                    + "/"
                    + file);
        properties.load(new StringReader(s));
        return Optional.ofNullable(properties.getProperty(key));
      } catch (Exception exception) {
        return Optional.empty();
      }
    }
  }
}
