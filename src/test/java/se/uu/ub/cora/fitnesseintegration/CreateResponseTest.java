/*
 * Copyright 2020 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.fitnesseintegration;

import static org.testng.Assert.assertSame;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.testng.annotations.Test;

public class CreateResponseTest {

	@Test
	public void testCreateResponse() {
		StatusType statusType = Response.Status.fromStatusCode(200);
		String responseText = "some response text";
		ReadResponse readResponse = new ReadResponse(statusType, responseText);
		String createdId = "someCreatedId";
		String token = "someToken";
		CreateResponse createReseponse = new CreateResponse(readResponse, createdId, token);

		assertSame(createReseponse.statusType, statusType);
		assertSame(createReseponse.responseText, responseText);
		assertSame(createReseponse.createdId, createdId);
		assertSame(createReseponse.token, token);

	}
}
