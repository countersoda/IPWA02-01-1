package beans;

import java.io.Serializable;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import model.Emission;

@Named("emission")
@ViewScoped
public class EmissionBean extends Emission implements Serializable {

	private static final long serialVersionUID = 1L;
}