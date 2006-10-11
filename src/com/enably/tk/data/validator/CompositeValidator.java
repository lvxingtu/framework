/* *************************************************************************
 
   								Millstone(TM) 
   				   Open Sourced User Interface Library for
   		 		       Internet Development with Java

             Millstone is a registered trademark of IT Mill Ltd
                  Copyright (C) 2000-2005 IT Mill Ltd
                     
   *************************************************************************

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   license version 2.1 as published by the Free Software Foundation.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:  +358 2 4802 7181
   20540, Turku                          email: info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for MillStone information and releases: www.millstone.org

   ********************************************************************** */
   
package com.enably.tk.data.validator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import com.enably.tk.data.*;

/** Composite validator.
 * 
 * This validator allows you to chain (compose) many validators
 * to validate one field. The contained validators may be required
 * to all validate the value to validate or it may be enough that
 * one contained validator validates the value. This behaviour is
 * controlled by the modes AND and OR.
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class CompositeValidator implements Validator {

	/** The validators are combined with AND clause: validity of the
	 * composite implies validity of the all validators it is composed of
	 * must be valid.
	 */
	public static final int MODE_AND = 0;

	/** The validators are combined with OR clause: validity of the
	 * composite implies that some of validators it is composed of
	 * must be valid.
	 */
	public static final int MODE_OR = 1;

	/** The validators are combined with and clause: validity of the
	 * composite implies validity of the all validators it is composed of
	 */
	public static final int MODE_DEFAULT = MODE_AND;

	/** Operation mode */
	private int mode = MODE_DEFAULT;

	/** List of contained validators */
	private LinkedList validators = new LinkedList();

	/** Error message */
	private String errorMessage;

	/** Construct composite validator in AND mode without error message */
	public CompositeValidator() {
	}

	/** Construct composite validator in given mode */
	public CompositeValidator(int mode, String errorMessage) {
		setMode(mode);
		setErrorMessage(errorMessage);
	}

	/** Validate the the given value.
	 * The value is valid, if:
	 * <ul>
	 * <li><code>MODE_AND</code>: All of the sub-validators are valid
	 * <li><code>MODE_OR</code>: Any of the sub-validators are valid
	 * </ul>
	 * 
	 * If the value is invalid, validation error is thrown. If the
	 * error message is set (non-null), it is used. If the error message
	 * has not been set, the first error occurred is thrown.
	 */
	public void validate(Object value) throws Validator.InvalidValueException {
		switch (mode) {
			case MODE_AND :
				for (Iterator i = validators.iterator(); i.hasNext();) 
					((Validator) i.next()).validate(value);
				return;

			case MODE_OR :
				Validator.InvalidValueException first = null;
				for (Iterator i = validators.iterator(); i.hasNext();) 
					try {
						((Validator) i.next()).validate(value);
						return;
					} catch (Validator.InvalidValueException e) {
						if	(first == null) first = e;
					}
				if (first == null) return;
				String em = getErrorMessage();
				if (em != null) throw new Validator.InvalidValueException(em);
				else throw first;
		}
		throw new IllegalStateException("The valitor is in unsupported operation mode");
	}

	/** Check the validity of the the given value.
	 * The value is valid, if:
	 * <ul>
	 * <li><code>MODE_AND</code>: All of the sub-validators are valid
	 * <li><code>MODE_OR</code>: Any of the sub-validators are valid
	 * </ul>
	 */
	public boolean isValid(Object value) {
		switch (mode) {
			case MODE_AND :
				for (Iterator i = validators.iterator(); i.hasNext();) {
					Validator v = (Validator) i.next();
					if (!v.isValid(value))
						return false;
				}
				return true;

			case MODE_OR :
				for (Iterator i = validators.iterator(); i.hasNext();) {
					Validator v = (Validator) i.next();
					if (v.isValid(value))
						return true;
				}
				return false;
		}
		throw new IllegalStateException("The valitor is in unsupported operation mode");
	}

	/** Get the mode of the validator.
	 * @return Operation mode of the validator: 
	 * <code>MODE_AND</code> or <code>MODE_OR</code>.
	 */
	public final int getMode() {
		return mode;
	}

	/** Set the mode of the validator. The valid modes are:
	 * <ul>
	 * <li><code>MODE_AND</code>  (default)
	 * <li><code>MODE_OR</code>
	 * </ul>
	 */
	public void setMode(int mode) {
		if (mode != MODE_AND && mode != MODE_OR)
			throw new IllegalArgumentException("Mode " + mode + " unsupported");
		this.mode = mode;
	}

	/** Get the error message for the composite validator. 
	 * If the error message is null, original error messages of the 
	 * sub-validators are used instead.
	 */
	public String getErrorMessage() {
		return null;
	}

	/** Set the error message for the composite validator. 
	 * If the error message is null, original error messages of the 
	 * sub-validators are used instead.
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/** Add validator to the interface */
	public void addValidator(Validator validator) {
		if (validator == null)
			return;
		validators.add(validator);
	}

	/** Remove a validator from the composite */
	public void removeValidator(Validator validator) {
		validators.remove(validator);
	}

	/** Get sub-validators by class. 
	 * 
	 * <p>If the component contains
	 * directly or recursively (it contains another composite 
	 * containing the validator) validators compatible with given type they
	 * are returned. This only applies to AND mode composite 
	 * validators.</p>
	 * 
	 * <p>If the validator is in OR mode or does not contain any
	 * validators of given type null is returned. </p>
	 * 
	 * @return Collection of validators compatible with given type that 
	 * must apply or null if none fould.
	 */
	public Collection getSubValidators(Class validatorType) {
		if (mode != MODE_AND)
			return null;

		HashSet found = new HashSet();
		for (Iterator i = validators.iterator(); i.hasNext();) {
			Validator v = (Validator) i.next();
			if (validatorType.isAssignableFrom(v.getClass()))
				found.add(v);
			if (v instanceof CompositeValidator
				&& ((CompositeValidator) v).getMode() == MODE_AND) {
				Collection c =
					((CompositeValidator) v).getSubValidators(validatorType);
				if (c != null)
					found.addAll(c);
			}
		}

		return found.isEmpty() ? null : found;
	}
	
}
