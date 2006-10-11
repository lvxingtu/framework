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

import java.net.MalformedURLException;
import java.net.URL;

import com.enably.tk.terminal.ExternalResource;
import com.enably.tk.ui.*;

public class FeatureServerEvents extends Feature {

	protected String getTitle() {
		return "Server Events";
	}

	protected Component getDemoComponent() {
		OrderedLayout l = new OrderedLayout();

		l.addComponent(
			new Label(
				"<h3>Multiplayer GO-Game</h3><p>For demonstration, see GO-Game example application. The application implements a "
					+ "multi-player board game, where the moved by one player are immediately reflected to "
					+ "another player.</p>"
					+ "<p>Updating another players screen is totally automatic, and the programmed "
					+ "does not need to be avare when the refresh requests are sent to screen by the server. In "
					+ "web adapter the requests are passed through open HTTP-connection as java-script fragments "
					+ "that update the windows that need repainting.</p>",
				Label.CONTENT_UIDL));

		URL goUrl = null;
		try {
			goUrl = new URL(getApplication().getURL(), "../go/");
		} catch (MalformedURLException e) {
		}

		if (goUrl != null) {
			Link link = new Link("Start GO-Game", new ExternalResource(goUrl));
			link.setTargetName("gogame");
			link.setTargetBorder(Link.TARGET_BORDER_NONE);
			l.addComponent(link);
		}

		l.addComponent(
			new Label(
				"<h3>Chat example</h3><p>For some purposes it might be better to create your own "+
				"stream. The easiest way of creating a continuous stream for "+
				"simple purposes is to use StreamResource-class. See chat "+
				"example below, how this technique can be used for creation "+
				"of simple chat program.</p>",
				Label.CONTENT_UIDL));

		URL chatUrl = null;
		try {
			chatUrl = new URL(getApplication().getURL(), "../chat/");
		} catch (MalformedURLException e) {
		}

		if (goUrl != null) {
			Link link = new Link("Start chat", new ExternalResource(chatUrl));
			link.setTargetName("chat");
			link.setTargetBorder(Link.TARGET_BORDER_NONE);
			l.addComponent(link);
		}

		return l;
	}

	protected String getDescriptionXHTML() {
		return "<p>Millstone component framework supports both transactional and "
			+ "continuous terminals. This means that either the events from the "
			+ "terminal are sent together as transactions or the events are "
			+ "passed immediately when the user initiates them through the user "
			+ "interface. </p>"
			+ "<p>WebAdapter converts the Millstone applications to web-environment "
			+ "by transferring the events as HTTP-parameters and drawing the "
			+ "pages after the components have received and handled the events. "
			+ "In the web-environment the web browser is always the active party that "
			+ "starts the transaction. This is problematic when the server should "
			+ "notify the user about changes without HTTP-request initiated by the "
			+ "user.</p>"
			+ "<h3>WebAdapter Solution</h3>"
			+ "<p>Millstone solves the problem by combining component frameworks "
			+ "ability to automatically notify terminal adapter about all visual "
			+ "changes to HTTP-protocols ability to handle very long and slow "
			+ "page downloads. WebAdapter provides the web browser with "
			+ "possibility to keep special server command stream open all the time. "
			+ "All the visual updates that happen in components causes the "
			+ "WebAdapter to automatically creates JavaScript that updates the "
			+ "corresponding web browser window and to send the script immediately "
			+ "to browser for execution.</p>"
			+ "<p>The mechanism is fairly complicated, but using the mechanism in "
			+ "any Millstone application is trivial. Application just needs "
			+ "to make sure that WebBrowser opens a hidden iframe to location: "
			+ "<code>?SERVER_COMMANDS=1</code>.</p>"
			+ "<p>See the example of the usage on Demo-tab to get better understanding "
			+ "of the mechanism. If you read the example's source code, you will notice that "
			+ "the program does not contain any support for sending events to client, as "
			+ "it is completely automatic.</p>";
	}

	protected String getImage() {
		return "serverevents.jpg";
	}

}
