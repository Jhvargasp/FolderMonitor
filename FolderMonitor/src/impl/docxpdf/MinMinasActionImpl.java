package impl.docxpdf;

import actions.Actions;

public class MinMinasActionImpl implements Actions {
	public void doUpdate(String file) {
	}

	public void doDelete(String file) {
	}

	public void doCreate(String file) {
		if (file.endsWith(".docx")) {
			Convert.convertOfficeToPdf(file);
		}
	}

	public static void main(String[] args) {
	}
}
