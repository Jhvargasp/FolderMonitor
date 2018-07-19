package impl.docxpdf;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

public class CommandRunner {
	private final HashMap<String, Object> hmParameters;

	public CommandRunner(HashMap<String, Object> hmParameters) {
		this.hmParameters = hmParameters;
	}

	public String execute() {
		return execute(this.hmParameters.get(PARAMETER_NAMES_STRING_COMMAND).toString());
	}

	public String execute(String strCommand) {
		StringBuffer sbMsg = new StringBuffer();
		try {
			Runtime rt = (Runtime) this.hmParameters.get(PARAMETER_NAMES_OBJECT_RUNTIME);
			Process pr = rt.exec(strCommand);

			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

			String line = null;
			while ((line = input.readLine()) != null) {
				sbMsg.append(line);
				sbMsg.append(this.hmParameters.get(PARAMETER_NAMES_STRING_LINE_SEPARATOR).toString());
			}
			int exitVal = pr.waitFor();
			sbMsg.append(MESSAGES_EXITED_WITH_ERROR_CODE + exitVal);
			sbMsg.append(this.hmParameters.get(PARAMETER_NAMES_STRING_LINE_SEPARATOR).toString());
		} catch (Exception e) {
			sbMsg.append(e.toString());
			sbMsg.append(this.hmParameters.get(PARAMETER_NAMES_STRING_LINE_SEPARATOR).toString());
			e.printStackTrace();
		}
		return sbMsg.toString();
	}

	public static String PARAMETER_NAMES_OBJECT_RUNTIME = "OBJECT_RUNTIME";
	public static String PARAMETER_NAMES_STRING_COMMAND = "STRING_COMMAND";
	public static String PARAMETER_NAMES_STRING_LINE_SEPARATOR = "STRING_LINE_SEPARATOR";
	public static String PARAMETER_KEYS_OBJECT_RUNTIME = "OBJECT_RUNTIME";
	public static String PARAMETER_KEYS_STRING_COMMAND = "STRING_COMMAND";
	public static String PARAMETER_KEYS_STRING_LINE_SEPARATOR = "STRING_LINE_SEPARATOR";
	public static String MESSAGES_EXITED_WITH_ERROR_CODE = "Exited with error code ";
	public static Object NULL_QUERY = null;
}
