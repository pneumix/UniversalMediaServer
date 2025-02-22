/*
 * This file is part of Universal Media Server, based on PS3 Media Server.
 *
 * This program is a free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; version 2 of the License only.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package net.pms.network.webinterfaceserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import net.pms.renderers.devices.WebRender;
import net.pms.dlna.RootFolder;
import net.pms.network.webinterfaceserver.WebInterfaceServerUtil;
import net.pms.network.webinterfaceserver.WebInterfaceServerHttpServerInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PollHandler implements HttpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(PollHandler.class);
	@SuppressWarnings("unused")
	private static final String CRLF = "\r\n";

	private final WebInterfaceServerHttpServerInterface parent;

	public PollHandler(WebInterfaceServerHttpServerInterface parent) {
		this.parent = parent;
	}

	@Override
	public void handle(HttpExchange t) throws IOException {
		try {
			if (WebInterfaceServerUtil.deny(t)) {
				throw new IOException("Access denied");
			}
			if (LOGGER.isTraceEnabled()) {
				WebInterfaceServerUtil.logMessageReceived(t, "");
			}
			RootFolder root = parent.getRoot(WebInterfaceServerUtil.userName(t), t);
			WebRender renderer = (WebRender) root.getDefaultRenderer();
			String json = renderer.getPushData();
			WebInterfaceServerUtil.respond(t, json, 200, "application/json");
		} catch (IOException e) {
			throw e;
		} catch (InterruptedException e) {
			// This can happen if a browser is left open and our cookie changed, or something like that
			// I'm just leaving this note here as a clue for the next person who encounters this
			LOGGER.error("Unexpected error on web player server. Please try closing any tabs or windows that contain UMS and try again");
			LOGGER.debug("", e);
		}
	}
}
