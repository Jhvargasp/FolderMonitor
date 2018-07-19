package impl.docxpdf;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Convert {
	public static String convertOfficeToPdf(String file) {
		ResourceBundle bundle = ResourceBundle.getBundle("docxpdf");
		String path = new File(file).getAbsolutePath().replaceAll(".docx", ".pdf");
		try {
			System.out.println("New implementation office");
			String jsPath = bundle.getString("routePathJs");
			System.out.println(file);
			HashMap<String, Object> hmParameters = new HashMap();
			hmParameters.put(CommandRunner.PARAMETER_NAMES_OBJECT_RUNTIME, Runtime.getRuntime());
			hmParameters.put(CommandRunner.PARAMETER_NAMES_STRING_LINE_SEPARATOR, "<br />\n");

			hmParameters.put(CommandRunner.PARAMETER_NAMES_STRING_COMMAND, jsPath + " \"" + file + "\"");

			System.out.println(hmParameters);

			CommandRunner cr = new CommandRunner(hmParameters);

			String strMsg = cr.execute();
			path = path.replaceAll(".doc", ".pdf");
			System.out.println(strMsg);
			System.out.println(path);
			new File(file).delete();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return path;
	}

	public static void main(String[] args) {
	}
}
