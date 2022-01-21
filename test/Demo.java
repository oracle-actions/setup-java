import java.util.ArrayList;

public class Demo {

  static Download.Browser BROWSER = new Download.Browser();
  static ArrayList<String> ERRORS = new ArrayList<>();

  public static void main(String[] args) throws Exception {
    jdks();

    if (ERRORS.isEmpty()) return;

    System.err.println();
    System.err.printf("// %d error%s detected%n", ERRORS.size(), ERRORS.size() == 1 ? "" : "s");
    ERRORS.forEach(System.err::println);
    System.exit(1);
  }

  static void jdks() throws Exception {
    System.out.println();
    System.out.println("// oracle.com - latest");
    checkJDK("oracle.com", "17", "latest");
    checkJDK("oracle.com", "18", "latest");

    System.out.println();
    System.out.println("// oracle.com - archive");
    checkJDK("oracle.com", "17", "17");
    checkJDK("oracle.com", "17", "17.0.1");
    checkJDK("oracle.com", "17", "17.0.2");
  }

  static void checkJDK(String website, String feature, String version) throws Exception {
    var os = Download.JDK.computeOsName();
    var arch = Download.JDK.computeOsArch();
    var type = Download.JDK.computeFileType();
    var jdk = new Download.JDK(feature, version, os, arch, type);
    var uri = Download.Website.find(website).orElseThrow().findUri(jdk).orElseThrow();
    var head = BROWSER.head(uri);
    if (head.statusCode() < 200 || head.statusCode() >= 400) ERRORS.add(head.toString());
    System.out.println(head);
  }
}
