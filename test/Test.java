import java.util.ArrayList;
import java.util.stream.Stream;

public class Test {

  static Download.Browser BROWSER = new Download.Browser();
  static ArrayList<String> ERRORS = new ArrayList<>();

  public static void main(String[] args) {
    checkAllOracleJDKs();
    checkAllJavaNetJDKs();

    if (ERRORS.isEmpty()) return;

    System.err.println();
    System.err.printf("// %d error%s detected%n", ERRORS.size(), ERRORS.size() == 1 ? "" : "s");
    ERRORS.forEach(System.err::println);
    System.exit(1);
  }

  static void checkAllOracleJDKs() {
    System.out.println();
    System.out.println("// oracle.com - 17 - latest");
    checkOracleJDK17("latest");

    System.out.println();
    System.out.println("// oracle.com - 17 - archive");
    Stream.of("17", "17.0.1", "17.0.2").forEach(Test::checkOracleJDK17);
  }

  static void checkAllJavaNetJDKs() {
    System.out.println();
    System.out.println("// java.net - GA - latest");
    checkJavaNetJDK("ga", "latest");

    System.out.println();
    System.out.println("// java.net - EA - latest");
    checkJavaNetJDK("ea","latest");

    System.out.println();
    System.out.println("// java.net - Project Loom - latest");
    checkJavaNetJDK("loom","latest");

    System.out.println();
    System.out.println("// java.net - Project Panama - latest");
    checkJavaNetJDK("panama","latest");

    System.out.println();
    System.out.println("// java.net - Project Valhalla - latest");
    checkJavaNetJDK("valhalla","latest");
  }

  static void checkOracleJDK17(String version) {
    checkJDK("oracle.com", new Download.JDK("17", version, "linux", "aarch64", "tar.gz"));
    checkJDK("oracle.com", new Download.JDK("17", version, "linux", "x64", "tar.gz"));
    checkJDK("oracle.com", new Download.JDK("17", version, "macos", "aarch64", "tar.gz"));
    checkJDK("oracle.com", new Download.JDK("17", version, "macos", "x64", "tar.gz"));
    checkJDK("oracle.com", new Download.JDK("17", version, "windows", "x64", "zip"));
  }

  static void checkJavaNetJDK(String feature, String version) {
    checkJDK("java.net", new Download.JDK(feature, version, "linux", "x64", "tar.gz"));
    checkJDK("java.net", new Download.JDK(feature, version, "linux", "x64", "tar.gz"));
    checkJDK("java.net", new Download.JDK(feature, version, "linux", "x64", "tar.gz"));
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
}
