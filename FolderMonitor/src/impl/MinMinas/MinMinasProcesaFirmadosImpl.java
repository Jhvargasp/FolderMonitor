package impl.MinMinas;

import actions.Actions;

public class MinMinasProcesaFirmadosImpl implements Actions {
	public void doCreate(String file) {
		if (file.endsWith(".pdf")) {
			FileNet.subirAvanzar(file);
		}
	}

	public void doDelete(String file) {
	}

	public void doUpdate(String file) {
	}
}
