package br.com.coder.arqprime.web.jsf.validators;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("noValidator")
public class NoValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object obj) throws ValidatorException {
		System.out.println(obj);
	}

}
