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

import com.enably.tk.data.*;


/* This validator is used for validating properties that 
 * do or do not allow null values.
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class NullValidator implements Validator {

	private boolean allowNull;
	private String errorMessage;

	/** Create a new NullValidator
	 * @param errorMessage - The error message to display on invalidation.
	 * @param allowNull - Are nulls allowed?
	 */
	public NullValidator(String errorMessage,boolean allowNull) {
		setErrorMessage(errorMessage);
		setNullAllowed(allowNull);
	}

	/** Validate the data given in value.
	 * @param value - The value to validate.
	 * @throws Validator.InvalidValueException - The value was invalid.
	 */
	public void validate(Object value) throws Validator.InvalidValueException {
		if ((allowNull && value != null) || (!allowNull && value == null))
			throw new Validator.InvalidValueException(errorMessage);
	}

	/** True of the value is valid.
	 * @param value - The value to validate.
	 */
	public boolean isValid(Object value) {
		return allowNull ? value == null : value != null;
	}

	/** True if nulls are allowed.
	 */
	public final boolean isNullAllowed() {
		return allowNull;
	}

	/** Sets if nulls are to be allowed.
	 * @param allowNull - Do we allow nulls?
	 */
	public void setNullAllowed(boolean allowNull) {
		this.allowNull = allowNull;
	}
	
	/** Get the error message that is displayed in case the
	 * value is invalid.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/** Set the error message to be displayed on invalid
	 * value.
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
