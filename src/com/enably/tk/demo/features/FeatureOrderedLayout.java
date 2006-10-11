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

package com.enably.tk.demo.features;

import com.enably.tk.ui.*;

public class FeatureOrderedLayout extends Feature {

	public FeatureOrderedLayout() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("OrderedLayout component");
		OrderedLayout ol = new OrderedLayout();
		for (int i=1;i<5; i++) ol.addComponent(new TextField("Test component "+i));
		show.addComponent(ol);
		l.addComponent(show);

		// Properties
		PropertyPanel p = new PropertyPanel(ol);
		Form ap = p.createBeanPropertySet(new String[] { "orientation" });
		ap.replaceWithSelect(
			"orientation",
			new Object[] {
				new Integer(OrderedLayout.ORIENTATION_HORIZONTAL),
				new Integer(OrderedLayout.ORIENTATION_VERTICAL)},
			new Object[] {
				"Horizontal",
				"Vertical"});
		Select themes = (Select) p.getField("style");
		themes
			.addItem("form")
			.getItemProperty(themes.getItemCaptionPropertyId())
			.setValue("form");
		p.addProperties("OrderedLayout Properties", ap);
		l.addComponent(p);

		return l;
	}

	protected String getExampleSrc() {
		return "OrderedLayout ol = new OrderedLayout(OrderedLayout.ORIENTATION_FLOW);\n"
			+ "ol.addComponent(new TextField(\"Textfield caption\"));\n"
			+ "ol.addComponent(new Label(\"Label\"));\n";

	}
	/**
	 * @see com.enably.tk.demo.features.Feature#getDescriptionXHTML()
	 */
	protected String getDescriptionXHTML() {
		return "This feature provides a container for laying out components either "
			+ "vertically, horizontally or flowingly. The orientation may be changed "
			+ "during runtime. It also defines a special style for themes to implement called \"form\""
			+ "that is used for input forms where the components are layed-out side-by-side "
			+ "with their captions."
			+ "<br/><br/>"
			+ "On the demo tab you can try out how the different properties "
			+ "affect the presentation of the component.";
	}

	protected String getImage() {
		return "orderedlayout.jpg";
	}

	protected String getTitle() {
		return "OrderedLayout";
	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */